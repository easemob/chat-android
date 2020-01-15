package com.hyphenate.easeui.ui.chat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.adapter.EMAChatRoomManagerListener;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseBaseMessageAdapter;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.EaseChatRoomListener;
import com.hyphenate.easeui.interfaces.EaseGroupListener;
import com.hyphenate.easeui.interfaces.IChatAdapterProvider;
import com.hyphenate.easeui.interfaces.IViewHolderProvider;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseDingMessageHelper;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * 说明：
 * 1、如果要提供自己的adapter，可以通过重写{@link #setViewHolderProvider()}，提供自己的adapter
 * 2、如果需要增加自定义的消息类型，可以通过重写{@link #setChatAdapterProvider()} )}来提供自己的ViewHolder
 */
public class EaseChatFragment extends EaseBaseFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, EaseChatInputMenu.ChatInputMenuListener,
        EaseChatExtendMenu.EaseChatExtendMenuItemClickListener, MessageListItemClickListener,
        EMCallBack, EMMessageListener, View.OnTouchListener, TextWatcher {

    private static final String TAG = EaseChatFragment.class.getSimpleName();

    protected static final int MSG_TYPING_BEGIN = 0;
    protected static final int MSG_TYPING_END = 1;

    protected static final String ACTION_TYPING_BEGIN = "TypingBegin";
    protected static final String ACTION_TYPING_END = "TypingEnd";
    protected static final int TYPING_SHOW_TIME = 5000;

    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_DING_MSG = 4;
    protected static final int REQUEST_CODE_SELECT_VIDEO = 11;
    protected static final int REQUEST_CODE_SELECT_FILE = 12;

    protected TextView tvErrorMsg;
    protected SwipeRefreshLayout srlRefresh;
    protected RecyclerView messageList;
    protected EaseChatInputMenu inputMenu;
    protected EaseVoiceRecorderView voiceRecorderView;

    /**
     * 消息类别，自定义
     */
    protected int chatType = EaseConstant.CHATTYPE_SINGLE;
    private String forwardMsgId;
    /**
     * "正在输入"功能的开关，打开后本设备发送消息将持续发送cmd类型消息通知对方"正在输入"
     */
    private boolean turnOnTyping;
    /**
     * 消息类别，SDK定义
     */
    protected String toChatUsername;
    protected EaseBaseMessageAdapter messageAdapter;
    protected File cameraFile;
    /**
     * chat conversation
     */
    protected EMConversation conversation;
    protected boolean isRoaming;
    /**
     * 是否是页面初始化的时候
     */
    private boolean isInitMsg;
    /**
     * load count from db or server
     */
    protected static int PAGE_SIZE = 20;
    private ChatRoomListener chatRoomListener;
    private GroupListener groupListener;
    private Handler typingHandler;
    protected OnMessageChangeListener messageChangeListener;
    private List<EMMessage> currentMessages;
    private IChatTitleProvider titleProvider;//provide title to activity's title bar

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initArguments();
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initArguments() {
        Bundle bundle = getArguments();
        if(bundle != null) {
            isRoaming = bundle.getBoolean("isRoaming", false);
            chatType = bundle.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
            toChatUsername = bundle.getString(EaseConstant.EXTRA_USER_ID);
            forwardMsgId = bundle.getString(EaseConstant.FORWARD_MSG_ID);
            turnOnTyping = openTurnOnTyping();
            initChildArguments();
        }
    }

    private void initView() {
        tvErrorMsg = findViewById(R.id.tv_error_msg);
        srlRefresh = findViewById(R.id.srl_refresh);
        messageList = findViewById(R.id.message_list);
        inputMenu = findViewById(R.id.input_menu);
        voiceRecorderView = findViewById(R.id.voice_recorder);

        initChildView();

        messageList.setLayoutManager(provideLayoutManager());
        messageAdapter = provideMessageAdapter();
        messageList.setAdapter(messageAdapter);

        initInputMenu();
        addExtendInputMenu();
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initListener() {
        tvErrorMsg.setOnClickListener(this);
        srlRefresh.setOnRefreshListener(this);
        inputMenu.setChatInputMenuListener(this);
        messageList.setOnTouchListener(this);
        inputMenu.getPrimaryMenu().getEditText().addTextChangedListener(this);
        setMessageClickListener();
        addGroupListener();
        addChatRoomListener();
        initChildListener();
    }

    private void setMessageClickListener() {
        if(messageAdapter != null) {
            messageAdapter.setListItemClickListener(this);
        }
    }

    private void initData() {
        initConversation();
        initChatType();
        sendForwardMsg();
        refreshMessages();
        hideNickname();
        setTypingHandler();
        initChildData();
    }

    @Override
    public void onClick(View v) {
       if(v.getId() == R.id.tv_error_msg) {
           onChatRoomViewCreation();
       }
    }

    @Override
    public void onChatExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
            case EaseChatInputMenu.ITEM_TAKE_PICTURE :
                selectPicFromCamera();
                break;
            case EaseChatInputMenu.ITEM_PICTURE :
                selectPicFromLocal();
                break;
            case EaseChatInputMenu.ITEM_LOCATION :
                EaseBaiduMapActivity.actionStartForResult(this, REQUEST_CODE_MAP);
                break;
            case EaseChatInputMenu.ITEM_VIDEO:
                selectVideoFromLocal();
                break;
            case EaseChatInputMenu.ITEM_FILE:
                selectFileFromLocal();
                break;
        }
    }

    @Override
    public void onRefresh() {
        loadMoreMessages(PAGE_SIZE, isRoaming);
    }

    /**
     * input menu listener
     * when typing on the edit-text layout.
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTyping(CharSequence s, int start, int before, int count) {
        // send action:TypingBegin cmd msg.
        typingHandler.sendEmptyMessage(MSG_TYPING_BEGIN);
    }

    /**
     * input menu listener
     * when send message button pressed
     * @param content
     */
    @Override
    public void onSendMessage(String content) {
        sendTextMessage(content);
    }

    /**
     * input menu listener
     * when big icon pressed
     * @param emojicon
     */
    @Override
    public void onBigExpressionClicked(EaseEmojicon emojicon) {
        sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
    }

    /**
     * input menu listener
     * when speak button is touched
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
        return voiceRecorderView.onPressToSpeakBtnTouch(v, event, (this::sendVoiceMessage));
    }

    /**
     * MessageListItemClickListener
     * @param message
     * @return
     */
    @Override
    public boolean onBubbleClick(EMMessage message) {
        return false;
    }

    /**
     * MessageListItemClickListener
     * @param message
     * @return
     */
    @Override
    public boolean onResendClick(EMMessage message) {
        EMLog.i(TAG, "onResendClick");
        new EaseAlertDialog(getContext(), R.string.resend, R.string.confirm_resend, null, new EaseAlertDialog.AlertDialogUser() {
            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (!confirmed) {
                    return;
                }
                message.setStatus(EMMessage.Status.CREATE);
                sendMessage(message);
            }
        }, true).show();
        return true;
    }

    /**
     * MessageListItemClickListener
     * @param v
     * @param message
     */
    @Override
    public void onBubbleLongClick(View v, EMMessage message) {

    }

    /**
     * MessageListItemClickListener
     * @param username
     */
    @Override
    public void onUserAvatarClick(String username) {
        // 跳转逻辑由开发者自行处理

    }

    /**
     * MessageListItemClickListener
     * @param username
     */
    @Override
    public void onUserAvatarLongClick(String username) {
        // 具体逻辑
        inputAtUsername(username, true);
    }

    /**
     * MessageListItemClickListener
     * @param message
     */
    @Override
    public void onMessageInProgress(EMMessage message) {
        message.setMessageStatusCallback(this);
    }

    /**
     * message status callback
     */
    @Override
    public void onSuccess() {
        if(messageChangeListener != null) {
            messageChangeListener.onMessageChange(EaseConstant.MESSAGE_CHANGE_SEND_SUCCESS);
        }
        EMLog.i(TAG, "send message success");
        refreshMessages();
    }

    /**
     * message status callback
     */
    @Override
    public void onError(int code, String error) {
        if(messageChangeListener != null) {
            messageChangeListener.onMessageChange(EaseConstant.MESSAGE_CHANGE_SEND_ERROR);
        }
        EMLog.i(TAG, "send message error = "+error);
        refreshMessages();
    }

    /**
     * message status callback
     */
    @Override
    public void onProgress(int progress, String status) {
        if(messageChangeListener != null) {
            messageChangeListener.onMessageChange(EaseConstant.MESSAGE_CHANGE_SEND_PROGRESS);
        }
        EMLog.i(TAG, "send message on progress");
        refreshMessages();
    }

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        if(messageChangeListener != null) {
            messageChangeListener.onMessageChange(EaseConstant.MESSAGE_CHANGE_RECEIVE);
        }
        boolean refresh = false;
        for (EMMessage message : messages) {
            String username = null;
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
            refreshToLatest();
        }

    }

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        // 对方是否正在输入的消息回调
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

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onMessageRead(List<EMMessage> messages) {
        refreshMessages();
    }

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
        refreshMessages();
    }

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        if(messageChangeListener != null) {
            messageChangeListener.onMessageChange(EaseConstant.MESSAGE_CHANGE_RECALL);
        }
        refreshMessages();
    }

    /**
     * EMMessageListener
     * @param message
     * @param change
     */
    @Override
    public void onMessageChanged(EMMessage message, Object change) {
        if(messageChangeListener != null) {
            messageChangeListener.onMessageChange(EaseConstant.MESSAGE_CHANGE_CHANGE);
        }
        refreshMessages();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hideKeyboard();
        inputMenu.hideExtendMenuContainer();
        return false;
    }

    /**
     * inputMenu addTextChangedListener
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * inputMenu addTextChangedListener
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(count == 1 && "@".equals(String.valueOf(s.charAt(start)))){
//            startActivityForResult(new Intent(getActivity(), PickAtUserActivity.class).
//                    putExtra("groupId", toChatUsername), REQUEST_CODE_SELECT_AT_USER);

        }
    }

    /**
     * inputMenu addTextChangedListener
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {

    }

//============================ child init start ================================
    /**
     * init child arguments
     */
    protected void initChildArguments() {}


    /**
     * init child view
     */
    protected void initChildView() {}

    /**
     * init child listener
     */
    protected void initChildListener() {}

    /**
     * init child data
     */
    protected void initChildData() {}

    /**
     * developer can override the method to change default chat extend menu items
     */
    protected void initInputMenu() {
        inputMenu.registerDefaultMenuItems(this);
    }

    /**
     * developer can add extend menu item by override the method
     */
    protected void addExtendInputMenu() {
        // inputMenu.registerExtendMenuItem(nameRes, drawableRes, itemId, listener);
    }

    /**
     * init chat conversation
     */
    protected void initConversation() {
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername
                , EaseCommonUtils.getConversationType(chatType), true);
        // make all message as read
        if(messageChangeListener != null) {
            int count = conversation.getUnreadMsgCount();
            if(count > 0) {
                messageChangeListener.onMessageChange(EaseConstant.CONVERSATION_READ);
            }
        }

        conversation.markAllMessagesAsRead();

        isInitMsg = true;
        //如果设置为漫游
        if(isRoaming) {
            loadMoreServerMessages(PAGE_SIZE);
            return;
        }
        // 非漫游，从本地数据库拉取数据
        loadMessagesFromLocal();
    }

