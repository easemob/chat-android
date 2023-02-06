package com.hyphenate.chatdemo.section.login.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.hyphenate.chatdemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatdemo.common.model.LoginResult;
import com.hyphenate.chatdemo.common.net.Resource;
import com.hyphenate.chatdemo.common.repositories.EMClientRepository;
import com.hyphenate.easeui.domain.EaseUser;

public class LoginFragmentViewModel extends AndroidViewModel {
    private EMClientRepository mRepository;
    private MediatorLiveData<Resource<EaseUser>> loginObservable;
    private SingleSourceLiveData<Resource<LoginResult>> loginFromAppServeObservable;

    public LoginFragmentViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMClientRepository();
        loginObservable = new MediatorLiveData<>();
        loginFromAppServeObservable = new SingleSourceLiveData<>();
    }

    /**
     * 登录环信
     * @param userName
     * @param pwd
     * @param isTokenFlag
     */
    public void login(String userName, String pwd, boolean isTokenFlag) {

        loginObservable.addSource(mRepository.loginToServer(userName, pwd, isTokenFlag), response -> {
            loginObservable.setValue(response);
        });
    }

    public LiveData<Resource<EaseUser>> getLoginObservable() {
        return loginObservable;
    }

    public LiveData<Resource<LoginResult>> getLoginFromAppServeObservable(){
        return loginFromAppServeObservable;
    }

    /**
     * 通过AppServe授权登录
     * @param userName
     * @param userPassword
     */
    public void loginFromAppServe(String userName,String userPassword){
        loginFromAppServeObservable.setSource(mRepository.loginFromServe(userName,userPassword));
    }
}
