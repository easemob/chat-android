package com.hyphenate.easeui.modules.chat.interfaces;

import android.view.View;

import com.hyphenate.easeui.model.EaseEvent;

/**
 * 用于监听{@link com.hyphenate.easeui.modules.chat.EaseChatLayout}中的变化
 */
public interface OnChatLayoutListener {
    /**
     * 消息变化事件
     * @param change
     */
    void onMessageChange(EaseEvent change);

    /**
     * 条目点击
     * @param view
     * @param itemId
     */
    void onChatExtendMenuItemClick(View view, int itemId);

    /**
     * 用于监听其他人正在数据事件
     * @param action 输入事件 TypingBegin为开始 TypingEnd为结束
     */
    default void onOtherTyping(String action){}

    /**
     * 发送消息失败
     * @param message
     */
    default void onSendMessageError(String message){}

}