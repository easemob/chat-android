package com.hyphenate.easeui.modules.chat.interfaces;

import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;

import com.hyphenate.easeui.modules.interfaces.IAvatarSet;

public interface IChatMessageItemSet extends IAvatarSet {
    /**
     * 是否展示昵称
     * @param showNickname
     */
    void showNickname(boolean showNickname);

    /**
     * 设置条目背景
     * @param bgDrawable
     */
    void setItemBackground(Drawable bgDrawable);

    /**
     * 设置文本消息字体大小
     * @param textSize
     */
    void setItemTextSize(int textSize);

    /**
     * 设置文本消息字体颜色
     * @param textColor
     */
    void setItemTextColor(@ColorInt int textColor);

    /**
     * 设置文本消息条目的最小高度
     * @param height
     */
    void setItemMinHeight(int height);

    /**
     * 设置时间线文本大小
     * @param textSize
     */
    void setTimeTextSize(int textSize);

    /**
     * 设置时间线文本颜色
     * @param textColor
     */
    void setTimeTextColor(int textColor);

    /**
     * 设置时间线背景
     * @param bgDrawable
     */
    void setTimeBackground(Drawable bgDrawable);
}
