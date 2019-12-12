package com.hyphenate.chatuidemo.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.BasicApplication;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.core.bean.EaseUser;
import com.hyphenate.chatuidemo.core.net.EmErrorCode;
import com.hyphenate.chatuidemo.core.net.Resource;
import com.hyphenate.chatuidemo.core.utils.ThreadManager;
import com.hyphenate.chatuidemo.core.interfaceOrImplement.EmResultCallBack;
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
                if(DemoHelper.getInstance().getAutoLogin()) {
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
                }else {
                    callBack.onError(EmErrorCode.EM_NOT_LOGIN);
                }

            }
        }.asLiveData();
    }

    public LiveData<Resource<String>> registerToHx(String userName, String pwd) {
        return new NetworkOnlyResource<String>() {

            @Override
            protected void createCall(@NonNull EmResultCallBack<LiveData<String>> callBack) {
                ThreadManager.getInstance().runOnIOThread(() -> {
                    try {
                        EMClient.getInstance().createAccount(userName, pwd);
                        MutableLiveData<String> observable = new MutableLiveData<>(userName);
                        callBack.onSuccess(observable);
                    } catch (HyphenateException e) {
                        callBack.onError(e.getErrorCode(), e.getMessage());
                    }
                });
            }

        }.asLiveData();
    }

    public LiveData<Resource<EaseUser>> loginToServer(String userName, String pwd) {
        return new NetworkOnlyResource<EaseUser>() {

            @Override
            protected void createCall(@NonNull EmResultCallBack<LiveData<EaseUser>> callBack) {
                DemoHelper.getInstance().init(BasicApplication.getInstance());
                DemoHelper.getInstance().setCurrentUserName(userName);
                EMClient.getInstance().login(userName, pwd, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        // ** manually load all local groups and conversation
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        // get current user id
                        String currentUser = EMClient.getInstance().getCurrentUser();
                        EaseUser user = new EaseUser(currentUser);
                        callBack.onSuccess(new MutableLiveData<>(user));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }

        }.asLiveData();
    }
}
