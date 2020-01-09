package com.hyphenate.easeui.adapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.interfaces.IViewHolderProvider;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;

public abstract class EaseBaseMessageAdapter<T extends EMMessage> extends EaseBaseRecyclerViewAdapter<T> {
    public MessageListItemClickListener itemClickListener;
    public EaseMessageListItemStyle itemStyle;
    public IViewHolderProvider viewHolderProvider;

    /**
     * create default item style
     * @return
     */
    public EaseMessageListItemStyle createDefaultItemStyle() {
        EaseMessageListItemStyle.Builder builder = new EaseMessageListItemStyle.Builder();
        builder.showAvatar(true)
                .showUserNick(false);
        return builder.build();
    }

    /**
     * set item click listener
     * @param itemClickListener
     */
    public void setListItemClickListener(MessageListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * set item style
     * @param itemStyle
     */
    public void setItemStyle(EaseMessageListItemStyle itemStyle) {
        this.itemStyle = itemStyle;
    }

    /**
     * if show nick name
     * @param showUserNick
     */
    public void showUserNick(boolean showUserNick) {
        this.itemStyle.setShowUserNick(showUserNick);
    }
}
