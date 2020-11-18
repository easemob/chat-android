package com.hyphenate.easeui.modules.chat.interfaces;

import android.graphics.drawable.Drawable;
import android.widget.EditText;

public interface IChatPrimaryMenu {
    /**
     * 菜单展示类型
     * @param type
     */
    void SetMenuShowType(int type);

    /**
     * 常规模式
     */
    void showNormalStatus();

    /**
     * 文本输入模式
     */
    void showTextStatus();

    /**
     * 语音输入模式
     */
    void showVoiceStatus();

    /**
     * 表情输入模式
     */
    void showEmojiconStatus();

    /**
     * 更多模式
     */
    void showMoreStatus();

    /**
     * 输入表情
     * @param emojiContent
     */
    void onEmojiconInputEvent(CharSequence emojiContent);

    /**
     * 删除表情
     */
    void onEmojiconDeleteEvent();

    /**
     * 输入文本
     * @param text
     */
    void onTextInsert(CharSequence text);

    /**
     * 获取EditText
     * @return
     */
    EditText getEditText();

    /**
     * 设置输入框背景
     * @param bg
     */
    void setMenuBackground(Drawable bg);
}
