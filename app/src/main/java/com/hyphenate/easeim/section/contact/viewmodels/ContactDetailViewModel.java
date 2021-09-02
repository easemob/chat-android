package com.hyphenate.easeim.section.contact.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.easeim.common.livedatas.SingleSourceLiveData;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.repositories.EMContactManagerRepository;
import com.hyphenate.easeui.domain.EaseUser;

public class ContactDetailViewModel extends AndroidViewModel {
    private EMContactManagerRepository repository;
    private SingleSourceLiveData<Resource<Boolean>> deleteObservable;
    private SingleSourceLiveData<Resource<Boolean>> blackObservable;
    private SingleSourceLiveData<Resource<EaseUser>> userInfoObservable;

    public ContactDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new EMContactManagerRepository();
        deleteObservable = new SingleSourceLiveData<>();
        blackObservable = new SingleSourceLiveData<>();
        userInfoObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<Boolean>> deleteObservable() {
        return deleteObservable;
    }

    public LiveData<Resource<Boolean>> blackObservable() {
        return blackObservable;
    }

    public LiveData<Resource<EaseUser>> userInfoObservable() {
        return userInfoObservable;
    }

    public void deleteContact(String username) {
        deleteObservable.setSource(repository.deleteContact(username));
    }

    public void addUserToBlackList(String username, boolean both) {
        blackObservable.setSource(repository.addUserToBlackList(username, both));
    }

    public void getUserInfoById(String username, boolean mIsFriend) {
        userInfoObservable.setSource(repository.getUserInfoById(username,mIsFriend));
    }

}
