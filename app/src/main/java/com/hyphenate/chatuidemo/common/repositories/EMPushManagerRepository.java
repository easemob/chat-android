package com.hyphenate.chatuidemo.common.repositories;

import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class EMPushManagerRepository extends BaseEMRepository {

    public LiveData<Resource<EMPushConfigs>> getPushConfigsFromServer() {
        return new NetworkOnlyResource<EMPushConfigs>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMPushConfigs>> callBack) {
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
        }.asLiveData();
    }
}
