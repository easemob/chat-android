package com.hyphenate.chatuidemo.section.chat;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.DemoHelper;

import java.util.List;

public class ChatPresenter implements EMMessageListener {

    public ChatPresenter() {
        DemoHelper.getInstance().getChatManager().addMessageListener(this);
    }

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onMessageReceived(List<EMMessage> messages) {

    }

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {

    }

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onMessageRead(List<EMMessage> messages) {

    }

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onMessageDelivered(List<EMMessage> messages) {

    }

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onMessageRecalled(List<EMMessage> messages) {

    }

    /**
     * EMMessageListener
     * @param message
     * @param change
     */
    @Override
    public void onMessageChanged(EMMessage message, Object change) {

    }
}
