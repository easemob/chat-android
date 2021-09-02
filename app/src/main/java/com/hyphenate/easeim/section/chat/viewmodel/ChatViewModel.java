package com.hyphenate.easeim.section.chat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeim.common.livedatas.SingleSourceLiveData;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.repositories.EMChatManagerRepository;
import com.hyphenate.easeim.common.repositories.EMChatRoomManagerRepository;
import com.hyphenate.easeim.section.conversation.viewmodel.ConversationListViewModel;

import java.util.List;

public class ChatViewModel extends ConversationListViewModel {
    private EMChatRoomManagerRepository chatRoomManagerRepository;
    private EMChatManagerRepository chatManagerRepository;
    private SingleSourceLiveData<Resource<EMChatRoom>> chatRoomObservable;
    private SingleSourceLiveData<Resource<Boolean>> makeConversationReadObservable;
    private SingleSourceLiveData<Resource< List<String>>> getNoPushUsersObservable;
    private SingleSourceLiveData<Resource<Boolean>> setNoPushUsersObservable;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        chatRoomManagerRepository = new EMChatRoomManagerRepository();
        chatManagerRepository = new EMChatManagerRepository();
        chatRoomObservable = new SingleSourceLiveData<>();
        makeConversationReadObservable = new SingleSourceLiveData<>();
        getNoPushUsersObservable = new SingleSourceLiveData<>();
        setNoPushUsersObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<EMChatRoom>> getChatRoomObservable() {
        return chatRoomObservable;
    }
    public LiveData<Resource<List<String>>> getNoPushUsersObservable() {
        return getNoPushUsersObservable;
    }
    public LiveData<Resource<Boolean>> setNoPushUsersObservable() {
        return setNoPushUsersObservable;
    }

    public void getChatRoom(String roomId) {
        EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(roomId);
        if (room != null) {
            chatRoomObservable.setSource(new MutableLiveData<>(Resource.success(room)));
        } else {
            chatRoomObservable.setSource(chatRoomManagerRepository.getChatRoomById(roomId));
        }
    }

    public void makeConversationReadByAck(String conversationId) {
        makeConversationReadObservable.setSource(chatManagerRepository.makeConversationReadByAck(conversationId));
    }

    /**
     * 设置单聊用户聊天免打扰
     *
     * @param userId 用户名
     * @param noPush 是否免打扰
     */
    public void setUserNotDisturb(String userId, boolean noPush) {
        setNoPushUsersObservable.setSource(chatManagerRepository.setUserNotDisturb(userId,noPush));
    }
    /**
     * 获取聊天免打扰用户
     */
    public void getNoPushUsers() {
        getNoPushUsersObservable.setSource(chatManagerRepository.getNoPushUsers());
    }

    public LiveData<Resource<Boolean>> getMakeConversationReadObservable() {
        return makeConversationReadObservable;
    }

}
