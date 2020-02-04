package com.hyphenate.chatuidemo.section.friends.viewmodels;

import android.app.Application;

import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMContactManagerRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ContactDetailViewModel extends AndroidViewModel {
    private EMContactManagerRepository repository;
    private SingleSourceLiveData<Resource<Boolean>> deleteObservable;
    private SingleSourceLiveData<Resource<Boolean>> blackObservable;

    public ContactDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new EMContactManagerRepository();
        deleteObservable = new SingleSourceLiveData<>();
        blackObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<Boolean>> deleteObservable() {
        return deleteObservable;
    }

    public LiveData<Resource<Boolean>> blackObservable() {
        return blackObservable;
    }

    public void deleteContact(String username) {
        deleteObservable.setSource(repository.deleteContact(username));
    }

    public void addUserToBlackList(String username, boolean both) {
        blackObservable.setSource(repository.addUserToBlackList(username, both));
    }

}
