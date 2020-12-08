package com.hyphenate.easeui.provider;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntegerRes;

import java.util.Map;

public interface EaseConversationInfoProvider {
    /**
     * 获取默认类型头像
     * @param type
     * @return
     */
    String getDefaultTypeAvatar(String type);

    /**
     * 获取默认类型头像
     * @param type
     * @return
     */
    @DrawableRes int getDefaultTypeAvatarResource(String type);

    /**
     * 获取会话名称
     * @param username
     * @return
     */
    String getConversationName(String username);
}
