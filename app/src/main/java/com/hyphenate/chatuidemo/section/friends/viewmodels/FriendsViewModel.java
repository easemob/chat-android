package com.hyphenate.chatuidemo.section.friends.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMContactManagerRepository;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

public class FriendsViewModel extends AndroidViewModel {
    private EMContactManagerRepository mRepository;
    private SingleSourceLiveData<Resource<List<EaseUser>>> contactObservable;

    public FriendsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMContactManagerRepository();
        contactObservable = new SingleSourceLiveData<>();
    }

    public void loadContactList() {
        contactObservable.setSource(mRepository.getContactList());
    }

    public LiveData<Resource<List<EaseUser>>> getContactObservable() {
        return contactObservable;
    }
}
