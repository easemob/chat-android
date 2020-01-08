package com.hyphenate.easeui.viewholder;

import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EaseViewHolderHelper {
    private static EaseViewHolderHelper instance;
    private Map<String, int[]> viewTypeMap;
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
    public Map<String, int[]> getViewTypeMap() {
        return viewTypeMap;
    }

    /**
     * 添加view type
     * @param type
     * @return
     */
    public Map<String, int[]> addViewType(String type) {
        // 如果已经包含相应的类型，则不再进行类型添加
        if(messageTypes.contains(type)) {
            return viewTypeMap;
        }
        messageTypes.add(type);
        int[] sparse = new int[2];
        sparse[0] = getSendType(viewTypeMap.size());
        sparse[1] = getReceiveType(viewTypeMap.size());
        viewTypeMap.put(type, sparse);
        return viewTypeMap;
    }

    /**
     * 获取消息类型
     * @param message
     * @return
     */
    public int getDefaultAdapterViewType(EMMessage message) {
        EMMessage.Direct direct = message.direct();
        boolean isSender = direct == EMMessage.Direct.SEND;
        if(message.getType() == EMMessage.Type.TXT) {
            if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                int[] viewType = getViewTypeMap().get(EaseConstant.MESSAGE_TYPE_EXPRESSION);
                if(viewType != null) {
                    return isSender ? viewType[0] : viewType[1];
                }
            }
        }
        int viewType = 0;
        int[] messageViewTypes = getViewTypeMap().get(message.getType().name());
        if(messageViewTypes != null) {
            viewType = isSender ? messageViewTypes[0] : messageViewTypes[1];
        }
        return viewType;
    }

    public SparseArray<EaseChatRowViewHolder> getDefaultChatViewHolder(ViewGroup parent, MessageListItemClickListener listener, EaseMessageListItemStyle itemStyle) {
        SparseArray<EaseChatRowViewHolder> sparseArray = new SparseArray<>();
        Iterator<Map.Entry<String, int[]>> iterator = viewTypeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, int[]> next = iterator.next();
            int[] viewType = next.getValue();
            sparseArray.put(viewType[0], getItemChatViewHolder(next.getKey(), parent, true, listener, itemStyle));
            sparseArray.put(viewType[1], getItemChatViewHolder(next.getKey(), parent, false, listener, itemStyle));
        }
        return sparseArray;
    }

    public EaseChatRowViewHolder getChatRowViewHolder(ViewGroup parent, int viewType, MessageListItemClickListener listener, EaseMessageListItemStyle itemStyle) {
        Iterator<Map.Entry<String, int[]>> iterator = viewTypeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, int[]> next = iterator.next();
            int[] value = next.getValue();
            if(value[0] == viewType) {
                return getItemChatViewHolder(next.getKey(), parent, true, listener, itemStyle);
            }else if(value[1] == viewType) {
                return getItemChatViewHolder(next.getKey(), parent, false, listener, itemStyle);
            }
        }
        return null;
    }

    private EaseChatRowViewHolder getItemChatViewHolder(String key, ViewGroup parent, boolean isSender, MessageListItemClickListener listener, EaseMessageListItemStyle itemStyle) {
        if(TextUtils.equals(key, EMMessage.Type.IMAGE.name())) {
            return EaseImageViewHolder.create(parent, isSender, listener, itemStyle);
        }else if(TextUtils.equals(key, EMMessage.Type.VIDEO.name())) {
            return EaseVideoViewHolder.create(parent, isSender, listener, itemStyle);
        }else if(TextUtils.equals(key, EMMessage.Type.LOCATION.name())) {
            return EaseLocationViewHolder.create(parent, isSender, listener, itemStyle);
        }else if(TextUtils.equals(key, EMMessage.Type.VOICE.name())) {
            return EaseVoiceViewHolder.create(parent, isSender, listener, itemStyle);
        }else if(TextUtils.equals(key, EMMessage.Type.FILE.name())) {
            return EaseFileViewHolder.create(parent, isSender, listener, itemStyle);
        }else if(TextUtils.equals(key, EaseConstant.MESSAGE_TYPE_EXPRESSION)) {
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
        int[] sparse;
        for(int i = 0; i < messageTypes.size(); i++) {
            String type = messageTypes.get(i);
            sparse = new int[2];
            sparse[0] = getSendType(i);
            sparse[1] = getReceiveType(i);
            viewTypeMap.put(type, sparse);
        }
        addViewType(EaseConstant.MESSAGE_TYPE_EXPRESSION);
    }

    /**
     * set send type by position
     * @param position
     * @return
     */
    private int getSendType(int position) {
        return 100 + position;
    }

    /**
     * set receive type by position
     * @param position
     * @return
     */
    private int getReceiveType(int position) {
        return 10000 + position;
    }

}
