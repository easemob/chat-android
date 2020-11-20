package com.hyphenate.easeui.modules.chat.presenter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.modules.ILoadDataView;

public interface IHandleMessageView extends ILoadDataView {
    /**
     * 生成视频封面失败
     * @param message
     */
    void createThumbFileFail(String message);

    /**
     * 发送消息失败
     * @param message
     */
    void sendMessageFail(String message);

    /**
     * 完成发送消息动作
     * @param message
     */
    void sendMessageFinish(EMMessage message);

    /**
     * 转发消息失败
     * @param message
     */
    void sendForwardMsgFail(String message);

    /**
     * 转发消息成功
     * @param message
     */
    void sendForwardMsgFinish(EMMessage message);
}
