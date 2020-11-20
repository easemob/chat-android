package com.hyphenate.easeui.modules.chat;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.chat.interfaces.ChatInputMenuListener;
import com.hyphenate.easeui.modules.chat.interfaces.IChatLayout;
import com.hyphenate.easeui.modules.chat.interfaces.OnChatLayoutListener;
import com.hyphenate.easeui.modules.chat.presenter.EaseHandleMessagePresenter;
import com.hyphenate.easeui.modules.chat.presenter.EaseHandleMessagePresenterImpl;
import com.hyphenate.easeui.modules.chat.presenter.IHandleMessageView;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.List;

public class EaseChatLayout extends RelativeLayout implements IChatLayout, IHandleMessageView
        , ChatInputMenuListener, EMMessageListener, EaseChatMessageListLayout.OnMessageTouchListener {
    private static final String TAG = EaseChatLayout.class.getSimpleName();
    private static final int MSG_TYPING_BEGIN = 0;
    private static final int MSG_TYPING_END = 1;

    public static final String ACTION_TYPING_BEGIN = "TypingBegin";
    public static final String ACTION_TYPING_END = "TypingEnd";
    protected static final int TYPING_SHOW_TIME = 5000;

    private EaseChatMessageListLayout layoutChatMessage;
    private EaseChatInputMenu layoutMenu;
    private EaseVoiceRecorderView voiceRecorder;
    /**
     * "正在输入"功能的开关，打开后本设备发送消息将持续发送cmd类型消息通知对方"正在输入"
     */
    private boolean turnOnTyping;
    /**
     * 用于处理用户是否正在输入的handler
     */
    private Handler typingHandler;
    /**
     * 对方的环信id
     */
    private String toChatUsername;
    /**
     * 聊天类型
     */
    private int chatType;
    /**
     * 用于监听消息的变化
     */
    private OnChatLayoutListener listener;
    private EaseHandleMessagePresenter presenter;

    public EaseChatLayout(Context context) {
        this(context, null);
    }

    public EaseChatLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        presenter = new EaseHandleMessagePresenterImpl();
        if(context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).getLifecycle().addObserver(presenter);
        }
        LayoutInflater.from(context).inflate(R.layout.ease_layout_chat, this);
        initView();
        initListener();
    }

    private void initView() {
        layoutChatMessage = findViewById(R.id.layout_chat_message);
        layoutMenu = findViewById(R.id.layout_menu);
        voiceRecorder = findViewById(R.id.voice_recorder);

        presenter.attachView(this);
    }

    private void initListener() {
        layoutChatMessage.setOnMessageTouchListener(this);
        layoutMenu.setChatInputMenuListener(this);
        getChatManager().addMessageListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getChatManager().removeMessageListener(this);
    }

    /**
     * 初始化
     * @param username 环信id
     * @param chatType 聊天类型，单聊，群聊或者聊天室
     */
    public void init(String username, int chatType) {
        init(EaseChatMessageListLayout.LoadDataType.LOCAL, username, chatType);
    }

    /**
     * 初始化
     * @param loadDataType 加载数据模式
     * @param username 环信id
     * @param chatType 聊天类型，单聊，群聊或者聊天室
     */
    public void init(EaseChatMessageListLayout.LoadDataType loadDataType, String username, int chatType) {
        this.toChatUsername = username;
        this.chatType = chatType;
        layoutChatMessage.init(loadDataType, toChatUsername, chatType);
        presenter.setupWithToUser(chatType, toChatUsername);
        initTypingHandler();
    }

    /**
     * 初始化历史消息搜索模式
     * @param toChatUsername
     * @param chatType
     */
    public void initHistoryModel(String toChatUsername, int chatType) {
        init(EaseChatMessageListLayout.LoadDataType.HISTORY, toChatUsername, chatType);
    }

    public void loadDefaultData() {
        layoutChatMessage.loadDefaultData();
    }

    public void loadData(String msgId, int pageSize) {
        layoutChatMessage.loadData(pageSize, msgId);
    }

    private void initTypingHandler() {
        if(turnOnTyping) {
            typingHandler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
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
        }else {
            if(typingHandler != null) {
                typingHandler.removeCallbacksAndMessages(null);
            }
        }
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
            presenter.sendCmdMessage(ACTION_TYPING_BEGIN);
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
        presenter.sendCmdMessage(ACTION_TYPING_END);
    }

    @Override
    public EaseChatMessageListLayout getChatMessageListLayout() {
        return layoutChatMessage;
    }

    @Override
    public EaseChatInputMenu getChatInputMenu() {
        return layoutMenu;
    }

    @Override
    public void turnOnTypingMonitor(boolean turnOn) {
        this.turnOnTyping = turnOn;
        initTypingHandler();
    }

    @Override
    public void sendTextMessage(String content) {
        presenter.sendTextMessage(content);
    }

    @Override
    public void sendTextMessage(String content, boolean isNeedGroupAck) {
        presenter.sendTextMessage(content, isNeedGroupAck);
    }

    @Override
    public void sendAtMessage(String content) {
        presenter.sendAtMessage(content);
    }

    @Override
    public void sendBigExpressionMessage(String name, String identityCode) {
        presenter.sendBigExpressionMessage(name, identityCode);
    }

    @Override
    public void sendVoiceMessage(String filePath, int length) {
        sendVoiceMessage(Uri.parse(filePath), length);
    }

    @Override
    public void sendVoiceMessage(Uri filePath, int length) {
        presenter.sendVoiceMessage(filePath, length);
    }

    @Override
    public void sendImageMessage(Uri imageUri) {
        presenter.sendImageMessage(imageUri);
    }

    @Override
    public void sendImageMessage(Uri imageUri, boolean sendOriginalImage) {
        presenter.sendImageMessage(imageUri, sendOriginalImage);
    }

    @Override
    public void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        presenter.sendLocationMessage(latitude, longitude, locationAddress);
    }

    @Override
    public void sendVideoMessage(Uri videoUri, int videoLength) {
        presenter.sendVideoMessage(videoUri, videoLength);
    }

    @Override
    public void sendFileMessage(Uri fileUri) {
        presenter.sendFileMessage(fileUri);
    }

    @Override
    public void sendMessage(EMMessage message) {
        presenter.sendMessage(message);
    }

    @Override
    public void sendForwardMsg(String forwardMsgId) {
        presenter.sendForwardMsg(forwardMsgId);
    }

    @Override
    public void addMessageAttributes(EMMessage message) {
        presenter.addMessageAttributes(message);
    }

    @Override
    public void setOnChatLayoutListener(OnChatLayoutListener listener) {
        this.listener = listener;
    }

    @Override
    public void onTyping(CharSequence s, int start, int before, int count) {
        if(typingHandler != null) {
            typingHandler.sendEmptyMessage(MSG_TYPING_BEGIN);
        }
    }

    @Override
    public void onSendMessage(String content) {
        presenter.sendTextMessage(content);
    }

    @Override
    public void onExpressionClicked(Object emojicon) {
        if(emojicon instanceof EaseEmojicon) {
            presenter.sendBigExpressionMessage(((EaseEmojicon) emojicon).getName(), ((EaseEmojicon) emojicon).getIdentityCode());
        }
    }

    @Override
    public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
        return voiceRecorder.onPressToSpeakBtnTouch(v, event, (this::sendVoiceMessage));
    }

    @Override
    public void onChatExtendMenuItemClick(int itemId, View view) {
        if(listener != null) {
            listener.onChatExtendMenuItemClick(view, itemId);
        }
    }

    private EMChatManager getChatManager() {
        return EMClient.getInstance().chatManager();
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        if(listener != null) {
            EaseEvent event = EaseEvent.create(EaseConstant.MESSAGE_CHANGE_RECEIVE, EaseEvent.TYPE.MESSAGE);
            listener.onMessageChange(event);
        }
        boolean refresh = false;
        for (EMMessage message : messages) {
            String username = null;
            if(message.isNeedGroupAck() && message.isUnread()) {
                try {
                    EMClient.getInstance().chatManager().ackGroupMessageRead(message.getTo(), message.getMsgId(), "");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
            // group message
            if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                // single chat message
                username = message.getFrom();
            }
            // if the message is for current conversation
            if (username.equals(toChatUsername) || message.getTo().equals(toChatUsername) || message.conversationId().equals(toChatUsername)) {
                refresh = true;
            }
        }
        if(refresh) {
            getChatMessageListLayout().refreshToLatest();
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        // 对方是否正在输入的消息回调
        for (final EMMessage msg : messages) {
            final EMCmdMessageBody body = (EMCmdMessageBody) msg.getBody();
            EMLog.i(TAG, "Receive cmd message: " + body.action() + " - " + body.isDeliverOnlineOnly());
            EaseThreadManager.getInstance().runOnMainThread(() -> {
                if(TextUtils.equals(msg.getFrom(), toChatUsername)) {
                    if(listener != null) {
                        listener.onOtherTyping(body.action());
                    }
                }
            });
        }
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        refreshMessages(messages);
    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
        refreshMessages(messages);
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        if(listener != null) {
            EaseEvent event = EaseEvent.create(EaseConstant.MESSAGE_CHANGE_RECALL, EaseEvent.TYPE.MESSAGE);
            listener.onMessageChange(event);
        }
        if(getChatMessageListLayout() != null) {
            getChatMessageListLayout().refreshMessages();
        }
    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {
        if(listener != null) {
            listener.onMessageChange(EaseEvent.create(EaseConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
        }
        refreshMessage(message);
    }

    private void refreshMessage(EMMessage message) {
        if(getChatMessageListLayout() != null) {
            getChatMessageListLayout().refreshMessage(message);
        }
    }

    private void refreshMessages(List<EMMessage> messages) {
        for (EMMessage msg : messages) {
            refreshMessage(msg);
        }
    }

    @Override
    public Context context() {
        return getContext();
    }

    @Override
    public void createThumbFileFail(String message) {

    }

    @Override
    public void sendMessageFail(String message) {
        if(listener != null) {
            listener.onSendMessageError(message);
        }
    }

    @Override
    public void sendMessageFinish(EMMessage message) {
        if(getChatMessageListLayout() != null) {
            getChatMessageListLayout().refreshToLatest();
        }
    }

    @Override
    public void sendForwardMsgFail(String message) {
        if(listener != null) {
            listener.onSendMessageError(message);
        }
    }

    @Override
    public void sendForwardMsgFinish(EMMessage message) {
        if(listener != null) {
            EaseEvent event = EaseEvent.create(EaseConstant.MESSAGE_FORWARD, EaseEvent.TYPE.MESSAGE, context().getString(R.string.has_been_send));
            listener.onMessageChange(event);
        }
    }

    @Override
    public void onTouchItemOutside(View v, int position) {
        layoutMenu.hideSoftKeyboard();
        layoutMenu.showExtendMenu(false);
    }

    @Override
    public void onViewDragging() {
        layoutMenu.hideSoftKeyboard();
        layoutMenu.showExtendMenu(false);
    }
}

