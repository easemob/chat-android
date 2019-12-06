package com.hyphenate.chatuidemo.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.common.ApiResponse;
import com.hyphenate.chatuidemo.common.ErrorCode;
import com.hyphenate.chatuidemo.common.NetworkOnlyResource;
import com.hyphenate.chatuidemo.common.Result;
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
    public LiveData<ApiResponse<Result<Boolean>>> loadAllInfoFromHX() {
        return new NetworkOnlyResource<Result<Boolean>>() {

            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                MutableLiveData<Result<Boolean>> observable = new MutableLiveData<>();
                if(isLoggedIn()) {
                    EMClient.getInstance().chatManager().loadAllConversations();
                    EMClient.getInstance().groupManager().loadAllGroups();
                    observable.postValue(new Result<>(ErrorCode.EM_NO_ERROR, true));
                }else {
                    observable.postValue(new Result<>(ErrorCode.EM_NO_ERROR, false));
                }
                return observable;
            }

            @Override
            protected boolean workOnUIThread() {
                return false;
            }
        }.asLiveData();
    }

    public LiveData<ApiResponse<Result<Boolean>>> registerToHx(String userName, String pwd) {
        return new NetworkOnlyResource<Result<Boolean>>() {

            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                MutableLiveData<Result<Boolean>> observable = new MutableLiveData<>();
                try {
                    EMClient.getInstance().createAccount(userName, pwd);
                    observable.postValue(new Result<>(ErrorCode.EM_NO_ERROR, true));
                } catch (HyphenateException e) {
                    observable.postValue(new Result<>(e.getErrorCode(), false));
                }
                return observable;
            }

        }.asLiveData();
    }
}
