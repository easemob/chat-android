package com.hyphenate.easeui.ui;

import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.interfaces.IViewHolderProvider;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.viewholder.EaseViewHolderHelper;

public class EaseViewHolderProvider implements IViewHolderProvider {

    @Override
    public EaseChatRowViewHolder provideViewHolder(ViewGroup parent, int viewType,
                                                                 MessageListItemClickListener listener,
                                                                 EaseMessageListItemStyle itemStyle) {
        return EaseViewHolderHelper.getInstance().getChatRowViewHolder(parent, viewType, listener, itemStyle);
    }

    @Override
    public int provideViewType(EMMessage message) {
        return EaseViewHolderHelper.getInstance().getDefaultAdapterViewType(message);
    }
}
