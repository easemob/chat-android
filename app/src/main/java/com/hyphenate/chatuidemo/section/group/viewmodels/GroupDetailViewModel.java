package com.hyphenate.chatuidemo.section.group.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMGroupManagerRepository;

import java.util.List;

public class GroupDetailViewModel extends AndroidViewModel {
    private EMGroupManagerRepository repository;
    private SingleSourceLiveData<Resource<EMGroup>> groupObservable;
    private SingleSourceLiveData<Resource<String>> announcementObservable;

    public GroupDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new EMGroupManagerRepository();
        groupObservable = new SingleSourceLiveData<>();
        announcementObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<EMGroup>> getGroupObservable() {
        return groupObservable;
    }

    public void getGroup(String groupId) {
        groupObservable.setSource(repository.getGroupFromServer(groupId));
    }

    public LiveData<Resource<String>> getAnnouncementObservable() {
        return announcementObservable;
    }

    public void getGroupAnnouncement(String groupId) {
        announcementObservable.setSource(repository.getGroupAnnouncement(groupId));
    }

    public void setGroupName(String groupName) {

    }
}
