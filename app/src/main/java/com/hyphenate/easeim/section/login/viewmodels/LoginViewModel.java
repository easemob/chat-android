package com.hyphenate.easeim.section.login.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.livedatas.SingleSourceLiveData;
import com.hyphenate.easeim.common.repositories.EMClientRepository;


public class LoginViewModel extends AndroidViewModel {
    private EMClientRepository mRepository;
    private SingleSourceLiveData<Resource<String>> registerObservable;
    private SingleSourceLiveData<Integer> pageObservable;
    private SingleSourceLiveData<Resource<Boolean>> verificationCodeObservable;
    private SingleSourceLiveData<Resource<String>> imgVerificationCodeObservable;
    private SingleSourceLiveData<Resource<Boolean>> registerFromAppServeObservable;


    public LoginViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMClientRepository();
        registerObservable = new SingleSourceLiveData<>();
        pageObservable = new SingleSourceLiveData<>();
        verificationCodeObservable =  new SingleSourceLiveData<>();
        registerFromAppServeObservable = new SingleSourceLiveData<>();
        imgVerificationCodeObservable = new SingleSourceLiveData<>();
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

    public LiveData<Resource<Boolean>> getVerificationCodeObservable(){
        return verificationCodeObservable;
    }

    public LiveData<Resource<String>> getImgVerificationCodeObservable(){
        return imgVerificationCodeObservable;
    }

    public LiveData<Resource<Boolean>> getRegisterFromAppServeObservable(){
        return registerFromAppServeObservable;
    }


    /**
     * 清理注册信息
     */
    public void clearRegisterInfo() {
        registerObservable.setValue(null);
    }

    /**
     * 获取短信验证码
     */
    public void postVerificationCode(String phoneNumber,String image_id,String imageCode){
        verificationCodeObservable.setSource(mRepository.getVerificationCode(phoneNumber,image_id,imageCode));
    }

    /**
     * 获取图片验证码
     */
    public void getImageVerificationCode(){
        imgVerificationCodeObservable.setSource(mRepository.getImgVerificationCode());
    }

    /**
     * 通过AppServe注册
     * @param userName
     * @param userPassword
     * @param phoneNumber
     * @param smsCode
     * @param imageId
     * @param imageCode
     */
    public void registerFromAppServe(String userName, String userPassword,String phoneNumber,String smsCode,String imageId,String imageCode) {
        registerFromAppServeObservable.setSource(mRepository.registerFromServe(userName,userPassword,phoneNumber,smsCode,imageId,imageCode));
    }


}
