package com.hyphenate.easeui.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
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
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseBaseAdapter;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseDingMessageHelper;
import com.hyphenate.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;

import java.io.File;
import java.util.List;

public class EaseChatFragment extends EaseBaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, EaseChatInputMenu.ChatInputMenuListener, EaseChatExtendMenu.EaseChatExtendMenuItemClickListener, MessageListItemClickListener, EMCallBack, EMMessageListener {
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_DING_MSG = 4;
    private static final String TAG = EaseChatFragment.class.getSimpleName();

    protected TextView tvErrorMsg;
    protected SwipeRefreshLayout srlRefresh;
    protected RecyclerView messageList;
    protected EaseChatInputMenu inputMenu;
    protected boolean isRoaming;
    /**
     * 消息类别，自定义
     */
    protected int chatType = EaseConstant.CHATTYPE_SINGLE;
    /**
     * 消息类别，SDK定义
     */
    protected EMMessage.ChatType emMsgChatType = EMMessage.ChatType.Chat;
    protected String toChatUsername;
    protected Activity context;
    protected EaseBaseAdapter messageAdapter;
    protected File cameraFile;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = (Activity) context;
    }

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
            initChildArguments();
        }
    }

    private void initView() {
        tvErrorMsg = findViewById(R.id.tv_error_msg);
        srlRefresh = findViewById(R.id.srl_refresh);
        messageList = findViewById(R.id.message_list);
        inputMenu = findViewById(R.id.input_menu);

        initChildView();

        messageList.setLayoutManager(provideLayoutManager());
        messageList.setAdapter(provideMessageAdapter());

        initInputMenu();
        addExtendInputMenu();
    }

    private void initListener() {
        tvErrorMsg.setOnClickListener(this);
        srlRefresh.setOnRefreshListener(this);
        inputMenu.setChatInputMenuListener(this);
        setMessageClickListener();
        initChildListener();
    }

    private void setMessageClickListener() {
        if(messageAdapter != null && messageAdapter instanceof EaseMessageAdapter) {
            ((EaseMessageAdapter)messageAdapter).setListItemClickListener(this);
        }
    }

    private void initData() {
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
        refreshMessages();
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
        Log.e("TAG", "onBigExpressionClicked");
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
        return false;
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
        // 跳转到好友页面

    }

    /**
     * MessageListItemClickListener
     * @param username
     */
    @Override
    public void onUserAvatarLongClick(String username) {
        // 具体逻辑
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
        refreshMessages();
    }

    /**
     * message status callback
     */
    @Override
    public void onError(int code, String error) {
        refreshMessages();
    }

    /**
     * message status callback
     */
    @Override
    public void onProgress(int progress, String status) {
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

    /**
     * fresh messages
     */
    public void refreshMessages() {
        if(messageAdapter != null && messageAdapter instanceof EaseMessageAdapter) {
            ((EaseMessageAdapter)messageAdapter).setConversationMessages();
        }
        if(srlRefresh != null) {
            srlRefresh.setRefreshing(false);
        }
    }

    /**
     * developer can override the method to change default chat extend menu items
     */
    protected void initInputMenu() {
        inputMenu.registerDefaultMenuItems(this);
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
            cursor = null;

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
        // TODO: 2020/1/5 此处需要显示最后一条的
        refreshMessages();
    }

    /**
     * 通过id获取当前view控件，需要在onViewCreated()之后的生命周期调用
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findViewById(@IdRes int id) {
        return requireView().findViewById(id);
    }

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

//============================ easy for developer to call ================================
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
     * developer can add extend menu item by override the method
     */
    protected void addExtendInputMenu() {
        // inputMenu.registerExtendMenuItem(nameRes, drawableRes, itemId, listener);
    }

    /**
     * set titleBar title
     * @param title
     */
    protected void setTitleBarText(String title) {

    }

    /**
     * 发送文本消息
     * @param content
     */
    protected void sendTextMessage(String content) {
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
     * add message extension
     * 添加扩展消息
     * @param message
     */
    protected void addMessageAttributes(EMMessage message) {
        // set message extension, for example
        // message.setAttribute("em_robot_message", isRobot);
    }

}
