package com.hyphenate.chatdemo.section.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.chatdemo.section.chat.views.ChatRowDisclaimer;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;

public class ChatDisclaimerViewHolder extends EaseChatRowViewHolder {

    public ChatDisclaimerViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatDisclaimerViewHolder create(ViewGroup parent, boolean isSender,
                                                  MessageListItemClickListener itemClickListener) {
        return new ChatDisclaimerViewHolder(new ChatRowDisclaimer(parent.getContext(), isSender), itemClickListener);
    }
}
