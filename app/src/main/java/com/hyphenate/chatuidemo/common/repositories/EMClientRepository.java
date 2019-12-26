package com.hyphenate.chatuidemo.common.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoApp;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.common.db.DemoDbHelper;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.DemoEmCallBack;
import com.hyphenate.chatuidemo.common.livedatas.UserInstanceLiveData;
import com.hyphenate.chatuidemo.common.net.ErrorCode;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.easeui.domain.EaseUser;
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
            protected void createCall(ResultCallBack<LiveData<Boolean>> callBack) {
                if(DemoHelper.getInstance().getAutoLogin()) {
                    ThreadManager.getInstance().runOnIOThread(() -> {
                        if(isLoggedIn()) {
                            loadAllConversationsAndGroups();
                            MutableLiveData<Boolean> observable = new MutableLiveData<>(true);
                            callBack.onSuccess(observable);
                        }else {
                            callBack.onError(ErrorCode.EM_NOT_LOGIN);
                        }

                    });
                }else {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN);
                }

            }
        }.asLiveData();
    }

    /**
     * 从本地数据库加载所有的对话及群组
     */
    private void loadAllConversationsAndGroups() {
        // 初始化数据库
        DemoDbHelper.getInstance(DemoApp.getInstance()).initDb(EMClient.getInstance().getCurrentUser());
        // 从本地数据库加载所有的对话及群组
        EMClient.getInstance().chatManager().loadAllConversations();
        EMClient.getInstance().groupManager().loadAllGroups();
    }

    /**
     * 注册
     * @param userName
     * @param pwd
     * @return
     */
    public LiveData<Resource<String>> registerToHx(String userName, String pwd) {
        return new NetworkOnlyResource<String>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                //注册之前先判断SDK是否已经初始化，如果没有先进行SDK的初始化
                if(!DemoHelper.getInstance().isSDKInit) {
                    DemoHelper.getInstance().init(DemoApp.getInstance());
                    DemoHelper.getInstance().setCurrentUserName(userName);
                }
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

    /**
     * 登录到服务器，可选择密码登录或者token登录
     * @param userName
     * @param pwd
     * @param isTokenFlag
     * @return
     */
    public LiveData<Resource<EaseUser>> loginToServer(String userName, String pwd, boolean isTokenFlag) {
        return new NetworkOnlyResource<EaseUser>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EaseUser>> callBack) {
                DemoHelper.getInstance().init(DemoApp.getInstance());
                DemoHelper.getInstance().setCurrentUserName(userName);
                if(isTokenFlag) {
                    EMClient.getInstance().loginWithToken(userName, pwd, new DemoEmCallBack() {
                        @Override
                        public void onSuccess() {
                            successForCallBack(callBack);
                        }

                        @Override
                        public void onError(int code, String error) {
                            callBack.onError(code, error);
                        }
                    });
                }else {
                    EMClient.getInstance().login(userName, pwd, new DemoEmCallBack() {
                        @Override
                        public void onSuccess() {
                            successForCallBack(callBack);
                        }

                        @Override
                        public void onError(int code, String error) {
                            callBack.onError(code, error);
                        }
                    });
                }

            }

        }.asLiveData();
    }

    private void successForCallBack(@NonNull ResultCallBack<LiveData<EaseUser>> callBack) {
        // ** manually load all local groups and conversation
        loadAllConversationsAndGroups();
        // get current user id
        String currentUser = EMClient.getInstance().getCurrentUser();
        EaseUser user = new EaseUser(currentUser);
        UserInstanceLiveData.getInstance().postValue(user);
        callBack.onSuccess(new MutableLiveData<>(user));
    }
}