//============================ child init end ================================

//============================== view control start ===========================


    private void initChatType() {
        if(isSingleChat()) {
            setTitleBarText(toChatUsername);
        }else if(isGroupChat()) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group != null){
                setTitleBarText(group.getGroupName());
            }
        }else if(isChatRoomChat()) {
            onChatRoomViewCreation();
        }
    }

    /**
     * set titleBar title
     * @param title
     */
    protected void setTitleBarText(String title) {
        if(titleProvider != null) {
            titleProvider.provideTitle(chatType, title);
        }
    }

    protected void finishRefresh() {
        if(srlRefresh != null) {
            srlRefresh.setRefreshing(false);
        }
    }

    /**
     * provide recyclerView LayoutManager
     * @return
     */
    protected RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(mContext);
    }

    /**
     * provide message adapter
     * @return
     */
    protected EaseBaseMessageAdapter<EMMessage> provideMessageAdapter() {
        IChatAdapterProvider adapterProvider = setChatAdapterProvider();
        if(adapterProvider != null) {
            return adapterProvider.provideMessageAdaper();
        }
        return new EaseMessageAdapter(setViewHolderProvider());
    }

    /**
     * show msg toast
     * @param message
     */
    protected void showMsgToast(String message) {
        // developer can show the message by your own style
    }

    /**
     * no more message
     */
    protected void showNoMoreMsgToast() {
        showMsgToast(getResources().getString(R.string.no_more_messages));
    }

    /**
     * error msg or no more message
     * @param errorMsg
     */
    protected void showLoadMsgToast(String errorMsg) {
        showMsgToast(TextUtils.isEmpty(errorMsg) ? getResources().getString(R.string.no_more_messages) : errorMsg);
    }

    protected IChatAdapterProvider setChatAdapterProvider() {
        return null;
    }

    /**
     * set viewHolder provider
     */
    public IViewHolderProvider setViewHolderProvider() {
        return null;
    }

    public void setOnMessageChangeListener(OnMessageChangeListener listener) {
        this.messageChangeListener = listener;
    }

    /**
     * 用于监听消息的变化，发送消息及接收消息
     */
    public interface OnMessageChangeListener {
        void onMessageChange(String change);
    }

    public void setIChatTitleProvider(IChatTitleProvider titleProvider) {
        this.titleProvider = titleProvider;
    }

    /**
     * 聊天标题
     */
    public interface IChatTitleProvider {
        /**
         * 标题
         * @param chatType
         * @param title
         */
        void provideTitle(int chatType, String title);
    }


