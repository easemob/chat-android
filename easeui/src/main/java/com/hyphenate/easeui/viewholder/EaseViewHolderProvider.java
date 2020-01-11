package com.hyphenate.easeui.viewholder;

import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.interfaces.IViewHolderProvider;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;

public class EaseViewHolderProvider implements IViewHolderProvider {

    @Override
    public EaseChatRowViewHolder provideViewHolder(ViewGroup parent, int viewType,
                                                                 MessageListItemClickListener listener,
                                                                 EaseMessageListItemStyle itemStyle) {
        return EaseViewHolderHelper.getInstance().getChatRowViewHolder(parent, viewType, listener, itemStyle);
    }

    @Override
    public int provideViewType(EMMessage message) {
        return EaseViewHolderHelper.getInstance().getAdapterViewType(message);
    }
}
