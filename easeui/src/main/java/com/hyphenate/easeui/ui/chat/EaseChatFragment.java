package com.hyphenate.easeui.ui.chat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.EaseGroupListener;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseDingMessageHelper;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;

import java.io.File;
import java.util.List;

public abstract class EaseChatFragment extends EaseBaseFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, EaseChatInputMenu.ChatInputMenuListener,
        EaseChatExtendMenu.EaseChatExtendMenuItemClickListener, MessageListItemClickListener,
        EMCallBack, EMMessageListener, View.OnTouchListener, TextWatcher {
    private static final String TAG = EaseChatFragment.class.getSimpleName();
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_DING_MSG = 4;

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
     * 消息类别，SDK定义
     */
    protected EMMessage.ChatType emMsgChatType = EMMessage.ChatType.Chat;
    protected String toChatUsername;
    protected EaseBaseRecyclerViewAdapter messageAdapter;
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
            toChatUsername = bundle.getString(EaseConstant.EXTRA_USER_ID);
            forwardMsgId = bundle.getString(EaseConstant.FORWARD_MSG_ID);
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
        messageList.setAdapter(provideMessageAdapter());

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
        initChildListener();
    }

    private void setMessageClickListener() {
        if(messageAdapter != null && messageAdapter instanceof EaseMessageAdapter) {
            ((EaseMessageAdapter)messageAdapter).setListItemClickListener(this);
        }
    }

    private void addGroupListener() {
        GroupListener listener = new GroupListener();
        EMClient.getInstance().groupManager().addGroupChangeListener(listener);
    }

    private void initData() {
        initConversation();
        sendForwardMsg();
        refreshMessages();
        initChildData();
    }

    @Override
    public void onClick(View v) {
        // do nothing
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
                startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
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
        Log.e("TAG", "onTyping");
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
        return false;
    }

    /**
     * MessageListItemClickListener
     * @param message
     */
    @Override
    public void onBubbleLongClick(EMMessage message) {

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

    }

    /**
     * message status callback
     */
    @Override
    public void onSuccess() {
        EMLog.i(TAG, "send message success");
        refreshMessages();
    }

    /**
     * message status callback
     */
    @Override
    public void onError(int code, String error) {
        EMLog.i(TAG, "send message error = "+error);
        refreshMessages();
    }

    /**
     * message status callback
     */
    @Override
    public void onProgress(int progress, String status) {
        EMLog.i(TAG, "send message on progress");
        refreshMessages();
    }

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
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
                refreshMessages();
            }
        }
    }

    /**
     * EMMessageListener
     * @param messages
     */
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        // 对方是否正在输入的消息回调
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
        refreshMessages();
    }

    /**
     * EMMessageListener
     * @param message
     * @param change
     */
    @Override
    public void onMessageChanged(EMMessage message, Object change) {
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

    /**
     * set titleBar title
     * @param title
     */
    protected void setTitleBarText(String title) {

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
    protected RecyclerView.Adapter provideMessageAdapter() {
        messageAdapter = new EaseMessageAdapter(toChatUsername, chatType);
        return messageAdapter;
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
//============================== view control end ===========================

//============================ load and show messages start ==================================
    /**
     * fresh messages
     */
    public void refreshMessages() {
        if(isActivityDisable()) {
            return;
        }
        mContext.runOnUiThread(() -> {
            if(messageAdapter != null && messageAdapter instanceof EaseMessageAdapter) {
                ((EaseMessageAdapter)messageAdapter).setConversationMessages();
            }
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
        RecyclerView.LayoutManager manager = messageList.getLayoutManager();
        if(manager instanceof LinearLayoutManager) {
            ((LinearLayoutManager)manager).scrollToPositionWithOffset(position, 0);
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
        message.setChatType(emMsgChatType);
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
     * only for group chat
     * @param content
     */
    protected void sendAtMessage(String content) {
        if(chatType != EaseConstant.CHATTYPE_GROUP){
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
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(this);
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
                    Toast.makeText(getActivity(), R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == REQUEST_CODE_DING_MSG) { // To send the ding-type msg.
                String msgContent = data.getStringExtra("msg");
                EMLog.i(TAG, "To send the ding-type msg, content: " + msgContent);
                // Send the ding-type msg.
                EMMessage dingMsg = EaseDingMessageHelper.get().createDingMessage(toChatUsername, msgContent);
                sendMessage(dingMsg);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

//================================ fragment life cycle end ============================================

//================================== for group start ================================

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

    /**
     * input @
     * only for group chat
     * @param username
     */
    protected void inputAtUsername(String username, boolean autoAddAtSymbol){
        if(EMClient.getInstance().getCurrentUser().equals(username) ||
                chatType != EaseConstant.CHATTYPE_GROUP){
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

}
