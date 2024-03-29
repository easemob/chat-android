package com.hyphenate.chatdemo.section.chat.delegates;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatdemo.common.constant.DemoConstant;
import com.hyphenate.chatdemo.section.chat.viewholder.ChatUserCardViewHolder;
import com.hyphenate.chatdemo.section.chat.views.ChatRowUserCard;
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;


public class ChatUserCardAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {
    @Override
    public boolean isForViewType(EMMessage item, int position) {
        if(item.getType() == EMMessage.Type.CUSTOM){
            EMCustomMessageBody messageBody = (EMCustomMessageBody) item.getBody();
            String event = messageBody.event();
            return event.equals(DemoConstant.USER_CARD_EVENT)?true:false;
        }
        return false;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new ChatRowUserCard(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new ChatUserCardViewHolder(view, itemClickListener);
    }
}

