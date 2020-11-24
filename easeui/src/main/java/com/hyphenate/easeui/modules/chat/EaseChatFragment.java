package com.hyphenate.easeui.modules.chat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.manager.EaseDingMessageHelper;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.chat.interfaces.OnChatLayoutListener;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseCompat;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.hyphenate.util.VersionUtils;

import java.io.File;

public class EaseChatFragment extends EaseBaseFragment implements OnChatLayoutListener {
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_DING_MSG = 4;
    protected static final int REQUEST_CODE_SELECT_VIDEO = 11;
    protected static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final String TAG = EaseChatFragment.class.getSimpleName();
    public EaseChatLayout chatLayout;
    public String toChatUsername;
    public int chatType;
    private OnChatLayoutListener listener;

    protected File cameraFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initArguments();
        return LayoutInflater.from(container.getContext()).inflate(getLayoutId(), null);
    }

    private int getLayoutId() {
        return R.layout.ease_fragment_chat_list;
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

    public void initArguments() {
        toChatUsername = getArguments().getString(EaseConstant.EXTRA_USER_ID);
        chatType = getArguments().getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
    }

    public void initView() {
        chatLayout = findViewById(R.id.layout_chat);
        chatLayout.getChatMessageListLayout().setItemShowType(EaseChatMessageListLayout.ShowType.NORMAL);
        chatLayout.getChatMessageListLayout().setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
    }

    public void initListener() {
        chatLayout.setOnChatLayoutListener(this);
    }

    public void initData() {
        chatLayout.init(toChatUsername, chatType);
        chatLayout.loadDefaultData();
    }

    public void setOnChatLayoutListener(OnChatLayoutListener listener) {
        this.listener = listener;
    }

    @Override
    public void onMessageChange(EaseEvent change) {
        if(listener != null) {
            listener.onMessageChange(change);
        }
    }

    @Override
    public boolean onBubbleClick(EMMessage message) {
        if(listener != null) {
            return listener.onBubbleClick(message);
        }
        return false;
    }

    @Override
    public boolean onBubbleLongClick(View v, EMMessage message) {
        if(listener != null) {
            return listener.onBubbleLongClick(v, message);
        }
        return false;
    }

    @Override
    public void onUserAvatarClick(String username) {
        if(listener != null) {
            listener.onUserAvatarClick(username);
        }
    }

    @Override
    public void onUserAvatarLongClick(String username) {
        if(listener != null) {
            listener.onUserAvatarLongClick(username);
        }
    }

    @Override
    public void onChatExtendMenuItemClick(View view, int itemId) {
        switch (itemId) {
            case EaseChatExtendMenu.ITEM_TAKE_PICTURE :
                selectPicFromCamera();
                break;
            case EaseChatExtendMenu.ITEM_PICTURE :
                selectPicFromLocal();
                break;
            case EaseChatExtendMenu.ITEM_LOCATION :
                startMapLocation(REQUEST_CODE_MAP);
                break;
            case EaseChatExtendMenu.ITEM_VIDEO :
                selectVideoFromLocal();
                break;
            case EaseChatExtendMenu.ITEM_FILE :
                selectFileFromLocal();
                break;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void onChatError(int code, String errorMsg) {
        if(listener != null) {
            listener.onChatError(code, errorMsg);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            chatLayout.getChatInputMenu().hideExtendContainer();
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                onActivityResultForCamera(data);
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                onActivityResultForLocalPhotos(data);
            } else if (requestCode == REQUEST_CODE_MAP) { // location
                onActivityResultForMapLocation(data);
            } else if (requestCode == REQUEST_CODE_DING_MSG) { // To send the ding-type msg.
                onActivityResultForDingMsg(data);
            }else if(requestCode == REQUEST_CODE_SELECT_FILE) {
                onActivityResultForLocalFiles(data);
            }
        }
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
     * select local image
     */
    protected void selectPicFromLocal() {
        EaseCompat.openImage(this, REQUEST_CODE_LOCAL);
    }

    /**
     * 启动定位
     * @param requestCode
     */
    protected void startMapLocation(int requestCode) {
        EaseBaiduMapActivity.actionStartForResult(this, requestCode);
    }

    /**
     * select local video
     */
    protected void selectVideoFromLocal() {

    }

    /**
     * select local file
     */
    protected void selectFileFromLocal() {
        Intent intent = new Intent();
        if(VersionUtils.isTargetQ(getActivity())) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * 相机返回处理结果
     * @param data
     */
    protected void onActivityResultForCamera(Intent data) {
        if (cameraFile != null && cameraFile.exists()) {
            chatLayout.sendImageMessage(Uri.parse(cameraFile.getAbsolutePath()));
        }
    }

    /**
     * 选择本地图片处理结果
     * @param data
     */
    protected void onActivityResultForLocalPhotos(@Nullable Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                chatLayout.sendImageMessage(selectedImage);
            }
        }
    }

    /**
     * 地图定位结果处理
     * @param data
     */
    protected void onActivityResultForMapLocation(@Nullable Intent data) {
        if(data != null) {
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            String locationAddress = data.getStringExtra("address");
            if (locationAddress != null && !locationAddress.equals("")) {
                chatLayout.sendLocationMessage(latitude, longitude, locationAddress);
            } else {
                if(listener != null) {
                    listener.onChatError(-1, getResources().getString(R.string.unable_to_get_loaction));
                }
            }
        }
    }

    protected void onActivityResultForDingMsg(@Nullable Intent data) {
        if(data != null) {
            String msgContent = data.getStringExtra("msg");
            EMLog.i(TAG, "To send the ding-type msg, content: " + msgContent);
            // Send the ding-type msg.
            EMMessage dingMsg = EaseDingMessageHelper.get().createDingMessage(toChatUsername, msgContent);
            chatLayout.sendMessage(dingMsg);
        }
    }

    /**
     * 本地文件选择结果处理
     * @param data
     */
    protected void onActivityResultForLocalFiles(@Nullable Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                chatLayout.sendFileMessage(uri);
            }
        }
    }

    /**
     * 检查sd卡是否挂载
     * @return
     */
    protected boolean checkSdCardExist() {
        return EaseCommonUtils.isSdcardExist();
    }
}

