package com.hyphenate.easeim.section.chat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeim.common.livedatas.SingleSourceLiveData;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.repositories.EMChatRoomManagerRepository;
import com.hyphenate.easeim.section.conversation.viewmodel.ConversationListViewModel;

public class ChatViewModel extends ConversationListViewModel {
    private EMChatRoomManagerRepository chatRoomManagerRepository;
    private SingleSourceLiveData<Resource<EMChatRoom>> chatRoomObservable;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        chatRoomManagerRepository = new EMChatRoomManagerRepository();
        chatRoomObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<EMChatRoom>> getChatRoomObservable() {
        return chatRoomObservable;
    }

    public void getChatRoom(String roomId) {
        EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(roomId);
        if(room != null) {
            chatRoomObservable.setSource(new MutableLiveData<>(Resource.success(room)));
        }else {
            chatRoomObservable.setSource(chatRoomManagerRepository.getChatRoomById(roomId));
        }
    }

}
