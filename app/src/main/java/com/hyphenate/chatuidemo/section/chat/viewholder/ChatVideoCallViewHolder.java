package com.hyphenate.chatuidemo.section.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.section.chat.activity.ChatVideoCallActivity;
import com.hyphenate.chatuidemo.section.chat.views.ChatRowVideoCall;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;

public class ChatVideoCallViewHolder extends EaseChatRowViewHolder {

    public ChatVideoCallViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemView, itemClickListener, itemStyle);
    }

    public static ChatVideoCallViewHolder create(ViewGroup parent, boolean isSender,
                                                        MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        return new ChatVideoCallViewHolder(new ChatRowVideoCall(parent.getContext(), isSender), itemClickListener, itemStyle);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        if(message.direct() == EMMessage.Direct.SEND) {
            ChatVideoCallActivity.actionStart(getContext(), message.getTo());
        }else {
            ChatVideoCallActivity.actionStart(getContext(), message.getFrom());
        }
    }
}
