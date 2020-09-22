package com.hyphenate.easeim.section.group.viewmodels;

import android.app.Application;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.livedatas.SingleSourceLiveData;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.repositories.EMChatRoomManagerRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ChatRoomDetailViewModel extends AndroidViewModel {
    private EMChatRoomManagerRepository repository;
    private SingleSourceLiveData<Resource<EMChatRoom>> chatRoomObservable = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<List<String>>> membersObservable;
    private SingleSourceLiveData<Resource<String>> announcementObservable = new SingleSourceLiveData<>();
    private LiveDataBus messageChangeObservable = LiveDataBus.get();
    private SingleSourceLiveData<Resource<Boolean>> destroyGroupObservable = new SingleSourceLiveData<>();

    public ChatRoomDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new EMChatRoomManagerRepository();
        membersObservable = new SingleSourceLiveData<>();
    }

    public LiveDataBus getMessageChangeObservable() {
        return messageChangeObservable;
    }

    public LiveData<Resource<EMChatRoom>> chatRoomObservable() {
        return chatRoomObservable;
    }

    public LiveData<Resource<List<String>>> memberObservable() {
        return membersObservable;
    }

    public LiveData<Resource<String>> updateAnnouncementObservable() {
        return announcementObservable;
    }

    public LiveData<Resource<Boolean>> destroyGroupObservable() {
        return destroyGroupObservable;
    }

    public void getChatRoomFromServer(String roomId) {
        chatRoomObservable.setSource(repository.getChatRoomById(roomId));
    }

    public void changeChatRoomSubject(String roomId, String newSubject) {
        chatRoomObservable.setSource(repository.changeChatRoomSubject(roomId, newSubject));
    }

    public void changeChatroomDescription(String roomId, String newDescription) {
        chatRoomObservable.setSource(repository.changeChatroomDescription(roomId, newDescription));
    }

    public void getChatRoomMembers(String roomId) {
        membersObservable.setSource(repository.loadMembers(roomId));
    }

    public void updateAnnouncement(String roomId, String announcement) {
        announcementObservable.setSource(repository.updateAnnouncement(roomId, announcement));
    }

    public void fetchChatRoomAnnouncement(String roomId) {
        announcementObservable.setSource(repository.fetchChatRoomAnnouncement(roomId));
    }

    public void destroyGroup(String roomId) {
        destroyGroupObservable.setSource(repository.destroyChatRoom(roomId));
    }

}
