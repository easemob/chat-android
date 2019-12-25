package com.hyphenate.chatuidemo.section.conversation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMChatManagerRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private EMChatManagerRepository mRepository;

    private SingleSourceLiveData<Resource<List<EMConversation>>> conversationObservable;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMChatManagerRepository();
        conversationObservable = new SingleSourceLiveData<>();
    }

    /**
     * 获取聊天列表
     */
    public void loadConversationList() {
        conversationObservable.setSource(mRepository.loadConversationList());
    }

    public LiveData<Resource<List<EMConversation>>> getConversationObservable() {
        return conversationObservable;
    }


}
