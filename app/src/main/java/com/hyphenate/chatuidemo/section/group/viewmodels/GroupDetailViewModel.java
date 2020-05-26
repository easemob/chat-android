package com.hyphenate.chatuidemo.section.group.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.common.livedatas.MessageChangeLiveData;
import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMGroupManagerRepository;
import com.hyphenate.easeui.model.EaseEvent;

public class GroupDetailViewModel extends AndroidViewModel {
    private EMGroupManagerRepository repository;
    private SingleSourceLiveData<Resource<EMGroup>> groupObservable;
    private SingleSourceLiveData<Resource<String>> announcementObservable;
    private SingleSourceLiveData<Resource<String>> refreshObservable;
    private SingleSourceLiveData<Resource<Boolean>> leaveGroupObservable;
    private MessageChangeLiveData messageChangeLiveData;

    public GroupDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new EMGroupManagerRepository();
        groupObservable = new SingleSourceLiveData<>();
        announcementObservable = new SingleSourceLiveData<>();
        refreshObservable = new SingleSourceLiveData<>();
        leaveGroupObservable = new SingleSourceLiveData<>();
        messageChangeLiveData = MessageChangeLiveData.getInstance();
    }

    public LiveData<EaseEvent> getMessageChangeObservable() {
        return messageChangeLiveData;
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

    public LiveData<Resource<String>> getRefreshObservable() {
        return refreshObservable;
    }

    public void setGroupName(String groupId, String groupName) {
        refreshObservable.setSource(repository.setGroupName(groupId, groupName));
    }

    public void setGroupAnnouncement(String groupId, String announcement) {
        refreshObservable.setSource(repository.setGroupAnnouncement(groupId, announcement));
    }

    public void setGroupDescription(String groupId, String description) {
        refreshObservable.setSource(repository.setGroupDescription(groupId, description));
    }

    public LiveData<Resource<Boolean>> getLeaveGroupObservable() {
        return leaveGroupObservable;
    }

    public void leaveGroup(String groupId) {
        leaveGroupObservable.setSource(repository.leaveGroup(groupId));
    }

    public void destroyGroup(String groupId) {
        leaveGroupObservable.setSource(repository.destroyGroup(groupId));
    }
}