//============================== view control end ===========================

//============================ load and show messages start ==================================

    private void checkIfSeekToLatest() {
        List<EMMessage> messages = conversation.getAllMessages();
        if(currentMessages == null || messages.size() > currentMessages.size()) {
            SeekToPosition(messages.size() - 1);
        }
        currentMessages = messages;
    }

    /**
     * seek to latest position
     */
    public void refreshToLatest() {
        List<EMMessage> messages = conversation.getAllMessages();
        boolean refresh = currentMessages != null && messages.size() > currentMessages.size();
        refreshMessages();
        if(refresh) {
            SeekToPosition(messages.size() - 1);
        }
    }

    /**
     * fresh messages
     */
    public void refreshMessages() {
        if(isActivityDisable()) {
            return;
        }
        mContext.runOnUiThread(() -> {
            List<EMMessage> messages = conversation.getAllMessages();
            conversation.markAllMessagesAsRead();
            if(messageAdapter != null) {
                messageAdapter.setData(messages);
            }
            currentMessages = messages;
            finishRefresh();
        });

    }

    /**
     * load more messages
     */
    protected void loadMoreMessages(int pageSize, boolean loadFromServer) {
        if(loadFromServer) {
            loadMoreServerMessages(pageSize);
            return;
        }
        loadMoreLocalMessages(pageSize);
    }

    private void loadMoreServerMessages(int pageSize) {
        int count = getCacheMessageCount();
        EMClient.getInstance().chatManager().asyncFetchHistoryMessage(toChatUsername,
                EaseCommonUtils.getConversationType(chatType), pageSize, count > 0 ? conversation.getAllMessages().get(0).getMsgId() : null,
                new EMValueCallBack<EMCursorResult<EMMessage>>() {
                    @Override
                    public void onSuccess(EMCursorResult<EMMessage> value) {
                        if(isActivityDisable()) {
                            return;
                        }
                        mContext.runOnUiThread(() -> loadMoreLocalMessages(pageSize));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        if(isActivityDisable()) {
                            return;
                        }
                        mContext.runOnUiThread(()-> {
                            showLoadMsgToast(errorMsg);
                            loadMoreLocalMessages(pageSize);
                        });
                    }
                });
    }

    private void loadMoreLocalMessages(int pageSize) {
        List<EMMessage> messageList = conversation.getAllMessages();
        int msgCount = messageList != null ? messageList.size() : 0;
        int allMsgCount = conversation.getAllMsgCount();
        if(msgCount < allMsgCount) {
            String msgId = null;
            if(msgCount > 0) {
                msgId = messageList.get(0).getMsgId();
            }
            List<EMMessage> moreMsgs = null;
            String errorMsg = null;
            try {
                moreMsgs = conversation.loadMoreMsgFromDB(msgId, pageSize);
            } catch (Exception e) {
                errorMsg = e.getMessage();
                e.printStackTrace();
            }
            // 刷新数据，一则刷新数据，二则需要消息进行定位
            if(moreMsgs == null || moreMsgs.isEmpty()) {
                showLoadMsgToast(errorMsg);
                return;
            }
            refreshMessages();
            // 对消息进行定位
            SeekToPosition(moreMsgs.size() - 1);
        }else {
            finishRefresh();
            showNoMoreMsgToast();
        }
    }

    /**
     * 移动到指定位置
     * @param position
     */
    private void SeekToPosition(int position) {
        if(isInitMsg) {
            position = conversation.getAllMessages().size() - 1;
            isInitMsg = false;
        }
        if(position < 0) {
            position = 0;
        }
        RecyclerView.LayoutManager manager = messageList.getLayoutManager();
        if(manager instanceof LinearLayoutManager) {
            if(isActivityDisable()) {
                return;
            }
            int finalPosition = position;
            mContext.runOnUiThread(()-> {
                ((LinearLayoutManager)manager).scrollToPositionWithOffset(finalPosition, 0);
            });
        }
    }

    /**
     * 获取内存中消息数目
     * @return
     */
    protected int getCacheMessageCount() {
        if(conversation == null) {
            return 0;
        }
        List<EMMessage> messageList = conversation.getAllMessages();
        return messageList != null ? messageList.size() : 0;
    }

    /**
     * 获取数据库中消息总数目
     * @return
     */
    protected int getAllMsgCountFromDb() {
        return conversation == null ? 0 : conversation.getAllMsgCount();
    }

    /**
     * 从本地数据库拉取数据
     */
    private void loadMessagesFromLocal() {
        int msgCount = getCacheMessageCount();
        if(msgCount < getAllMsgCountFromDb() && msgCount < PAGE_SIZE) {
            loadMoreMessages(PAGE_SIZE - msgCount, false);
        }else {
            SeekToPosition(msgCount - 1);
        }
    }

