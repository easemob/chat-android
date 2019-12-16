package com.hyphenate.chatuidemo.section.login.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chatuidemo.common.model.EaseUser;
import com.hyphenate.chatuidemo.common.livedatas.UserInstanceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.repositories.EMClientRepository;


public class LoginViewModel extends AndroidViewModel {
    private EMClientRepository mRepository;
    private UserInstanceLiveData loginObservable;
    private SingleSourceLiveData<Resource<String>> registerObservable;
    private SingleSourceLiveData<Integer> pageObservable;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMClientRepository();
        registerObservable = new SingleSourceLiveData<>();
        loginObservable = UserInstanceLiveData.getInstance();
        pageObservable = new SingleSourceLiveData<>();
    }

    /**
     * 获取页面跳转的observable
     * @return
     */
    public LiveData<Integer> getPageSelect() {
        return pageObservable;
    }

    /**
     * 设置跳转的页面
     * @param page
     */
    public void setPageSelect(int page) {
        pageObservable.setValue(page);
    }

    /**
     * 注册环信账号
     * @param userName
     * @param pwd
     * @return
     */
    public void register(String userName, String pwd) {
        registerObservable.setSource(mRepository.registerToHx(userName, pwd));
    }

    public LiveData<Resource<String>> getRegisterObservable() {
        return registerObservable;
    }

    /**
     * 清理注册信息
     */
    public void clearRegisterInfo() {
        registerObservable.setValue(null);
    }

    /**
     * 登录环信
     * @param userName
     * @param pwd
     * @param isTokenFlag
     */
    public void login(String userName, String pwd, boolean isTokenFlag) {
        loginObservable.setSource(mRepository.loginToServer(userName, pwd, isTokenFlag));
    }

    public LiveData<Resource<EaseUser>> getLoginObservable() {
        return loginObservable;
    }




}
