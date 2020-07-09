package com.hyphenate.chatuidemo.section.chat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hyphenate.chatuidemo.section.conversation.viewmodel.ConversationListViewModel;

public class ChatViewModel extends ConversationListViewModel {

    public ChatViewModel(@NonNull Application application) {
        super(application);
    }
}
