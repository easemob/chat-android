package com.hyphenate.chatuidemo.section.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.section.chat.activity.ChatVoiceCallActivity;
import com.hyphenate.chatuidemo.section.chat.views.ChatRowVoiceCall;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;

public class ChatVoiceCallViewHolder extends EaseChatRowViewHolder {

    public ChatVoiceCallViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemView, itemClickListener, itemStyle);
    }

    public static ChatVoiceCallViewHolder create(ViewGroup parent, boolean isSender,
                                                        MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        return new ChatVoiceCallViewHolder(new ChatRowVoiceCall(parent.getContext(), isSender), itemClickListener, itemStyle);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        if(message.direct() == EMMessage.Direct.SEND) {
            ChatVoiceCallActivity.actionStart(getContext(), message.getTo());
        }else {
            ChatVoiceCallActivity.actionStart(getContext(), message.getFrom());
        }
    }
}
