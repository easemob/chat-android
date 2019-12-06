package com.hyphenate.chatuidemo.common;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.hyphenate.util.EMLog;

public abstract class NetworkOnlyResource<RequestType> {
    private final ThreadManager mThreadManager;
    private final MediatorLiveData<ApiResponse<RequestType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkOnlyResource() {
        mThreadManager = ThreadManager.getInstance();
        if(mThreadManager.isMainThread()) {
            init();
        }else {
            mThreadManager.runOnMainThread(this::init);
        }
    }

    private void init() {
        result.setValue(ApiResponse.loading(null));
        if(workOnUIThread()) {
            fetchFromNetwork();
        }else {
            mThreadManager.runOnIOThread(this::fetchFromNetwork);
        }
    }

    private void fetchFromNetwork() {
        LiveData<RequestType> apiResponse = createCall();
        //先从数据库中获取数据
        result.addSource(apiResponse, response -> {
            // 当网络请求有结果是移除数据库和网络请求的数据
            result.removeSource(apiResponse);

            if(response != null) {
                // TODO: 19-12-6 修改此处逻辑，不一定非要是这个类
                if(response instanceof Result) {
                    int code = ((Result)response).code;
                    if(code != ErrorCode.EM_NO_ERROR) {
                        fetchFailed(code);
                    }else {
                        mThreadManager.runOnIOThread(()->{
                            try {
                                saveCallRequest(response);
                            } catch (Exception e) {
                                EMLog.e("", "saveCallRequest failed:" + e.toString());
                            }
                            Log.e("TAG", "postValue之前");
                            result.postValue(ApiResponse.success(response));
                        });
                    }
                }else {
                    fetchFailed(ErrorCode.EM_PARSE_ERROR);
                }
            }else {
                fetchFailed(ErrorCode.EM_ERR_OTHER);
            }
        });
    }

    // TODO: 19-12-6 添加相关的错误信息
    private void fetchFailed(int errorCode) {
        mThreadManager.runOnMainThread(()->{
            onFetchFailed();
            result.setValue(ApiResponse.error(errorCode, null));
        });
    }

    /**
     * 是否在UI线程
     * @return
     */
    protected boolean workOnUIThread() {
        return true;
    }

    /**
     * 返回结果数据
     * @return
     */
    public LiveData<ApiResponse<RequestType>> asLiveData() {
        return result;
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
     * 保存网络请求结果
     *
     * @param result
     */
    @WorkerThread
    protected void saveCallRequest(@NonNull RequestType result){}
}
