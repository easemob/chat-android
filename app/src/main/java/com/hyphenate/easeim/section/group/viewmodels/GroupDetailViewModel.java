package com.hyphenate.easeim.section.group.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.livedatas.SingleSourceLiveData;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.repositories.EMChatManagerRepository;
import com.hyphenate.easeim.common.repositories.EMGroupManagerRepository;
import com.hyphenate.easeim.common.repositories.EMPushManagerRepository;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailViewModel extends AndroidViewModel {
    private EMGroupManagerRepository repository;
    private EMChatManagerRepository chatRepository;
    private SingleSourceLiveData<Resource<EMGroup>> groupObservable;
    private SingleSourceLiveData<Resource<String>> announcementObservable;
    private SingleSourceLiveData<Resource<String>> refreshObservable;
    private SingleSourceLiveData<Resource<Boolean>> leaveGroupObservable;
    private SingleSourceLiveData<Resource<Boolean>> blockGroupMessageObservable;
    private SingleSourceLiveData<Resource<Boolean>> unblockGroupMessage;
    private SingleSourceLiveData<Resource<Boolean>> clearHistoryObservable;
    private SingleSourceLiveData<Boolean> offPushObservable;


    public GroupDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new EMGroupManagerRepository();
        chatRepository = new EMChatManagerRepository();
        groupObservable = new SingleSourceLiveData<>();
        announcementObservable = new SingleSourceLiveData<>();
        refreshObservable = new SingleSourceLiveData<>();
        leaveGroupObservable = new SingleSourceLiveData<>();
        blockGroupMessageObservable = new SingleSourceLiveData<>();
        unblockGroupMessage = new SingleSourceLiveData<>();
        clearHistoryObservable = new SingleSourceLiveData<>();
        offPushObservable = new SingleSourceLiveData<>();
    }

    public LiveDataBus getMessageChangeObservable() {
        return LiveDataBus.get();
    }

    public LiveData<Resource<EMGroup>> getGroupObservable() {
        return groupObservable;
    }

    public void getGroup(String groupId) {
        new EMPushManagerRepository().getPushConfigsFromServer();
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

    public LiveData<Resource<Boolean>> blockGroupMessageObservable() {
        return blockGroupMessageObservable;
    }

    public void blockGroupMessage(String groupId) {
        blockGroupMessageObservable.setSource(repository.blockGroupMessage(groupId));
    }

    public LiveData<Resource<Boolean>> unblockGroupMessage() {
        return unblockGroupMessage;
    }

    public void unblockGroupMessage(String groupId) {
        unblockGroupMessage.setSource(repository.unblockGroupMessage(groupId));
    }

    public LiveData<Boolean> offPushObservable() {
        return offPushObservable;
    }

    public void updatePushServiceForGroup(String groupId, boolean noPush) {
        EaseThreadManager.getInstance().runOnIOThread(()-> {
            List<String> onPushList = new ArrayList<>();
            onPushList.add(groupId);
            try {
                DemoHelper.getInstance().getPushManager().updatePushServiceForGroup(onPushList, noPush);
            } catch (HyphenateException e) {
                e.printStackTrace();
                offPushObservable.postValue(true);
            }
            offPushObservable.postValue(true);
        });

    }

    public LiveData<Resource<Boolean>> getClearHistoryObservable() {
        return clearHistoryObservable;
    }

    public void clearHistory(String conversationId) {
        clearHistoryObservable.setSource(chatRepository.deleteConversationById(conversationId));
    }
}
