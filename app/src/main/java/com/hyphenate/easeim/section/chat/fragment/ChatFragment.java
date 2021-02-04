package com.hyphenate.easeim.section.chat.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.model.EmojiconExampleGroupData;
import com.hyphenate.easeim.common.utils.ToastUtils;
import com.hyphenate.easeim.section.base.BaseActivity;
import com.hyphenate.easeim.section.chat.activity.ChatVideoCallActivity;
import com.hyphenate.easeim.section.chat.activity.ChatVoiceCallActivity;
import com.hyphenate.easeim.section.chat.activity.ForwardMessageActivity;
import com.hyphenate.easeim.section.conference.ConferenceActivity;
import com.hyphenate.easeim.section.chat.activity.ImageGridActivity;
import com.hyphenate.easeim.section.chat.activity.LiveActivity;
import com.hyphenate.easeim.section.chat.activity.PickAtUserActivity;
import com.hyphenate.easeim.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.easeim.section.dialog.DemoDialogFragment;
import com.hyphenate.easeim.section.dialog.DemoListDialogFragment;
import com.hyphenate.easeim.section.dialog.FullEditDialogFragment;
import com.hyphenate.easeim.section.contact.activity.ContactDetailActivity;
import com.hyphenate.easeim.section.dialog.SimpleDialogFragment;
import com.hyphenate.easeim.section.group.GroupHelper;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.delegate.EaseExpressionAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseFileAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseTextAdapterDelegate;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.modules.chat.EaseChatExtendMenu;
import com.hyphenate.easeui.modules.chat.EaseChatFragment;
import com.hyphenate.easeui.modules.chat.EaseChatInputMenu;
import com.hyphenate.easeui.modules.chat.EaseChatMessageListLayout;
import com.hyphenate.easeui.modules.chat.EaseInputMenuStyle;
import com.hyphenate.easeui.modules.chat.interfaces.IChatExtendMenu;
import com.hyphenate.easeui.modules.chat.interfaces.IChatPrimaryMenu;
import com.hyphenate.easeui.modules.chat.interfaces.OnMenuChangeListener;
import com.hyphenate.easeui.modules.chat.interfaces.OnRecallMessageResultListener;
import com.hyphenate.easeui.modules.menu.EasePopupWindowHelper;
import com.hyphenate.easeui.modules.menu.MenuItemBean;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.UriUtils;


public class ChatFragment extends EaseChatFragment implements OnRecallMessageResultListener {
    private static final String TAG = ChatFragment.class.getSimpleName();
    private MessageViewModel viewModel;
    protected ClipboardManager clipboard;

    private static final int REQUEST_CODE_SELECT_AT_USER = 15;
    private static final String[] calls = {"视频通话", "语音通话"};
    private OnFragmentInfoListener infoListener;
    private Dialog dialog;

    @Override
    public void initView() {
        super.initView();
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        //获取到聊天列表控件
        //EaseChatMessageListLayout messageListLayout = chatLayout.getChatMessageListLayout();
        //设置聊天列表背景
        //messageListLayout.setBackground(new ColorDrawable(Color.parseColor("#DA5A4D")));
        //设置默认头像
        //messageListLayout.setAvatarDefaultSrc(ContextCompat.getDrawable(mContext, R.drawable.ease_default_avatar));
        //设置头像形状
        //messageListLayout.setAvatarShapeType(1);
        //设置文本字体大小
        //messageListLayout.setItemTextSize((int) EaseCommonUtils.sp2px(mContext, 18));
        //设置文本字体颜色
        //messageListLayout.setItemTextColor(ContextCompat.getColor(mContext, R.color.red));
        //设置时间线的背景
        //messageListLayout.setTimeBackground(ContextCompat.getDrawable(mContext, R.color.gray_normal));
        //设置时间线的文本大小
        //messageListLayout.setTimeTextSize((int) EaseCommonUtils.sp2px(mContext, 18));
        //设置时间线的文本颜色
        //messageListLayout.setTimeTextColor(ContextCompat.getColor(mContext, R.color.black));
        //设置聊天列表样式：两侧及均位于左侧
        //messageListLayout.setItemShowType(EaseChatMessageListLayout.ShowType.LEFT);

        //获取到菜单输入父控件
        //EaseChatInputMenu chatInputMenu = chatLayout.getChatInputMenu();
        //获取到菜单输入控件
        //IChatPrimaryMenu primaryMenu = chatInputMenu.getPrimaryMenu();
        //if(primaryMenu != null) {
            //设置菜单样式为不可用语音模式
        //    primaryMenu.setMenuShowType(EaseInputMenuStyle.ONLY_TEXT);
        //}

    }

