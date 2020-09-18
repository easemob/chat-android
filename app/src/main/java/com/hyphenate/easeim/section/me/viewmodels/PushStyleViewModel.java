package com.hyphenate.easeim.section.me.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMPushManager;
import com.hyphenate.easeim.common.livedatas.SingleSourceLiveData;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.repositories.EMPushManagerRepository;

public class PushStyleViewModel extends AndroidViewModel {
    private EMPushManagerRepository repository;
    private SingleSourceLiveData<Resource<Boolean>> pushStyleObservable;

    public PushStyleViewModel(@NonNull Application application) {
        super(application);
        repository = new EMPushManagerRepository();
        pushStyleObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<Boolean>> getPushStyleObservable() {
        return pushStyleObservable;
    }

    public void updateStyle(EMPushManager.DisplayStyle style) {
        pushStyleObservable.setSource(repository.updatePushStyle(style));
    }
}

