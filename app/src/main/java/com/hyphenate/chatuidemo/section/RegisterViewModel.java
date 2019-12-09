package com.hyphenate.chatuidemo.section;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chatuidemo.common.Resource;
import com.hyphenate.chatuidemo.common.SingleSourceLiveData;
import com.hyphenate.chatuidemo.repositories.EMClientRepository;

public class RegisterViewModel extends AndroidViewModel {
    private EMClientRepository mRepository;
    private SingleSourceLiveData<Resource<Boolean>> registerObservable;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMClientRepository();
        registerObservable = new SingleSourceLiveData<>();
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

    public LiveData<Resource<Boolean>> getRegisterObservable() {
        return registerObservable;
    }
}
