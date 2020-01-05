package com.hyphenate.easeui.ui.chat;

import android.os.Handler;
import android.os.Message;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.util.EMLog;

import java.util.List;

import androidx.annotation.NonNull;

public class EaseSingleChatFragment extends EaseChatFragment {
    protected static final int MSG_TYPING_BEGIN = 0;
    protected static final int MSG_TYPING_END = 1;
    protected static final String ACTION_TYPING_BEGIN = "TypingBegin";
    protected static final String ACTION_TYPING_END = "TypingEnd";
    protected static final int TYPING_SHOW_TIME = 5000;
    private static final String TAG = EaseSingleChatFragment.class.getSimpleName();
    /**
     * "正在输入"功能的开关，打开后本设备发送消息将持续发送cmd类型消息通知对方"正在输入"
     */
    private boolean turnOnTyping;
    private Handler typingHandler;

    @Override
    protected void initChildArguments() {
        super.initChildArguments();
        chatType = EaseConstant.CHATTYPE_SINGLE;
        emMsgChatType = EMMessage.ChatType.Chat;
        this.turnOnTyping = openTurnOnTyping();
    }

    @Override
    protected void initChildData() {
        super.initChildData();
        ((EaseMessageAdapter)messageAdapter).showUserNick(false);
        setTypingHandler();
    }

    @Override
    public void onTyping(CharSequence s, int start, int before, int count) {
        super.onTyping(s, start, before, count);
        // send action:TypingBegin cmd msg.
        typingHandler.sendEmptyMessage(MSG_TYPING_BEGIN);
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        super.onCmdMessageReceived(messages);
        for (final EMMessage msg : messages) {
            final EMCmdMessageBody body = (EMCmdMessageBody) msg.getBody();
            EMLog.i(TAG, "Receive cmd message: " + body.action() + " - " + body.isDeliverOnlineOnly());
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ACTION_TYPING_BEGIN.equals(body.action()) && msg.getFrom().equals(toChatUsername)) {
                        setTitleBarText(getString(R.string.alert_during_typing));
                    } else if (ACTION_TYPING_END.equals(body.action()) && msg.getFrom().equals(toChatUsername)) {
                        setTitleBarText(toChatUsername);
                    }
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        typingHandler.sendEmptyMessage(MSG_TYPING_END);
    }

    /**
     * 用于控制，是否告诉对方，你正在输入中
     * @return
     */
    protected boolean openTurnOnTyping() {
        return false;
    }

    private void setTypingHandler() {
        typingHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_TYPING_BEGIN :
                        setTypingBeginMsg(this);
                        break;
                    case MSG_TYPING_END :
                        setTypingEndMsg(this);
                        break;
                }

            }
        };
    }

    /**
     * 处理“正在输入”开始
     * @param handler
     */
    private void setTypingBeginMsg(Handler handler) {
        if (!turnOnTyping) return;
        // Only support single-chat type conversation.
        if (chatType != EaseConstant.CHATTYPE_SINGLE)
            return;
        if (handler.hasMessages(MSG_TYPING_END)) {
            // reset the MSG_TYPING_END handler msg.
            handler.removeMessages(MSG_TYPING_END);
        } else {
            // Send TYPING-BEGIN cmd msg
            EMMessage beginMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
            EMCmdMessageBody body = new EMCmdMessageBody(ACTION_TYPING_BEGIN);
            // Only deliver this cmd msg to online users
            body.deliverOnlineOnly(true);
            beginMsg.addBody(body);
            beginMsg.setTo(toChatUsername);
            EMClient.getInstance().chatManager().sendMessage(beginMsg);
        }
        handler.sendEmptyMessageDelayed(MSG_TYPING_END, TYPING_SHOW_TIME);
    }

    /**
     * 处理“正在输入”结束
     * @param handler
     */
    private void setTypingEndMsg(Handler handler) {
        if (!turnOnTyping) return;

        // Only support single-chat type conversation.
        if (chatType != EaseConstant.CHATTYPE_SINGLE)
            return;

        // remove all pedding msgs to avoid memory leak.
        handler.removeCallbacksAndMessages(null);
        // Send TYPING-END cmd msg
        EMMessage endMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        EMCmdMessageBody body = new EMCmdMessageBody(ACTION_TYPING_END);
        // Only deliver this cmd msg to online users
        body.deliverOnlineOnly(true);
        endMsg.addBody(body);
        endMsg.setTo(toChatUsername);
        EMClient.getInstance().chatManager().sendMessage(endMsg);
    }
}
