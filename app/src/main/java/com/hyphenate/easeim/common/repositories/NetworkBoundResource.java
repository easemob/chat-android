package com.hyphenate.easeim.common.repositories;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.easeim.common.net.ErrorCode;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.net.Result;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeim.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.easeim.common.utils.DemoLog;

/**
 * 作为服务器拉取数据和本地数据融合类
 * @param <ResultType> 本地数据库中拉取的数据
 * @param <RequestType> 服务器中拉取的数据
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {
    private static final String TAG = "NetworkBoundResource";
    private EaseThreadManager mThreadManager;
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
    private LiveData<ResultType> lastFailSource;

    public NetworkBoundResource() {
        mThreadManager = EaseThreadManager.getInstance();
        if(mThreadManager.isMainThread()) {
            init();
        }else {
            mThreadManager.runOnMainThread(this::init);
        }
    }

    /**
     * work on main thread
     */
    private void init() {
        // 通知UI开始加载
        result.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = safeLoadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if(shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            }else {
                result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
            }
        });
    }

    /**
     * work on main thread
     * @param dbSource
     */
    private void fetchFromNetwork(LiveData<ResultType> dbSource) {
        // 先展示数据库中的数据，处理完网络请求数据后，再从数据库中取出一次进行展示
        result.addSource(dbSource, newData-> setValue(Resource.loading(newData)));
        createCall(new ResultCallBack<LiveData<RequestType>>() {
            @Override
            public void onSuccess(LiveData<RequestType> apiResponse) {
                // 保证回调后在主线程
                mThreadManager.runOnMainThread(() -> {
                    result.addSource(apiResponse, response-> {
                        result.removeSource(apiResponse);
                        result.removeSource(dbSource);
                        if(response != null) {
                            // 如果结果是EmResult结构，需要判断code，是否请求成功
                            if(response instanceof Result) {
                                int code = ((Result) response).code;
                                if(code != ErrorCode.EM_NO_ERROR) {
                                    fetchFailed(code, dbSource, null);
                                }
                            }
                            // 在异步线程中处理保存到数据库的逻辑
                            mThreadManager.runOnIOThread(() -> {
                                try {
                                    saveCallResult(processResponse(response));
                                } catch (Exception e) {
                                    DemoLog.e(TAG, "save call result failed: " + e.toString());
                                }
                                //为了获取最新的数据，需要从数据库重新取一次数据，保证页面与数据的一致性
                                mThreadManager.runOnMainThread(() ->
                                        result.addSource(safeLoadFromDb(), newData -> {
                                            setValue(Resource.success(newData));
                                        }));
                            });

                        }else {
                            fetchFailed(ErrorCode.EM_ERR_UNKNOWN, dbSource, null);
                        }
                    });
                });

            }

            @Override
            public void onError(int error, String errorMsg) {
                mThreadManager.runOnMainThread(() -> {
                    fetchFailed(error, dbSource, errorMsg);
                });
            }
        });


    }

    /**
     * 安全从数据库加载数据，如果加载失败，则数据返回null。
     * @return
     */
    private LiveData<ResultType> safeLoadFromDb() {
        LiveData<ResultType> dbSource;
        try {
            dbSource = loadFromDb();
        } catch (Exception e) {
            DemoLog.e(TAG, "safe load from db failed: " + e.toString());
            dbSource = new MutableLiveData<>(null);
        }
        return dbSource;
    }

    @MainThread
    private void fetchFailed(int code, LiveData<ResultType> dbSource, String message) {
        onFetchFailed();
        try {
            result.addSource(dbSource, newData -> setValue(Resource.error(code, message, newData)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if(result.getValue() != newValue) {
            result.setValue(newValue);
        }
    }

    /**
     * Process request response
     * @param response
     * @return
     */
    @WorkerThread
    protected RequestType processResponse(RequestType response) {
        return response;
    }

    /**
     * Called with the data in the database to decide whether to fetch
     * potentially updated data from the network.
     * @param data
     * @return
     */
    @MainThread
    protected abstract boolean shouldFetch(ResultType data);

    /**
     * Called to get the cached data from the database.
     * @return
     */
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    /**
     * 此处设计为回调模式，方便在此方法中进行异步操作
     * @return
     */
    @MainThread
    protected abstract void createCall(ResultCallBack<LiveData<RequestType>> callBack);

    /**
     * Called to save the result of the API response into the database
     * @param item
     */
    @WorkerThread
    protected abstract void saveCallResult(RequestType item);

    /**
     * Called when the fetch fails. The child class may want to reset components like rate limiter.
     */
    protected void onFetchFailed() {}

    /**
     * Returns a LiveData object that represents the resource that's implemented
     * in the base class.
     * @return
     */
    protected LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }
}
