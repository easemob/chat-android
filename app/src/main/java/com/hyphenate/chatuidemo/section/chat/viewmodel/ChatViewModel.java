package com.hyphenate.chatuidemo.section.chat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMChatManagerRepository;
import com.hyphenate.chatuidemo.section.conversation.viewmodel.HomeViewModel;

public class ChatViewModel extends HomeViewModel {

    public ChatViewModel(@NonNull Application application) {
        super(application);
    }
}
