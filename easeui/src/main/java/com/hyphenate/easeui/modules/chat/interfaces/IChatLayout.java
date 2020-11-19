package com.hyphenate.easeui.modules.chat.interfaces;

import com.hyphenate.easeui.modules.chat.EaseChatInputMenu;
import com.hyphenate.easeui.modules.chat.EaseChatMessageListLayout;

public interface IChatLayout {
    /**
     * 获取聊天列表
     * @return
     */
    EaseChatMessageListLayout getChatMessageListLayout();

    /**
     * 获取输入菜单
     * @return
     */
    EaseChatInputMenu getChatInputMenu();
}
