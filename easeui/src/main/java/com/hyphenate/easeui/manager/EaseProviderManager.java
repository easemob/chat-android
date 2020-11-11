package com.hyphenate.easeui.manager;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.provider.EaseConversationInfoProvider;
import com.hyphenate.easeui.provider.EaseEmojiconInfoProvider;
import com.hyphenate.easeui.provider.EaseSettingsProvider;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EaseProviderManager {
    private static volatile EaseProviderManager instance;

    private EaseSettingsProvider settingsProvider;

    private EaseEmojiconInfoProvider emojiconInfoProvider;

    private EaseUserProfileProvider userProvider;

    private EaseConversationInfoProvider conversationInfoProvider;

    private EaseProviderManager() {}

    public static EaseProviderManager getInstance() {
        if(instance == null) {
            synchronized (EaseProviderManager.class) {
                if(instance == null) {
                    instance = new EaseProviderManager();
                }
            }
        }
        return instance;
    }

    public EaseSettingsProvider getSettingsProvider() {
        return settingsProvider;
    }

    public void setSettingsProvider(EaseSettingsProvider provider) {
        this.settingsProvider = provider;
    }

    public EaseEmojiconInfoProvider getEmojiconInfoProvider() {
        return emojiconInfoProvider;
    }

    public void setEmojiconInfoProvider(EaseEmojiconInfoProvider provider) {
        this.emojiconInfoProvider = provider;
    }

    public EaseUserProfileProvider getUserProvider() {
        return userProvider;
    }

    public void setUserProvider(EaseUserProfileProvider provider) {
        this.userProvider = provider;
    }

    public EaseConversationInfoProvider getConversationInfoProvider() {
        return conversationInfoProvider;
    }

    public void setConversationInfoProvider(EaseConversationInfoProvider provider) {
        this.conversationInfoProvider = provider;
    }

    private EaseSettingsProvider getDefaultSettingsProvider() {
        return new EaseSettingsProvider() {
            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                return false;
            }

            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                return false;
            }

            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return false;
            }

            @Override
            public boolean isSpeakerOpened() {
                return false;
            }
        };
    }

}
