package com.hyphenate.chatdemo.section.login.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chatdemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatdemo.common.net.Resource;
import com.hyphenate.chatdemo.common.repositories.EMClientRepository;

public class ChangePwdViewModel extends AndroidViewModel {
    private EMClientRepository mRepository;
    private SingleSourceLiveData<Resource<Boolean>> changeObservable;
    private SingleSourceLiveData<Resource<Boolean>> checkObservable;

    public ChangePwdViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMClientRepository();
        changeObservable = new SingleSourceLiveData<>();
        checkObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<Boolean>> getChangeObservable(){
        return changeObservable;
    }

    public LiveData<Resource<Boolean>> getCheckObservable(){
        return checkObservable;
    }


    public void changePasswordFromAppServe(String userName,String newPassword){
        changeObservable.setSource(mRepository.changePwdFromServe(userName,newPassword));
    }

    public void checkObservable(String userName,String phoneNumber,String smsCode){
        checkObservable.setSource(mRepository.checkIdentity(userName,phoneNumber,smsCode));
    }

    /**
     * 清理注册信息
     */
    public void clearRegisterInfo() {
        changeObservable.setValue(null);
        checkObservable.setValue(null);
    }
}
