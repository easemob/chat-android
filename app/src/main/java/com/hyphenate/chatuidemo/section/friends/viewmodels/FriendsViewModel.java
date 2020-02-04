package com.hyphenate.chatuidemo.section.friends.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.net.Result;
import com.hyphenate.chatuidemo.common.repositories.EMContactManagerRepository;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

public class FriendsViewModel extends AndroidViewModel {
    private EMContactManagerRepository mRepository;
    private SingleSourceLiveData<Resource<List<EaseUser>>> contactObservable;
    private SingleSourceLiveData<Resource<Boolean>> resultObservable;

    public FriendsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMContactManagerRepository();
        contactObservable = new SingleSourceLiveData<>();
        resultObservable = new SingleSourceLiveData<>();
    }

    public void loadContactList() {
        contactObservable.setSource(mRepository.getContactList());
    }

    public LiveData<Resource<List<EaseUser>>> getContactObservable() {
        return contactObservable;
    }

    public LiveData<Resource<Boolean>> resultObservable() {
        return resultObservable;
    }

    public void deleteContact(String username) {
        resultObservable.setSource(mRepository.deleteContact(username));
    }

    public void addUserToBlackList(String username, boolean both) {
        resultObservable.setSource(mRepository.addUserToBlackList(username, both));
    }

}
