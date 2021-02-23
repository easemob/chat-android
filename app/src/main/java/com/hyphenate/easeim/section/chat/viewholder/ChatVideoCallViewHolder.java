package com.hyphenate.easeim.section.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeim.section.chat.views.ChatRowVideoCall;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;

public class ChatVideoCallViewHolder extends EaseChatRowViewHolder {

    public ChatVideoCallViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatVideoCallViewHolder create(ViewGroup parent, boolean isSender,
                                                        MessageListItemClickListener itemClickListener) {
        return new ChatVideoCallViewHolder(new ChatRowVideoCall(parent.getContext(), isSender), itemClickListener);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
      }
}