//============================ load and show messages start ==================================

//======================= choose resources start ============================

    /**
     * select local video
     */
    protected void selectVideoFromLocal() {

    }

    /**
     * select local file
     */
    protected void selectFileFromLocal() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    /**
     * select picture from camera
     */
    protected void selectPicFromCamera() {
        if(!checkSdCardExist()) {
            return;
        }
        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(getContext(), cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * 检查sd卡是否挂载
     * @return
     */
    protected boolean checkSdCardExist() {
        return EaseCommonUtils.isSdcardExist();
    }

//====================================== choose resources end =================================

//==================================== send message start ======================================
    /**
     * send image
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    /**
     * send file
     * @param uri
     */
    protected void sendFileByUri(Uri uri){
        String filePath = EaseCompat.getPath(getActivity(), uri);
        EMLog.i(TAG, "sendFileByUri: " + filePath);
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(getActivity(), R.string.File_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        sendFileMessage(filePath);
    }

    /**
     * 发送文本消息
     * @param content
     */
    protected void sendTextMessage(String content) {
        if(EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content);
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        sendMessage(message);
    }

    /**
     * send big expression message
     * @param name
     * @param identityCode
     */
    protected void sendBigExpressionMessage(String name, String identityCode){
        EMMessage message = EaseCommonUtils.createExpressionMessage(toChatUsername, name, identityCode);
        sendMessage(message);
    }

    /**
     * send voice message
     * @param filePath
     * @param length
     */
    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        sendMessage(message);
    }

    /**
     * send image message
     * @param imagePath
     */
    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
        sendMessage(message);
    }

    /**
     * send location message
     * @param latitude
     * @param longitude
     * @param locationAddress
     */
    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUsername);
        sendMessage(message);
    }

    /**
     * send video message
     * @param videoPath
     * @param thumbPath
     * @param videoLength
     */
    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUsername);
        sendMessage(message);
    }

    /**
     * send file message
     * @param filePath
     */
    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUsername);
        sendMessage(message);
    }

    /**
     * send message
     * @param message
     */
    protected void sendMessage(EMMessage message) {
        addMessageAttributes(message);
        if (chatType == EaseConstant.CHATTYPE_GROUP){
            message.setChatType(EMMessage.ChatType.GroupChat);
        }else if(chatType == EaseConstant.CHATTYPE_CHATROOM){
            message.setChatType(EMMessage.ChatType.ChatRoom);
        }
        message.setMessageStatusCallback(this);
        // send message
        EMClient.getInstance().chatManager().sendMessage(message);
        // refresh messages
        refreshMessages();
        SeekToPosition(conversation.getAllMessages().size() - 1);
    }

    /**
     * forward message
     */
    protected void sendForwardMsg() {
        if(TextUtils.isEmpty(forwardMsgId)) {
            return;
        }
        final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(forwardMsgId);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
            case TXT:
                if(forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
                    sendBigExpressionMessage(((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
                }else{
                    // get the content and send it
                    String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                    sendTextMessage(content);
                }
                break;
            case IMAGE:
                // send image
                String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // send thumb nail if original image does not exist
                        filePath = ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
                    }
                    sendImageMessage(filePath);
                }
                break;
            default:
                break;
        }

        if(forward_msg.getChatType() == EMMessage.ChatType.ChatRoom){
            EMClient.getInstance().chatroomManager().leaveChatRoom(forward_msg.getTo());
        }
    }

    /**
     * add message extension
     * 添加扩展消息
     * @param message
     */
    protected void addMessageAttributes(EMMessage message) {
        // set message extension, for example
        // message.setAttribute("em_robot_message", isRobot);
    }

