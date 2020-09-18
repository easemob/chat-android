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

    public ChatLiveInviteViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemView, itemClickListener, itemStyle);
    }

    public static ChatLiveInviteViewHolder create(ViewGroup parent, boolean isSender,
                                                        MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        return new ChatLiveInviteViewHolder(new ChatRowLive(parent.getContext(), isSender), itemClickListener, itemStyle);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);

        String confId = message.getStringAttribute(DemoConstant.EM_CONFERENCE_ID, "");
        String confPassword = message.getStringAttribute(DemoConstant.EM_CONFERENCE_PASSWORD,"");
        int type = message.getIntAttribute(DemoConstant.EM_CONFERENCE_TYPE, 0);
        goLive(confId, confPassword, message.getFrom());
    }

    public void goLive(String confId, String password, String inviter) {
        PushAndMessageHelper.goLive(getContext(), confId, password, inviter);
    }
}
