package com.hyphenate.easeui.viewholder;

import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.util.EMLog;

/**
 * 增加消息类型，统一在此类中进行修改
 * 1、增加类型。
 * 2、增加相应的ViewHolder及其对应的View，
 * 并在{@link #createChatViewHolder(ViewGroup, int, MessageListItemClickListener, EaseMessageListItemStyle)}中进行返回
 * 3、如果是自定义的消息类型，需要修改{@link #getAdapterViewType(EMMessage)}中相关的类型判断
 */
public enum MESSAGE_TYPE {
    TXT(110, 10110),
    EXPRESSION(111, 10111),
    IMAGE(112, 10112),
    VIDEO(113, 10113),
    LOCATION(114, 10114),
    VOICE(115, 10115),
    FILE(116, 10116),
    CMD( 117, 10117);

    private static final String TAG = MESSAGE_TYPE.class.getSimpleName();
    public int sendViewType;
    public int receiveViewType;

    private MESSAGE_TYPE(int sendViewType, int receiveViewType) {
        this.sendViewType = sendViewType;
        this.receiveViewType = receiveViewType;
    }

    public int getViewType(EMMessage.Direct direct) {
        return isSender(direct) ? sendViewType : receiveViewType;
    }

    /**
     * 判断是否是发送方
     * @param direct
     * @return
     */
    public static boolean isSender(EMMessage.Direct direct) {
        return direct == EMMessage.Direct.SEND;
    }

    /**
     * 获取布局类型
     * @param message
     * @return
     */
    public static int getAdapterViewType(EMMessage message) {
        EMMessage.Direct direct = message.direct();
        if(message.getType() == EMMessage.Type.TXT) {
            if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                return EXPRESSION.getViewType(direct);
            }
        }
        int viewType = 0;
        try {
            viewType = MESSAGE_TYPE.valueOf(message.getType().name()).getViewType(direct);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return viewType;
    }

    /**
     * 如果没有相应的类型，则默认返回TXT类型
     * @param viewType
     * @return
     */
    public static MESSAGE_TYPE getMessageType(int viewType) {
        MESSAGE_TYPE[] types = MESSAGE_TYPE.values();
        for (MESSAGE_TYPE type : types) {
            if(type.sendViewType == viewType || type.receiveViewType == viewType) {
                return type;
            }
        }
        return TXT;
    }

    /**
     * 获取相应的ViewHolder类型
     * @param parent
     * @param viewType
     * @param itemClickListener
     * @param itemStyle
     * @return
     */
    public static EaseChatRowViewHolder createChatViewHolder(ViewGroup parent, int viewType,
                                                             MessageListItemClickListener itemClickListener,
                                                             EaseMessageListItemStyle itemStyle) {

        MESSAGE_TYPE type = getMessageType(viewType);
        if(type == null) {
            return EaseTextViewHolder.create(parent, false, itemClickListener, itemStyle);
        }
        EMLog.d(TAG, "viewType = "+type.name());
        boolean isSend = viewType == type.sendViewType;
        switch (type) {
            case IMAGE :
                return EaseImageViewHolder.create(parent, isSend, itemClickListener, itemStyle);
            case LOCATION :
                return EaseLocationViewHolder.create(parent, isSend, itemClickListener, itemStyle);
            case VOICE :
                return EaseVoiceViewHolder.create(parent, isSend, itemClickListener, itemStyle);
            case VIDEO :
                return EaseVideoViewHolder.create(parent, isSend, itemClickListener, itemStyle);
            case FILE :
                return EaseFileViewHolder.create(parent, isSend, itemClickListener, itemStyle);
            case EXPRESSION:
                return EaseExpressionViewHolder.create(parent, isSend, itemClickListener, itemStyle);
            default:
                return EaseTextViewHolder.create(parent, isSend, itemClickListener, itemStyle);
        }

    }
}