//============================== send message end ==============================================

//============================== fragment life cycle start =====================================

    @Override
    public void onResume() {
        super.onResume();
        refreshToLatest();
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(this);
        if(isGroupChat()) {
            EaseAtMessageHelper.get().removeAtMeGroup(toChatUsername);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(this);
        if(typingHandler != null) {
            typingHandler.sendEmptyMessage(MSG_TYPING_END);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists())
                    sendImageMessage(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_MAP) { // location
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    sendLocationMessage(latitude, longitude, locationAddress);
                } else {
                    showMsgToast(getResources().getString(R.string.unable_to_get_loaction));
                }

            } else if (requestCode == REQUEST_CODE_DING_MSG) { // To send the ding-type msg.
                String msgContent = data.getStringExtra("msg");
                EMLog.i(TAG, "To send the ding-type msg, content: " + msgContent);
                // Send the ding-type msg.
                EMMessage dingMsg = EaseDingMessageHelper.get().createDingMessage(toChatUsername, msgContent);
                sendMessage(dingMsg);
            }else if(requestCode == REQUEST_CODE_SELECT_VIDEO) {
                if (data != null) {
                    int duration = data.getIntExtra("dur", 0);
                    String videoPath = data.getStringExtra("path");
                    File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                        ThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                        sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else if(requestCode == REQUEST_CODE_SELECT_FILE) {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        sendFileByUri(uri);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(groupListener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(groupListener);
        }
        if(chatRoomListener != null) {
            EMClient.getInstance().chatroomManager().removeChatRoomListener(chatRoomListener);
        }
        if(isChatRoomChat()) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
        }
    }

//================================ fragment life cycle end ============================================

//================================= for single start ================================

    /**
     * 判断是否是single chat
     * @return
     */
    protected boolean isSingleChat() {
        return chatType == EaseConstant.CHATTYPE_SINGLE;
    }

    /**
     * 用于控制，是否告诉对方，你正在输入中
     * @return
     */
    protected boolean openTurnOnTyping() {
        return false;
    }

    /**
     * 不展示nickname
     */
    protected void hideNickname() {
        if(isSingleChat()) {
            messageAdapter.showUserNick(false);
        }
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


//================================= for single end ================================

//================================== for group start ================================

    /**
     * only for group chat
     * @param content
     */
    protected void sendAtMessage(String content) {
        if(!isGroupChat()){
            EMLog.e(TAG, "only support group chat message");
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
        if(EMClient.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get().containsAtAll(content)){
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
        }else {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
                    EaseAtMessageHelper.get().atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content)));
        }
        sendMessage(message);
    }

    /**
     * user war been kicked from group
     * @param groupId
     * @param groupName
     */
    protected void onUserRemoved(String groupId, String groupName) {
        if(isActivityDisable()) {
            return;
        }
        mContext.runOnUiThread(()-> mContext.finish());
    }

    /**
     * group was been destroyed
     * @param groupId
     * @param groupName
     */
    protected void onGroupDestroyed(String groupId, String groupName) {
        if(isActivityDisable()) {
            return;
        }
        mContext.runOnUiThread(()-> mContext.finish());
    }

    protected void addGroupListener() {
        if(!isGroupChat()) {
            return;
        }
        groupListener = new GroupListener();
        EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);
    }

    /**
     * 判断是否是群组聊天
     * @return
     */
    protected boolean isGroupChat() {
        return chatType == EaseConstant.CHATTYPE_GROUP;
    }

    /**
     * input @
     * only for group chat
     * @param username
     */
    protected void inputAtUsername(String username, boolean autoAddAtSymbol){
        if(EMClient.getInstance().getCurrentUser().equals(username) ||
                !isGroupChat()){
            return;
        }
        EaseAtMessageHelper.get().addAtUser(username);
        EaseUser user = EaseUserUtils.getUserInfo(username);
        if (user != null){
            username = user.getNickname();
        }
        if(autoAddAtSymbol)
            inputMenu.insertText("@" + username + " ");
        else
            inputMenu.insertText(username + " ");
    }

    /**
     * group listener
     */
    protected class GroupListener extends EaseGroupListener {

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            EaseChatFragment.this.onUserRemoved(groupId, groupName);
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            EaseChatFragment.this.onGroupDestroyed(groupId, groupName);
        }
    }

