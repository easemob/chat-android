package com.hyphenate.chatuidemo.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.common.EmErrorCode;
import com.hyphenate.chatuidemo.common.Resource;
import com.hyphenate.chatuidemo.common.ThreadManager;
import com.hyphenate.chatuidemo.core.EmResultCallBack;
import com.hyphenate.exceptions.HyphenateException;

/**
 * 作为EMClient的repository,处理EMClient相关的逻辑
 */
public class EMClientRepository {

    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * 登录过后需要加载的数据
     * @return
     */
    public LiveData<Resource<Boolean>> loadAllInfoFromHX() {
        return new NetworkOnlyResource<Boolean>() {

            @Override
            protected void createCall(EmResultCallBack<LiveData<Boolean>> callBack) {
                ThreadManager.getInstance().runOnIOThread(() -> {
                    if(isLoggedIn()) {
                        EMClient.getInstance().chatManager().loadAllConversations();
                        EMClient.getInstance().groupManager().loadAllGroups();
                        MutableLiveData<Boolean> observable = new MutableLiveData<>(true);
                        callBack.onSuccess(observable);
                    }else {
                        callBack.onError(EmErrorCode.EM_NOT_LOGIN);
                    }
                });
            }
        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> registerToHx(String userName, String pwd) {
        return new NetworkOnlyResource<Boolean>() {

            @Override
            protected void createCall(@NonNull EmResultCallBack<LiveData<Boolean>> callBack) {
                try {
                    EMClient.getInstance().createAccount(userName, pwd);
                    MutableLiveData<Boolean> observable = new MutableLiveData<>(true);
                    callBack.onSuccess(observable);
                } catch (HyphenateException e) {
                    callBack.onError(e.getErrorCode(), e.getMessage());
                }
            }

        }.asLiveData();
    }

}
