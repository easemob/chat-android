package com.hyphenate.easeui.ui.chat.delegates;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.adapter.EaseAdapterDelegate;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;

public abstract class EaseMessageAdapterDelegate<T, VH extends EaseChatRowViewHolder> extends EaseAdapterDelegate<T, VH> {
    private MessageListItemClickListener mItemClickListener;
    private EaseMessageListItemStyle mItemStyle;
    public boolean mIsSender;

    public EaseMessageAdapterDelegate() {
        this.mItemStyle = createDefaultItemStyle();
    }

    public EaseMessageAdapterDelegate(MessageListItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
        this.mItemStyle = createDefaultItemStyle();
    }

    public EaseMessageAdapterDelegate(MessageListItemClickListener itemClickListener,
                                      EaseMessageListItemStyle itemStyle) {
        this.mItemClickListener = itemClickListener;
        this.mItemStyle = itemStyle;
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

    public boolean isMessageSender(EMMessage message) {
        return message.direct() == EMMessage.Direct.SEND;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent) {
        EaseChatRow view = getEaseChatRow(parent, mIsSender);
        return createViewHolder(view, mItemClickListener, mItemStyle);
    }

    protected abstract EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender);

    protected abstract VH createViewHolder(View view, MessageListItemClickListener itemClickListener
            , EaseMessageListItemStyle itemStyle);

    public void setListItemClickListener(MessageListItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }
}
