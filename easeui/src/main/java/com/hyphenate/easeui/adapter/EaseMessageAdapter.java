package com.hyphenate.easeui.adapter;

import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.interfaces.IViewHolderProvider;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.ui.chat.delegates.EaseMessageAdapterDelegate;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.viewholder.EaseViewHolderHelper;

/**
 * 开发者可在实现{@link IViewHolderProvider}提供相应的ViewHolder及ViewType
 * ViewHolder的提供主要通过{@link EaseViewHolderHelper}
 */
public class EaseMessageAdapter extends EaseBaseDelegateAdapter<EMMessage> {
    public MessageListItemClickListener itemClickListener;
    public EaseMessageListItemStyle itemStyle;

    public EaseMessageAdapter() {
        itemStyle = createDefaultItemStyle();
    }

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_empty_list_invisible;
    }

    public EaseBaseDelegateAdapter addDelegate(EaseAdapterDelegate<?, ?> delegate, String tag) {
        if(delegate instanceof EaseMessageAdapterDelegate) {
            ((EaseMessageAdapterDelegate)delegate).setListItemClickListener(itemClickListener);
        }
        return super.addDelegate(delegate, tag);
    }

    public EaseBaseDelegateAdapter addDelegate(EaseAdapterDelegate<?, ?> delegate) {
        if(delegate instanceof EaseMessageAdapterDelegate) {
            ((EaseMessageAdapterDelegate)delegate).setListItemClickListener(itemClickListener);
        }
        return super.addDelegate(delegate);
    }

    public EaseBaseDelegateAdapter setFallbackDelegate(EaseAdapterDelegate delegate) {
        if(delegate instanceof EaseMessageAdapterDelegate) {
            ((EaseMessageAdapterDelegate)delegate).setListItemClickListener(itemClickListener);
        }
        return super.setFallbackDelegate(delegate);
    }

    /**
     * get item message
     * @param position
     * @return
     */
    private EMMessage getItemMessage(int position) {
        if(mData != null && !mData.isEmpty()) {
            return mData.get(position);
        }
        return null;
    }

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