//=============================== for group end =======================================

//================================ for chat room start =====================================

    /**
     * join chat room
     */
    private void onChatRoomViewCreation() {
        if(!isChatRoomChat()) {
            return;
        }
        EMClient.getInstance().chatroomManager().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {
            @Override
            public void onSuccess(EMChatRoom value) {
                if(isActivityDisable()) {
                    return;
                }
                if(!TextUtils.equals(toChatUsername, value.getId())) {
                    return;
                }
                mContext.runOnUiThread(()-> {
                    EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(toChatUsername);
                    String title = room != null ? room.getName() : toChatUsername;
                    setTitleBarText(title);
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.d(TAG, "join room failure : "+error);
                if(!isActivityDisable()) {
                    mContext.finish();
                }
            }
        });
    }

    protected void addChatRoomListener() {
        if(!isChatRoomChat()) {
            return;
        }
        chatRoomListener = new ChatRoomListener();
        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomListener);
    }

    /**
     * 判断是否是chat room
     * @return
     */
    protected boolean isChatRoomChat() {
        return chatType == EaseConstant.CHATTYPE_CHATROOM;
    }

    protected void onRemovedFromChatRoom(int reason, String roomId, String roomName, String participant) {
        if(isActivityDisable()) {
            return;
        }
        mContext.runOnUiThread(() -> {
            if(!TextUtils.equals(roomId, toChatUsername)) {
                return;
            }
            if(reason == EMAChatRoomManagerListener.BE_KICKED) {
                mContext.finish();
            }else {
                tvErrorMsg.setVisibility(View.VISIBLE);
            }
        });
    }

    protected void onMemberJoined(String roomId, String participant) {
        if(isActivityDisable()) {
            return;
        }
    }

    protected void onMemberExited(String roomId, String roomName, String participant) {
        if (isActivityDisable()) {
            return;
        }
    }

    protected void onChatRoomDestroyed(String roomId, String roomName) {
        if(isActivityDisable()) {
            return;
        }
        mContext.runOnUiThread(() -> mContext.finish());
    }

    private class ChatRoomListener extends EaseChatRoomListener {

        @Override
        public void onChatRoomDestroyed(String roomId, String roomName) {
            EaseChatFragment.this.onChatRoomDestroyed(roomId, roomName);
        }

        @Override
        public void onRemovedFromChatRoom(int reason, String roomId, String roomName, String participant) {
            EaseChatFragment.this.onRemovedFromChatRoom(reason, roomId,  roomName, participant);
        }

        @Override
        public void onMemberJoined(String roomId, String participant) {
            EaseChatFragment.this.onMemberJoined(roomId, participant);
        }

        @Override
        public void onMemberExited(String roomId, String roomName, String participant) {
            EaseChatFragment.this.onMemberExited(roomId, roomName, participant);
        }
    }

//================================ for chat room end =====================================

//================================== for video and voice start ====================================

    /**
     * start video call
     */
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected()) {
            showMsgToast(getResources().getString(R.string.not_connect_to_server));
        }else {
            startChatVideoCall();
            // videoCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }

    protected void startChatVideoCall() {
        startActivity(new Intent(getActivity(), VideoCallActivity.class).putExtra("username", toChatUsername)
                .putExtra("isComingCall", false));
    }

    /**
     * start voice call
     */
    protected void startVoiceCall() {
        if (!EMClient.getInstance().isConnected()) {
            showMsgToast(getResources().getString(R.string.not_connect_to_server));
        } else {
            startChatVoiceCall();
            // voiceCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }

    protected void startChatVoiceCall() {
        startActivity(new Intent(getActivity(), VoiceCallActivity.class).putExtra("username", toChatUsername)
                .putExtra("isComingCall", false));
    }


//================================== for video and voice end ====================================


}
