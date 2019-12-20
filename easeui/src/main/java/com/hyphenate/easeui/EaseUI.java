package com.hyphenate.easeui;

import com.hyphenate.easeui.provider.EaseEmojiconInfoProvider;
import com.hyphenate.easeui.provider.EaseSettingsProvider;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;

public class EaseUI {
    private static EaseUI instance;

    private EaseSettingsProvider settingsProvider;

    private EaseEmojiconInfoProvider mEmojiconInfoProvider;

    private EaseUserProfileProvider userProvider;

    private EaseUI() {}

    public static EaseUI getInstance() {
        if(instance == null) {
            synchronized (EaseUI.class) {
                if(instance == null) {
                    instance = new EaseUI();
                }
            }
        }
        return instance;
    }

    /**
     * get emojicon provider
     * @return
     */
    public EaseEmojiconInfoProvider getEmojiconInfoProvider() {
        return mEmojiconInfoProvider;
    }

    /**
     * set emojicon provider
     * @param emojiconInfoProvider
     * @return
     */
    public EaseUI setEmojiconInfoProvider(EaseEmojiconInfoProvider emojiconInfoProvider) {
        mEmojiconInfoProvider = emojiconInfoProvider;
        return this;
    }

    /**
     * get settings provider
     * @return
     */
    public EaseSettingsProvider getSettingsProvider() {
        return settingsProvider;
    }

    /**
     * set settting provider
     * @param settingsProvider
     * @return
     */
    public EaseUI setSettingsProvider(EaseSettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
        return this;
    }

    /**
     * get user profile provider
     * @return
     */
    public EaseUserProfileProvider getUserProvider() {
        return userProvider;
    }

    /**
     * set user profile provider
     * @param userProvider
     * @return
     */
    public EaseUI setUserProvider(EaseUserProfileProvider userProvider) {
        this.userProvider = userProvider;
        return this;
    }
}
