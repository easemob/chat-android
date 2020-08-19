package com.hyphenate.chatuidemo.common.repositories;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class EMPushManagerRepository extends BaseEMRepository {

    /**
     * 获取推送配置
     * @return
     */
    public LiveData<Resource<EMPushConfigs>> getPushConfigsFromServer() {
        return new NetworkBoundResource<EMPushConfigs, EMPushConfigs>() {
            @Override
            protected boolean shouldFetch(EMPushConfigs data) {
                return true;
            }

            @Override
            protected LiveData<EMPushConfigs> loadFromDb() {
                return createLiveData(getPushManager().getPushConfigs());
            }

            @Override
            protected void createCall(ResultCallBack<LiveData<EMPushConfigs>> callBack) {
                ThreadManager.getInstance().runOnIOThread(()-> {
                    EMPushConfigs configs = null;
                    try {
                        configs = getPushManager().getPushConfigsFromServer();
                        callBack.onSuccess(createLiveData(configs));
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getMessage());
                    }
                });
            }

            @Override
            protected void saveCallResult(EMPushConfigs item) {

            }
        }.asLiveData();
    }

    /**
     * 设置免打扰时间段
     * 如果end小于start,则end为第二天的hour
     * @param start
     * @param end
     * @return
     */
    public LiveData<Resource<Boolean>> disableOfflinePush(int start, int end) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                ThreadManager.getInstance().runOnIOThread(()-> {
                    try {
                        EMClient.getInstance().pushManager().disableOfflinePush(start, end);
                        callBack.onSuccess(createLiveData(true));
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getDescription());
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 允许离线推送
     * @return
     */
    public LiveData<Resource<Boolean>> enableOfflinePush() {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                ThreadManager.getInstance().runOnIOThread(()-> {
                    try {
                        EMClient.getInstance().pushManager().enableOfflinePush();
                        callBack.onSuccess(createLiveData(true));
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getDescription());
                    }
                });
            }
        }.asLiveData();
    }
}
