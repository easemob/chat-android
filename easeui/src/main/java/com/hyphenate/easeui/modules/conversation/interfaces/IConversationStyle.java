package com.hyphenate.easeui.modules.conversation.interfaces;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easeui.modules.conversation.EaseConversationListLayout;
import com.hyphenate.easeui.modules.interfaces.IAvatarSet;

public interface IConversationStyle extends IAvatarSet, IConversationTextStyle {

    /**
     * 设置条目背景
     * @param backGround
     */
    void setItemBackGround(Drawable backGround);

    /**
     * 设置条目高度
     * @param height
     */
    void setItemHeight(int height);

    /**
     * 是否展示未读红点
     * @param hide
     */
    void hideUnreadDot(boolean hide);
}
