package com.hyphenate.easeui.viewholder;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EaseViewHolderHelper {
    private static final int BASE_SEND_TYPE = 100;
    private static final int BASE_RECEIVE_TYPE = 10000;
    private static EaseViewHolderHelper instance;
    /**
     * key为类型，value为sendViewType
     */
    private Map<String, Integer> viewTypeMap;
    /**
     * key为sendViewType，value为messageType
     */
    private Map<Integer, String> viewTypeMap2;
    private String[] defaultExtendMessageType = {EaseConstant.MESSAGE_TYPE_EXPRESSION};
    private List<String> messageTypes;

    private EaseViewHolderHelper() {
        initMessageTypeList();
        initViewTypeMap();
    }

    public static EaseViewHolderHelper getInstance() {
        if(instance == null) {
            synchronized (EaseViewHolderHelper.class) {
                if(instance == null) {
                    instance = new EaseViewHolderHelper();
                }
            }
        }
        return instance;
    }

    /**
     * get view type map
     * @return
     */
    public Map<String, Integer> getViewTypeMap() {
        return viewTypeMap;
    }

    /**
     * 添加view type
     * @param type
     * @return
     */
    public Map<String, Integer> addViewType(String type) {
        // 如果已经包含相应的类型，则不再进行类型添加
        if(messageTypes.contains(type)) {
            return viewTypeMap;
        }
        messageTypes.add(type);
        viewTypeMap.put(type, getSendType(viewTypeMap.size()));
        viewTypeMap2.put(getSendType(viewTypeMap2.size()), type);
        return viewTypeMap;
    }

    /**
     * 获取消息类型
     * @param message
     * @return
     */
    public int getAdapterViewType(EMMessage message) {
        return getAdapterViewType(message, null);
    }

    /**
     * 获取消息类型
     * @param message
     * @return
     */
    public int getAdapterViewType(EMMessage message, addMoreMessageTypeProvider provider) {
        EMMessage.Direct direct = message.direct();
        boolean isSender = direct == EMMessage.Direct.SEND;
        if(provider != null) {
            int messageType = provider.addMoreMessageType(message, getViewTypeMap());
            if(messageType != 0) {
                return messageType;
            }
        }
        if(message.getType() == EMMessage.Type.TXT) {
            if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                int viewType = 0;
                try {
                    viewType = getViewTypeMap().get(EaseConstant.MESSAGE_TYPE_EXPRESSION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(viewType != 0) {
                    return isSender ? viewType : getReceiveType(viewType);
                }
            }
        }
        int viewType = 0;
        try {
            viewType = getViewTypeMap().get(message.getType().name());
            return isSender ? viewType : getReceiveType(viewType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewType;
    }

    public EaseChatRowViewHolder getChatRowViewHolder(ViewGroup parent, int viewType,
                                                      MessageListItemClickListener listener, EaseMessageListItemStyle itemStyle) {
        return getChatRowViewHolder(parent, viewType, listener, itemStyle, null);
    }

    public EaseChatRowViewHolder getChatRowViewHolder(ViewGroup parent, int viewType,
                                                      MessageListItemClickListener listener,
                                                      EaseMessageListItemStyle itemStyle,
                                                      ExtendViewHolderProvider addMoreViewHolder) {
        String type = null;
        boolean isSender = false;
        if(viewTypeMap2.keySet().contains(viewType)) {
            type = viewTypeMap2.get(viewType);
            isSender = true;
        }else if(viewTypeMap2.keySet().contains(getSendTypeByReceive(viewType))) {
            type = viewTypeMap2.get(getSendTypeByReceive(viewType));
        }
        if(TextUtils.isEmpty(type)) {
            return null;
        }
        if(addMoreViewHolder != null) {
            EaseChatRowViewHolder viewHolder = addMoreViewHolder.addMoreViewHolder(parent, type, isSender, listener, itemStyle);
            if(viewHolder != null) {
                return viewHolder;
            }
        }
        if(TextUtils.equals(type, EMMessage.Type.IMAGE.name())) {
            return EaseImageViewHolder.create(parent, isSender, listener, itemStyle);
        }else if(TextUtils.equals(type, EMMessage.Type.VIDEO.name())) {
            return EaseVideoViewHolder.create(parent, isSender, listener, itemStyle);
        }else if(TextUtils.equals(type, EMMessage.Type.LOCATION.name())) {
            return EaseLocationViewHolder.create(parent, isSender, listener, itemStyle);
        }else if(TextUtils.equals(type, EMMessage.Type.VOICE.name())) {
            return EaseVoiceViewHolder.create(parent, isSender, listener, itemStyle);
        }else if(TextUtils.equals(type, EMMessage.Type.FILE.name())) {
            return EaseFileViewHolder.create(parent, isSender, listener, itemStyle);
        }else if(TextUtils.equals(type, EaseConstant.MESSAGE_TYPE_EXPRESSION)) {
            return EaseExpressionViewHolder.create(parent, isSender, listener, itemStyle);
        }else {
            return EaseTextViewHolder.create(parent, isSender, listener, itemStyle);
        }
    }

    private void initMessageTypeList() {
        messageTypes = new ArrayList<>();
        EMMessage.Type[] values = EMMessage.Type.values();
        for (EMMessage.Type type : values) {
            messageTypes.add(type.name());
        }
        messageTypes.addAll(Arrays.asList(defaultExtendMessageType));
    }

    private void initViewTypeMap() {
        viewTypeMap = new HashMap<>();
        viewTypeMap2 = new HashMap<>();
        for(int i = 0; i < messageTypes.size(); i++) {
            String type = messageTypes.get(i);
            viewTypeMap.put(type, getSendType(i));
            viewTypeMap2.put(getSendType(i), type);
        }
    }

    /**
     * set send type by position
     * @param position
     * @return
     */
    private int getSendType(int position) {
        return BASE_SEND_TYPE + position;
    }

    /**
     * get send type by receive type
     * @param receiveType
     * @return
     */
    private int getSendTypeByReceive(int receiveType) {
        return receiveType - BASE_RECEIVE_TYPE;
    }

    /**
     * set receive type by position
     * @param sendViewType
     * @return
     */
    public int getReceiveType(int sendViewType) {
        return BASE_RECEIVE_TYPE + sendViewType;
    }

    /**
     * 提供更多的ViewHolder
     */
    public interface ExtendViewHolderProvider {
        /**
         * 返回自定义的ViewHolder
         * @param parent
         * @param type
         * @param isSender
         * @param listener
         * @param itemStyle
         * @return
         */
        EaseChatRowViewHolder addMoreViewHolder(ViewGroup parent, String type, boolean isSender,
                                                MessageListItemClickListener listener, EaseMessageListItemStyle itemStyle);

    }

    /**
     * 提供更多的消息类型
     */
    public interface addMoreMessageTypeProvider {
        int addMoreMessageType(EMMessage message, Map<String, Integer> viewTypeMap);
    }

}
