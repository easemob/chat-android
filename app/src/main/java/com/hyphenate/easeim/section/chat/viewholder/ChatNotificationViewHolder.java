package com.hyphenate.easeim.section.chat.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;

public class ChatNotificationViewHolder extends EaseChatRowViewHolder {

    public ChatNotificationViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemView, itemClickListener, itemStyle);
    }
}

