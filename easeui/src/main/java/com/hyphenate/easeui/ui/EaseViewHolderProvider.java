package com.hyphenate.easeui.ui;

import android.util.SparseArray;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.interfaces.IViewHolderProvider;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.viewholder.EaseViewHolderHelper;

public class EaseViewHolderProvider implements IViewHolderProvider {
    private SparseArray<EaseChatRowViewHolder> viewHolderMap;

    @Override
    public SparseArray<EaseChatRowViewHolder> provideViewHolder(ViewGroup parent,
                                                                 MessageListItemClickListener listener,
                                                                 EaseMessageListItemStyle itemStyle) {
        if(viewHolderMap == null) {
            viewHolderMap = setViewHolderMap(parent, listener, itemStyle);
        }
        return viewHolderMap;
    }

    private SparseArray<EaseChatRowViewHolder> setViewHolderMap(ViewGroup parent, MessageListItemClickListener listener, EaseMessageListItemStyle itemStyle) {
        return EaseViewHolderHelper.getInstance().getDefaultChatViewHolder(parent, listener, itemStyle);
    }

    @Override
    public int provideViewType(EMMessage message) {
        return EaseViewHolderHelper.getInstance().getDefaultAdapterViewType(message);
    }
}
