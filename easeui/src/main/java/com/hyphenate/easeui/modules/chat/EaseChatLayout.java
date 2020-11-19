package com.hyphenate.easeui.modules.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.modules.chat.interfaces.IChatLayout;

public class EaseChatLayout extends RelativeLayout implements IChatLayout {

    private EaseChatMessageListLayout layoutChatMessage;
    private EaseChatInputMenu layoutMenu;

    public EaseChatLayout(Context context) {
        this(context, null);
    }

    public EaseChatLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.ease_layout_chat, this);
        initView();
    }

    private void initView() {
        layoutChatMessage = findViewById(R.id.layout_chat_message);
        layoutMenu = findViewById(R.id.layout_menu);
    }

    /**
     * 初始化
     * @param username 环信id
     * @param chatType 聊天类型，单聊，群聊或者聊天室
     */
    public void init(String username, int chatType) {
        init(EaseChatMessageListLayout.LoadDataType.LOCAL, username, chatType);
    }

    /**
     * 初始化
     * @param loadDataType 加载数据模式
     * @param username 环信id
     * @param chatType 聊天类型，单聊，群聊或者聊天室
     */
    public void init(EaseChatMessageListLayout.LoadDataType loadDataType, String username, int chatType) {
        layoutChatMessage.init(loadDataType, username, chatType);
        layoutChatMessage.loadDefaultData();
    }

    @Override
    public EaseChatMessageListLayout getChatMessageListLayout() {
        return layoutChatMessage;
    }

    @Override
    public EaseChatInputMenu getChatInputMenu() {
        return layoutMenu;
    }
}

