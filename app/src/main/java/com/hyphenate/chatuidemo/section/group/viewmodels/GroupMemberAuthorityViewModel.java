package com.hyphenate.chatuidemo.section.group.viewmodels;

import android.app.Application;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;
import com.hyphenate.chatuidemo.common.livedatas.LiveDataBus;
import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMGroupManagerRepository;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class GroupMemberAuthorityViewModel extends AndroidViewModel {
    private EMGroupManagerRepository repository;
    private SingleSourceLiveData<Resource<EMGroup>> adminObservable;
    private SingleSourceLiveData<Resource<List<EaseUser>>> membersObservable;
    private SingleSourceLiveData<Resource<Map<String, Long>>> muteMembersObservable;
    private SingleSourceLiveData<Resource<List<String>>> blackMembersObservable;
    private MediatorLiveData<Resource<Boolean>> refreshObservable;
    private LiveDataBus messageChangeLiveData = LiveDataBus.get();

    public GroupMemberAuthorityViewModel(@NonNull Application application) {
        super(application);
        repository = new EMGroupManagerRepository();
        adminObservable = new SingleSourceLiveData<>();
        membersObservable = new SingleSourceLiveData<>();
        muteMembersObservable = new SingleSourceLiveData<>();
        blackMembersObservable = new SingleSourceLiveData<>();
        refreshObservable = new MediatorLiveData<>();
    }

    public LiveDataBus getMessageChangeObservable() {
        return messageChangeLiveData;
    }

    public LiveData<Resource<EMGroup>> getGroupObservable() {
        return adminObservable;
    }

    public void getGroup(String groupId) {
        adminObservable.setSource(repository.getGroupFromServer(groupId));
    }

    public LiveData<Resource<List<EaseUser>>> getMemberObservable() {
        return membersObservable;
    }

    public void getMembers(String groupId) {
        membersObservable.setSource(repository.getGroupMembers(groupId));
    }

    public LiveData<Resource<Map<String, Long>>> getMuteMembersObservable() {
        return muteMembersObservable;
    }

    public void getMuteMembers(String groupId) {
        muteMembersObservable.setSource(repository.getGroupMuteMap(groupId));
    }

    public LiveData<Resource<List<String>>> getBlackObservable() {
        return blackMembersObservable;
    }

    public void getBlackMembers(String groupId) {
        blackMembersObservable.setSource(repository.getGroupBlackList(groupId));
    }

    public LiveData<Resource<Boolean>> getRefreshObservable() {
        return refreshObservable;
    }

    public void changeOwner(String groupId, String username) {
        refreshObservable.addSource(repository.changeOwner(groupId, username),
                value -> refreshObservable.postValue(value));
    }

    public void addGroupAdmin(String groupId, String username) {
        refreshObservable.addSource(repository.addGroupAdmin(groupId, username),
                value -> refreshObservable.postValue(value));
    }

    public void removeGroupAdmin(String groupId, String username) {
        refreshObservable.addSource(repository.removeGroupAdmin(groupId, username),
                value -> refreshObservable.postValue(value));
    }

    public void removeUserFromGroup(String groupId, String username) {
        refreshObservable.addSource(repository.removeUserFromGroup(groupId, username),
                value -> refreshObservable.postValue(value));
    }

    public void blockUser(String groupId, String username) {
        refreshObservable.addSource(repository.blockUser(groupId, username),
                value -> refreshObservable.postValue(value));
    }

    public void unblockUser(String groupId, String username) {
        refreshObservable.addSource(repository.unblockUser(groupId, username),
                value -> refreshObservable.postValue(value));
    }

    public void muteGroupMembers(String groupId, List<String> usernames, long duration) {
        refreshObservable.addSource(repository.muteGroupMembers(groupId, usernames, duration),
                value -> refreshObservable.postValue(value));
    }

    public void unMuteGroupMembers(String groupId, List<String> usernames) {
        refreshObservable.addSource(repository.unMuteGroupMembers(groupId, usernames),
                value -> refreshObservable.postValue(value));
    }

}
