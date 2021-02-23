package com.hyphenate.easeim.section.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.manager.PushAndMessageHelper;
import com.hyphenate.easeim.section.chat.views.ChatRowLive;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;

public class ChatLiveInviteViewHolder extends EaseChatRowViewHolder {

    public ChatLiveInviteViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatLiveInviteViewHolder create(ViewGroup parent, boolean isSender,
                                                        MessageListItemClickListener itemClickListener) {
        return new ChatLiveInviteViewHolder(new ChatRowLive(parent.getContext(), isSender), itemClickListener);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
    }

}
