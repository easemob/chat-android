package com.hyphenate.chatuidemo.section.chat.delegates;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.section.chat.viewholder.ChatVoiceCallViewHolder;
import com.hyphenate.chatuidemo.section.chat.views.ChatRowVoiceCall;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.ui.chat.delegates.EaseMessageAdapterDelegate;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;

import static com.hyphenate.chat.EMMessage.Type.TXT;

public class ChatVoiceCallAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {
    @Override
    public boolean isForViewType(EMMessage item, int position) {
        mIsSender = isMessageSender(item);
        return item.getType() == TXT && item.getBooleanAttribute(DemoConstant.MESSAGE_ATTR_IS_VOICE_CALL, false);
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new ChatRowVoiceCall(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        return new ChatVoiceCallViewHolder(view, itemClickListener, itemStyle);
    }
}
