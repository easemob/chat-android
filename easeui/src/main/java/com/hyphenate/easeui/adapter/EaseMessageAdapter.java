package com.hyphenate.easeui.adapter;

import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;

public class EaseMessageAdapter extends EaseBaseRecyclerViewAdapter<EMMessage> {
    public static final int MESSAGE_TYPE_RECV_TXT = 110;
    public static final int MESSAGE_TYPE_SENT_TXT = 111;
    public static final int MESSAGE_TYPE_SENT_IMAGE = 112;
    public static final int MESSAGE_TYPE_SENT_LOCATION = 113;
    public static final int MESSAGE_TYPE_RECV_LOCATION = 114;
    public static final int MESSAGE_TYPE_RECV_IMAGE = 115;
    public static final int MESSAGE_TYPE_SENT_VOICE = 116;
    public static final int MESSAGE_TYPE_RECV_VOICE = 117;
    public static final int MESSAGE_TYPE_SENT_VIDEO = 118;
    public static final int MESSAGE_TYPE_RECV_VIDEO = 119;
    public static final int MESSAGE_TYPE_SENT_FILE = 120;
    public static final int MESSAGE_TYPE_RECV_FILE = 121;
    public static final int MESSAGE_TYPE_SENT_EXPRESSION = 122;
    public static final int MESSAGE_TYPE_RECV_EXPRESSION = 123;

    private String toChatUsername;
    private EMConversation conversation;
    private MessageListItemClickListener itemClickListener;
    private EaseMessageListItemStyle itemStyle;

    public EaseMessageAdapter(String username, int chatType) {
        this.toChatUsername = username;
        this.conversation = EMClient.getInstance().chatManager().getConversation(username
                , EaseCommonUtils.getConversationType(chatType), true);
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
        if(message.getType() == EMMessage.Type.TXT) {
            if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXPRESSION : MESSAGE_TYPE_SENT_EXPRESSION;
            }
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == EMMessage.Type.IMAGE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;
        }
        if (message.getType() == EMMessage.Type.LOCATION) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
        }
        if (message.getType() == EMMessage.Type.VOICE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
        }
        if (message.getType() == EMMessage.Type.VIDEO) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
        }
        if (message.getType() == EMMessage.Type.FILE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
        }
        return super.getItemViewType(position);
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
    public void setItemClickListener(MessageListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * set item style
     * @param itemStyle
     */
    public void setItemStyle(EaseMessageListItemStyle itemStyle) {
        this.itemStyle = itemStyle;
    }
}
