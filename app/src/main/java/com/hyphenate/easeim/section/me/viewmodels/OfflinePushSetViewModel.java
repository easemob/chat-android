package com.hyphenate.easeim.section.me.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.easeim.common.livedatas.SingleSourceLiveData;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.repositories.EMPushManagerRepository;

public class OfflinePushSetViewModel extends AndroidViewModel {
    private EMPushManagerRepository repository;
    private SingleSourceLiveData<Resource<EMPushConfigs>> configsObservable;
    private SingleSourceLiveData<Resource<Boolean>> disableObservable;
    private SingleSourceLiveData<Resource<Boolean>> enableObservable;
    private SingleSourceLiveData<Resource<Boolean>> updatePushNicknameObservable;

    public OfflinePushSetViewModel(@NonNull Application application) {
        super(application);
        repository = new EMPushManagerRepository();
        configsObservable = new SingleSourceLiveData<>();
        disableObservable = new SingleSourceLiveData<>();
        enableObservable = new SingleSourceLiveData<>();
        updatePushNicknameObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<EMPushConfigs>> getConfigsObservable() {
        return configsObservable;
    }

    public void getPushConfigs() {
        configsObservable.setSource(repository.getPushConfigsFromServer());
    }

    public LiveData<Resource<Boolean>> getDisableObservable() {
        return disableObservable;
    }

    public void disableOfflinePush(int start, int end) {
        disableObservable.setSource(repository.disableOfflinePush(start, end));
    }

    public LiveData<Resource<Boolean>> getEnableObservable() {
        return enableObservable;
    }

    public void enableOfflinePush() {
        enableObservable.setSource(repository.enableOfflinePush());
    }

    public LiveData<Resource<Boolean>> getUpdatePushNicknameObservable() {
        return updatePushNicknameObservable;
    }

    public void updatePushNickname(String nickname) {
        updatePushNicknameObservable.setSource(repository.updatePushNickname(nickname));
    }
}

