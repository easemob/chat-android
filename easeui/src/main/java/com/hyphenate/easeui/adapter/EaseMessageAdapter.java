package com.hyphenate.easeui.adapter;

import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.viewholder.MESSAGE_TYPE;

/**
 * 在{@link com.hyphenate.easeui.viewholder.MESSAGE_TYPE}中进行添加及修改
 */
public class EaseMessageAdapter extends EaseBaseRecyclerViewAdapter<EMMessage> {

    private String toChatUsername;
    private EMConversation conversation;
    private MessageListItemClickListener itemClickListener;
    private EaseMessageListItemStyle itemStyle;

    public EaseMessageAdapter(String username, int chatType) {
        this.toChatUsername = username;
        this.conversation = EMClient.getInstance().chatManager().getConversation(username
                , EaseCommonUtils.getConversationType(chatType), true);
        itemStyle = createDefaultItemStyle();
    }

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_empty_list_invisible;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return createItemViewHolder(parent, viewType);
    }

    private ViewHolder createItemViewHolder(ViewGroup parent, int viewType) {
        return EaseChatRowViewHolder.create(parent, viewType, itemClickListener, itemStyle);
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItemMessage(position);
        if(message == null) {
            return super.getItemViewType(position);
        }
        int viewType = MESSAGE_TYPE.getAdapterViewType(message);
        return viewType == 0 ? super.getItemViewType(position) : viewType;
    }

    /**
     * create default item style
     * @return
     */
    protected EaseMessageListItemStyle createDefaultItemStyle() {
        EaseMessageListItemStyle.Builder builder = new EaseMessageListItemStyle.Builder();
        builder.showAvatar(true)
                .showUserNick(false);
        return builder.build();
    }


    /**
     * get item message
     * @param position
     * @return
     */
    private EMMessage getItemMessage(int position) {
        if(mData != null) {
            return mData.get(position);
        }
        return null;
    }

    public void setConversationMessages() {
        if(conversation != null) {
            mData = conversation.getAllMessages();
            conversation.markAllMessagesAsRead();
            setData(mData);
        }
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
