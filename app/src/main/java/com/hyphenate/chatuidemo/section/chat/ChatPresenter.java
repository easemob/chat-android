package com.hyphenate.chatuidemo.section.chat;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.common.livedatas.MessageChangeLiveData;
import com.hyphenate.easeui.constants.EaseConstant;

import java.util.List;

public class ChatPresenter implements EMMessageListener {
    private static ChatPresenter instance;
    private MessageChangeLiveData messageObservable;

    private ChatPresenter() {
        messageObservable = MessageChangeLiveData.getInstance();
        DemoHelper.getInstance().getChatManager().addMessageListener(this);
    }

    public static ChatPresenter getInstance() {
        if(instance == null) {
            synchronized (ChatPresenter.class) {
                if(instance == null) {
                    instance = new ChatPresenter();
                }
            }
        }
        return instance;
    }

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        messageObservable.postValue(EaseConstant.MESSAGE_CHANGE_RECEIVE);
    }

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        messageObservable.postValue(EaseConstant.MESSAGE_CHANGE_CMD_RECEIVE);
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
        // 撤回消息的回调
        for (EMMessage msg : messages) {

        }
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
