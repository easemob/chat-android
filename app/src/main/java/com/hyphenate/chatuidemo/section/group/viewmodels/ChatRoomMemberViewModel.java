package com.hyphenate.chatuidemo.section.group.viewmodels;

import android.app.Application;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chatuidemo.common.livedatas.MessageChangeLiveData;
import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMChatRoomManagerRepository;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ChatRoomMemberViewModel extends AndroidViewModel {
    private EMChatRoomManagerRepository repository;
    private SingleSourceLiveData<Resource<EMChatRoom>> chatRoomObservable;
    private SingleSourceLiveData<Resource<Boolean>> destroyGroupObservable;
    private SingleSourceLiveData<Resource<List<String>>> blackObservable;
    private SingleSourceLiveData<Resource<Map<String, Long>>> muteMapObservable;
    private SingleSourceLiveData<Resource<List<String>>> membersObservable;
    private MessageChangeLiveData messageChangeObservable;

    public ChatRoomMemberViewModel(@NonNull Application application) {
        super(application);
        repository = new EMChatRoomManagerRepository();
        chatRoomObservable = new SingleSourceLiveData<>();
        destroyGroupObservable = new SingleSourceLiveData<>();
        blackObservable = new SingleSourceLiveData<>();
        muteMapObservable = new SingleSourceLiveData<>();
        membersObservable = new SingleSourceLiveData<>();
        messageChangeObservable = MessageChangeLiveData.getInstance();
    }

    public LiveData<Resource<EMChatRoom>> chatRoomObservable() {
        return chatRoomObservable;
    }

    public LiveData<Resource<Boolean>> destroyGroupObservable() {
        return destroyGroupObservable;
    }

    public LiveData<Resource<List<String>>> blackObservable() {
        return blackObservable;
    }

    public LiveData<Resource<Map<String, Long>>> muteMapObservable() {
        return muteMapObservable;
    }

    public LiveData<Resource<List<String>>> membersObservable() {
        return membersObservable;
    }

    public MessageChangeLiveData getMessageChangeObservable() {
        return messageChangeObservable;
    }

    public void getGroupMuteMap(String roomId) {
        muteMapObservable.setSource(repository.getGroupMuteMap(roomId));
    }

    public void getGroupBlackList(String roomId) {
        blackObservable.setSource(repository.getGroupBlackList(roomId));
    }

    public void getMembersList(String roomId) {
        membersObservable.setSource(repository.loadMembers(roomId));
    }

    public void getChatRoom(String roomId) {
        chatRoomObservable.setSource(repository.getChatRoomById(roomId));
    }

    public void addGroupAdmin(String roomId, String username) {
        chatRoomObservable.setSource(repository.addGroupAdmin(roomId, username));
    }

    public void removeGroupAdmin(String roomId, String username) {
        chatRoomObservable.setSource(repository.removeGroupAdmin(roomId, username));
    }

    public void changeOwner(String roomId, String username) {
        chatRoomObservable.setSource(repository.changeOwner(roomId, username));
    }

    public void removeUserFromGroup(String roomId, List<String> usernames) {
        chatRoomObservable.setSource(repository.removeUserFromGroup(roomId, usernames));
    }

    public void blockUser(String roomId, List<String> username) {
        chatRoomObservable.setSource(repository.blockUser(roomId, username));
    }

    public void unblockUser(String roomId, List<String> username) {
        chatRoomObservable.setSource(repository.unblockUser(roomId, username));
    }

    public void muteGroupMembers(String roomId, List<String> usernames, long duration) {
        chatRoomObservable.setSource(repository.muteGroupMembers(roomId, usernames, duration));
    }

    public void unMuteGroupMembers(String roomId, List<String> usernames) {
        chatRoomObservable.setSource(repository.unMuteGroupMembers(roomId, usernames));
    }

    public void destroyGroup(String roomId) {
        destroyGroupObservable.setSource(repository.destroyGroup(roomId));
    }
}
