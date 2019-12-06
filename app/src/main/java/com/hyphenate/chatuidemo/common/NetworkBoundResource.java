package com.hyphenate.chatuidemo.common;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.util.EMLog;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final ThreadManager mThreadManager;
    private final MediatorLiveData<ApiResponse<ResultType>> result = new MediatorLiveData<>();

    public NetworkBoundResource() {
        mThreadManager = ThreadManager.getInstance();
        if(mThreadManager.isMainThread()) {
            init();
        }else {
            mThreadManager.runOnMainThread(()-> init());
        }
    }

    private void init() {
        result.setValue(ApiResponse.loading(null));
        LiveData<ResultType> dbSource = safeLoadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if(shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            }else {
                result.addSource(dbSource, newData -> setValue(ApiResponse.success(newData)));
            }
        });
    }

    private void fetchFromNetwork(LiveData<ResultType> dbSource) {
        LiveData<RequestType> apiResponse = createCall();
        //先从数据库中获取数据
        result.addSource(dbSource, newData -> setValue(ApiResponse.loading(newData)));
        result.addSource(apiResponse, response -> {
            // 当网络请求有结果是移除数据库和网络请求的数据
            result.removeSource(apiResponse);
            result.removeSource(dbSource);

            if(response != null) {
                if(response instanceof ApiResponse) {
                    int code = ((ApiResponse)response).code;
                    if(code != ErrorCode.EM_NO_ERROR) {
                        onFetchFailed();
                        result.addSource(dbSource, newData -> setValue(ApiResponse.error(code, newData)));
                    }else {
                        mThreadManager.runOnIOThread(()->{
                            try {
                                saveCallRequest(processResponse(response));
                            } catch (Exception e) {
                                EMLog.e("", "saveCallRequest failed:" + e.toString());
                            }
                            //重新从数据库拉取数据，保证页面是最新数据
                            mThreadManager.runOnMainThread(()-> result.addSource(safeLoadFromDb()
                                    , newData -> setValue(ApiResponse.success(newData))));
                        });
                    }
                }else {
                    onFetchFailed();
                    result.addSource(dbSource, newData -> setValue(ApiResponse.error(ErrorCode.EM_PARSE_ERROR, newData)));
                }
            }else {
                onFetchFailed();
                result.addSource(dbSource, newData -> setValue(ApiResponse.error(ErrorCode.EM_ERR_OTHER, newData)));
            }
        });
    }

    @MainThread
    private void setValue(ApiResponse<ResultType> newValue) {
        if(result.getValue() != newValue) {
            result.setValue(newValue);
        }
    }

    private LiveData<ResultType> safeLoadFromDb() {
        LiveData<ResultType> dbSource;
        try {
            dbSource = loadFromDb();
        } catch (Exception e) {
            EMLog.e("", "loadFromDb failed:" + e.toString());
            dbSource = new MutableLiveData<>(null);
        }
        return dbSource;
    }

    /**
     * 返回结果数据
     * @return
     */
    public LiveData<ApiResponse<ResultType>> asLiveData() {
        return result;
    }

    protected RequestType processResponse(RequestType response) {
        return response;
    }

    /**
     * 是否拉取数据
     * @param data
     * @return
     */
    protected boolean shouldFetch(ResultType data) {
        return true;
    }

    /**
     * fetch error
     */
    protected void onFetchFailed(){}

    /**
     * 创建网络请求
     * @return
     */
    @NonNull
    @MainThread
    protected abstract LiveData<RequestType> createCall();

    /**
     * 从数据库中获取数据
     * @return
     */
    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    /**
     * 保存网络请求结果
     *
     * @param result
     */
    @WorkerThread
    protected abstract void saveCallRequest(@NonNull RequestType result);
}
