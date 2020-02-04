package com.hyphenate.chatuidemo.section.friends.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.hyphenate.chatuidemo.common.livedatas.MessageChangeLiveData;
import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMContactManagerRepository;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

public class FriendsViewModel extends AndroidViewModel {
    private EMContactManagerRepository mRepository;
    private SingleSourceLiveData<Resource<List<EaseUser>>> contactObservable;
    private MediatorLiveData<Resource<List<EaseUser>>> blackObservable;
    private SingleSourceLiveData<Resource<Boolean>> blackResultObservable;
    private SingleSourceLiveData<Resource<Boolean>> deleteObservable;

    public FriendsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMContactManagerRepository();
        contactObservable = new SingleSourceLiveData<>();
        blackObservable = new MediatorLiveData<>();
        blackResultObservable = new SingleSourceLiveData<>();
        deleteObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<List<EaseUser>>> blackObservable() {
        return blackObservable;
    }

    public MessageChangeLiveData messageChangeObservable() {
        return MessageChangeLiveData.getInstance();
    }

    public void getBlackList() {
        blackObservable.addSource(mRepository.getBlackContactList(), result -> blackObservable.postValue(result));
    }

    public void loadContactList() {
        contactObservable.setSource(mRepository.getContactList());
    }

    public LiveData<Resource<List<EaseUser>>> getContactObservable() {
        return contactObservable;
    }

    public LiveData<Resource<Boolean>> resultObservable() {
        return blackResultObservable;
    }

    public LiveData<Resource<Boolean>> deleteObservable() {
        return deleteObservable;
    }

    public void deleteContact(String username) {
        deleteObservable.setSource(mRepository.deleteContact(username));
    }

    public void addUserToBlackList(String username, boolean both) {
        blackResultObservable.setSource(mRepository.addUserToBlackList(username, both));
    }

}
