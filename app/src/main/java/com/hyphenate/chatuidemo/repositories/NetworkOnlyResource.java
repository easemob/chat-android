package com.hyphenate.chatuidemo.repositories;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.hyphenate.chatuidemo.core.net.EmErrorCode;
import com.hyphenate.chatuidemo.core.net.EmResult;
import com.hyphenate.chatuidemo.core.net.Resource;
import com.hyphenate.chatuidemo.core.utils.ThreadManager;
import com.hyphenate.chatuidemo.core.interfaceOrImplement.EmResultCallBack;
import com.hyphenate.chatuidemo.core.utils.EmLog;

/**
 * 此类用于从环信SDK拉取异步数据或者其他耗时操作
 * @param <ResultType>
 */
public abstract class NetworkOnlyResource<ResultType> {
    private static final String TAG = "NetworkBoundResource";
    private ThreadManager mThreadManager;
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    public NetworkOnlyResource() {
        mThreadManager = ThreadManager.getInstance();
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
        fetchFromNetwork();
    }

    /**
     * work on main thread
     */
    private void fetchFromNetwork() {
        createCall(new EmResultCallBack<LiveData<ResultType>>() {
            @Override
            public void onSuccess(LiveData<ResultType> apiResponse) {
                // 保证回调后在主线程
                mThreadManager.runOnMainThread(() -> {
                    result.addSource(apiResponse, response-> {
                        result.removeSource(apiResponse);
                        if(response != null) {
                            // 如果结果是EmResult结构，需要判断code，是否请求成功
                            if(response instanceof EmResult) {
                                int code = ((EmResult) response).code;
                                if(code != EmErrorCode.EM_NO_ERROR) {
                                    fetchFailed(code, null);
                                }
                            }
                            // 在异步线程中处理保存的逻辑
                            mThreadManager.runOnIOThread(() -> {
                                try {
                                    saveCallResult(processResponse(response));
                                } catch (Exception e) {
                                    EmLog.e(TAG, "save call result failed: " + e.toString());
                                }
                                result.postValue(Resource.success(response));
                            });

                        }else {
                            fetchFailed(EmErrorCode.EM_ERR_UNKNOWN, null);
                        }
                    });
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                mThreadManager.runOnMainThread(() -> {
                    fetchFailed(error, errorMsg);
                });
            }
        });


    }

    @MainThread
    private void fetchFailed(int code, String message) {
        onFetchFailed();
        result.setValue(Resource.error(code, message,null));
    }

    /**
     * Called to save the result of the API response into the database
     * @param item
     */
    @WorkerThread
    protected void saveCallResult(ResultType item){ }

    /**
     * Process request response
     * @param response
     * @return
     */
    @WorkerThread
    protected ResultType processResponse(ResultType response) {
        return response;
    }

    /**
     * 此处设计为回调模式，方便在此方法中进行异步操作
     * @return
     */
    @MainThread
    protected abstract void createCall(@NonNull EmResultCallBack<LiveData<ResultType>> callBack);

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