    private void addItemMenuAction() {
        MenuItemBean itemMenu = new MenuItemBean(0, R.id.action_chat_forward, 11, getString(R.string.action_forward));
        itemMenu.setResourceId(R.drawable.ease_chat_item_menu_forward);
        chatLayout.addItemMenu(itemMenu );
    }

    private void resetChatExtendMenu() {
        IChatExtendMenu chatExtendMenu = chatLayout.getChatInputMenu().getChatExtendMenu();
        chatExtendMenu.clear();
        chatExtendMenu.registerMenuItem(R.string.attach_picture, R.drawable.ease_chat_image_selector, R.id.extend_item_picture);
        chatExtendMenu.registerMenuItem(R.string.attach_take_pic, R.drawable.ease_chat_takepic_selector, R.id.extend_item_take_picture);
        chatExtendMenu.registerMenuItem(R.string.attach_video, R.drawable.em_chat_video_selector, R.id.extend_item_video);
        //添加扩展槽
        if(chatType == EaseConstant.CHATTYPE_SINGLE){
            //inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, EaseChatInputMenu.ITEM_VOICE_CALL, this);
            chatExtendMenu.registerMenuItem(R.string.attach_media_call, R.drawable.em_chat_video_call_selector, R.id.extend_item_video_call);
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP) { // 音视频会议
            chatExtendMenu.registerMenuItem(R.string.voice_and_video_conference, R.drawable.em_chat_video_call_selector, R.id.extend_item_conference_call);
            //目前普通模式也支持设置主播和观众人数，都建议使用普通模式
            //inputMenu.registerExtendMenuItem(R.string.title_live, R.drawable.em_chat_video_call_selector, EaseChatInputMenu.ITEM_LIVE, this);
        }
        chatExtendMenu.registerMenuItem(R.string.attach_location, R.drawable.ease_chat_location_selector, R.id.extend_item_location);
        chatExtendMenu.registerMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, R.id.extend_item_file);
        //群组类型，开启消息回执，且是owner
        if(chatType == EaseConstant.CHATTYPE_GROUP && EMClient.getInstance().getOptions().getRequireAck()) {
            EMGroup group = DemoHelper.getInstance().getGroupManager().getGroup(conversationId);
            if(GroupHelper.isOwner(group)) {
                chatExtendMenu.registerMenuItem(R.string.em_chat_group_delivery_ack, R.drawable.demo_chat_delivery_selector, R.id.extend_item_delivery);
            }
        }
        //添加扩展表情
        chatLayout.getChatInputMenu().getEmojiconMenu().addEmojiconGroup(EmojiconExampleGroupData.getData());
    }

    @Override
    public void initListener() {
        super.initListener();
        chatLayout.setOnRecallMessageResultListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        resetChatExtendMenu();
        addItemMenuAction();

        chatLayout.getChatInputMenu().getPrimaryMenu().getEditText().setText(getUnSendMsg());
        chatLayout.turnOnTypingMonitor(DemoHelper.getInstance().getModel().isShowMsgTyping());

        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
        LiveDataBus.get().with(DemoConstant.MESSAGE_CALL_SAVE, Boolean.class).observe(getViewLifecycleOwner(), event -> {
            if(event == null) {
                return;
            }
            if(event) {
                chatLayout.getChatMessageListLayout().refreshToLatest();
            }
        });

        LiveDataBus.get().with(DemoConstant.CONVERSATION_DELETE, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if(event == null) {
                return;
            }
            if(event.isMessageChange()) {
                chatLayout.getChatMessageListLayout().refreshMessages();
            }
        });
        LiveDataBus.get().with(DemoConstant.CONVERSATION_READ, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if(event == null) {
                return;
            }
            if(event.isMessageChange()) {
                chatLayout.getChatMessageListLayout().refreshMessages();
            }
        });
    }

    private void showDeliveryDialog() {
        new FullEditDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(R.string.em_chat_group_read_ack)
                .setOnConfirmClickListener(R.string.em_chat_group_read_ack_send, new FullEditDialogFragment.OnSaveClickListener() {
                    @Override
                    public void onSaveClick(View view, String content) {
                        chatLayout.sendTextMessage(content, true);
                    }
                })
                .setConfirmColor(R.color.em_color_brand)
                .setHint(R.string.em_chat_group_read_ack_hint)
                .show();
    }

    private void showSelectDialog() {
        new DemoListDialogFragment.Builder((BaseActivity) mContext)
                //.setTitle(R.string.em_single_call_type)
                .setData(calls)
                .setCancelColorRes(R.color.black)
                .setWindowAnimations(R.style.animate_dialog)
                .setOnItemClickListener(new DemoListDialogFragment.OnDialogItemClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        switch (position) {
                            case 0 :
                                startVideoCall();
                                break;
                            case 1 :
                                startVoiceCall();
                                break;
                        }
                    }
                })
                .show();
    }

    @Override
    public void onUserAvatarClick(String username) {
        if(!TextUtils.equals(username, DemoHelper.getInstance().getCurrentUser())) {
            EaseUser user = new EaseUser();
            user.setUsername(username);
            ContactDetailActivity.actionStart(mContext, user);
        }
    }

    @Override
    public void onUserAvatarLongClick(String username) {

    }

    @Override
    public boolean onBubbleLongClick(View v, EMMessage message) {
        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!chatLayout.getChatMessageListLayout().isGroupChat()) {
            return;
        }
        if(count == 1 && "@".equals(String.valueOf(s.charAt(start)))){
            PickAtUserActivity.actionStartForResult(ChatFragment.this, conversationId, REQUEST_CODE_SELECT_AT_USER);
        }
    }

    @Override
    protected void selectVideoFromLocal() {
        super.selectVideoFromLocal();
        Intent intent = new Intent(getActivity(), ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
    }

    @Override
    public boolean onBubbleClick(EMMessage message) {
        return false;
    }

    @Override
    public void onChatExtendMenuItemClick(View view, int itemId) {
        super.onChatExtendMenuItemClick(view, itemId);
        switch (itemId) {
            case R.id.extend_item_video_call:
                //startVideoCall();
                showSelectDialog();
                break;
//            case EaseChatInputMenu.ITEM_VOICE_CALL:
//                showSelectDialog();
//                break;
            case R.id.extend_item_conference_call:
                ConferenceActivity.startConferenceCall(getActivity(), conversationId);
                break;
            case R.id.extend_item_delivery://群消息回执
                showDeliveryDialog();
                break;
        }
    }

    @Override
    public void onChatError(int code, String errorMsg) {
        if(infoListener != null) {
            infoListener.onChatError(code, errorMsg);
        }
    }

    @Override
    public void onOtherTyping(String action) {
        if(infoListener != null) {
            infoListener.onOtherTyping(action);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_AT_USER :
                    if(data != null){
                        String username = data.getStringExtra("username");
                        chatLayout.inputAtUsername(username, false);
                    }
                    break;
                case REQUEST_CODE_SELECT_VIDEO: //send the video
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");
                        String uriString = data.getStringExtra("uri");
                        EMLog.d(TAG, "path = "+videoPath + " uriString = "+uriString);
                        if(!TextUtils.isEmpty(videoPath)) {
                            chatLayout.sendVideoMessage(Uri.parse(videoPath), duration);
                        }else {
                            Uri videoUri = UriUtils.getLocalUriFromString(uriString);
                            chatLayout.sendVideoMessage(videoUri, duration);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //保存未发送的文本消息内容
        if(mContext != null && mContext.isFinishing()) {
            if(chatLayout.getChatInputMenu() != null) {
                saveUnSendMsg(chatLayout.getInputContent());
                LiveDataBus.get().with(DemoConstant.MESSAGE_NOT_SEND).postValue(true);
            }
        }
    }

    //================================== for video and voice start ====================================

    /**
     * start video call
     */
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected()) {
            showMsgToast(getResources().getString(com.hyphenate.easeui.R.string.not_connect_to_server));
        }else {
            startChatVideoCall();
        }
    }

    private void showMsgToast(String string) {
        ToastUtils.showToast(string);
    }

    /**
     * start voice call
     */
    protected void startVoiceCall() {
        if (!EMClient.getInstance().isConnected()) {
            showMsgToast(getResources().getString(com.hyphenate.easeui.R.string.not_connect_to_server));
        } else {
            startChatVoiceCall();
        }
    }

    protected void startChatVideoCall() {
        ChatVideoCallActivity.actionStart(mContext, conversationId);
    }

    protected void startChatVoiceCall() {
        ChatVoiceCallActivity.actionStart(mContext, conversationId);
    }

    /**
     * 保存未发送的文本消息内容
     * @param content
     */
    private void saveUnSendMsg(String content) {
        DemoHelper.getInstance().getModel().saveUnSendMsg(conversationId, content);
    }

    private String getUnSendMsg() {
        return DemoHelper.getInstance().getModel().getUnSendMsg(conversationId);
    }

    @Override
    public void onPreMenu(EasePopupWindowHelper helper, EMMessage message) {
        //默认两分钟后，即不可撤回
        if(System.currentTimeMillis() - message.getMsgTime() > 2 * 60 * 1000) {
            helper.findItemVisible(R.id.action_chat_recall, false);
        }
        EMMessage.Type type = message.getType();
        helper.findItemVisible(R.id.action_chat_forward, false);
        switch (type) {
            case TXT:
                if(!message.getBooleanAttribute(DemoConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)
                        && !message.getBooleanAttribute(DemoConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    helper.findItemVisible(R.id.action_chat_forward, true);
                }
                break;
            case IMAGE:
                helper.findItemVisible(R.id.action_chat_forward, true);
                break;
        }

        if(chatType == DemoConstant.CHATTYPE_CHATROOM) {
            helper.findItemVisible(R.id.action_chat_forward, true);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItemBean item, EMMessage message) {
        switch (item.getItemId()) {
            case R.id.action_chat_forward :
                ForwardMessageActivity.actionStart(mContext, message.getMsgId());
                return true;
            case R.id.action_chat_delete:
                showDeleteDialog(message);
                return true;
            case R.id.action_chat_recall :
                showProgressBar();
                chatLayout.recallMessage(message);
                return true;
        }
        return false;
    }

    private void showProgressBar() {
        View view = View.inflate(mContext, R.layout.demo_layout_progress_recall, null);
        dialog = new Dialog(mContext,R.style.dialog_recall);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(view, layoutParams);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void showDeleteDialog(EMMessage message) {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(getString(R.string.em_chat_delete_title))
                .setConfirmColor(R.color.red)
                .setOnConfirmClickListener(getString(R.string.delete), new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        chatLayout.deleteMessage(message);
                    }
                })
                .showCancelButton(true)
                .show();
    }

    public void setOnFragmentInfoListener(OnFragmentInfoListener listener) {
        this.infoListener = listener;
    }

    @Override
    public void recallSuccess(EMMessage message) {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void recallFail(int code, String errorMsg) {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public interface OnFragmentInfoListener {
        void onChatError(int code, String errorMsg);

        void onOtherTyping(String action);
    }
}