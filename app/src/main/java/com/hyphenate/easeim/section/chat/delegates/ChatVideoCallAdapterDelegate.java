package com.hyphenate.easeim.section.chat.delegates;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.utils.EaseMsgUtils;
import com.hyphenate.easeim.section.chat.viewholder.ChatVideoCallViewHolder;
import com.hyphenate.easeim.section.chat.views.ChatRowVideoCall;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;

import static com.hyphenate.chat.EMMessage.Type.TXT;

public class ChatVideoCallAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {
    @Override
    public boolean isForViewType(EMMessage item, int position) {
        boolean isRtcCall =item.getStringAttribute(EaseMsgUtils.CALL_MSG_TYPE,"").equals(EaseMsgUtils.CALL_MSG_INFO)?true:false;
        boolean isVideoCall = item.getIntAttribute(EaseMsgUtils.CALL_TYPE,0) == EaseCallType.SINGLE_VIDEO_CALL.code?true:false;
        return item.getType() == TXT && isRtcCall && isVideoCall;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new ChatRowVideoCall(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new ChatVideoCallViewHolder(view, itemClickListener);
    }
}
