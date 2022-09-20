package com.hyphenate.chatdemo.section.chat.fragment;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.chatdemo.DemoApplication;
import com.hyphenate.chatdemo.DemoHelper;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.constant.DemoConstant;
import com.hyphenate.chatdemo.common.livedatas.LiveDataBus;
import com.hyphenate.chatdemo.common.model.EmojiconExampleGroupData;
import com.hyphenate.chatdemo.section.av.VideoCallActivity;
import com.hyphenate.chatdemo.section.base.BaseActivity;
import com.hyphenate.chatdemo.section.chat.activity.ForwardMessageActivity;
import com.hyphenate.chatdemo.section.chat.activity.PickAtUserActivity;
import com.hyphenate.chatdemo.section.chat.activity.SelectUserCardActivity;
import com.hyphenate.chatdemo.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.chatdemo.section.conference.ConferenceInviteActivity;
import com.hyphenate.chatdemo.section.contact.activity.ContactDetailActivity;
import com.hyphenate.chatdemo.section.dialog.DemoDialogFragment;
import com.hyphenate.chatdemo.section.dialog.DemoListDialogFragment;
import com.hyphenate.chatdemo.section.dialog.FullEditDialogFragment;
import com.hyphenate.chatdemo.section.dialog.LabelEditDialogFragment;
import com.hyphenate.chatdemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.chatdemo.section.group.GroupHelper;
import com.hyphenate.chatdemo.section.me.activity.UserDetailActivity;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.chat.EaseChatFragment;
import com.hyphenate.easeui.modules.chat.EaseChatMessageListLayout;
import com.hyphenate.easeui.modules.chat.interfaces.IChatExtendMenu;
import com.hyphenate.easeui.modules.chat.interfaces.OnRecallMessageResultListener;
import com.hyphenate.easeui.modules.menu.EasePopupWindowHelper;
import com.hyphenate.easeui.modules.menu.MenuItemBean;
import com.hyphenate.util.EMLog;

import java.util.HashMap;
import java.util.Map;


public class ChatFragment extends EaseChatFragment implements OnRecallMessageResultListener {
    private static final String TAG = ChatFragment.class.getSimpleName();
    private static final int REQUEST_CODE_SELECT_USER_CARD = 20;
    private MessageViewModel viewModel;
    protected ClipboardManager clipboard;

    private static final int REQUEST_CODE_SELECT_AT_USER = 15;
    private static final String[] calls = {DemoApplication.getInstance().getApplicationContext().getString(R.string.video_call), DemoApplication.getInstance().getApplicationContext().getString(R.string.voice_call)};
    private static final String[] labels = {
            DemoApplication.getInstance().getApplicationContext().getString(R.string.tab_politics),
            DemoApplication.getInstance().getApplicationContext().getString(R.string.tab_yellow_related),
            DemoApplication.getInstance().getApplicationContext().getString(R.string.tab_advertisement),
            DemoApplication.getInstance().getApplicationContext().getString(R.string.tab_abuse),
            DemoApplication.getInstance().getApplicationContext().getString(R.string.tab_violent),
            DemoApplication.getInstance().getApplicationContext().getString(R.string.tab_contraband),
            DemoApplication.getInstance().getApplicationContext().getString(R.string.tab_other),
    };
    private OnFragmentInfoListener infoListener;
    private Dialog dialog;

    @Override
    public void initView() {
        super.initView();
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        EaseChatMessageListLayout messageListLayout = chatLayout.getChatMessageListLayout();
        //设置聊天列表背景
//      messageListLayout.setBackground(new ColorDrawable(Color.parseColor("#DA5A4D")));
        messageListLayout.setBackgroundResource(R.drawable.demo_caht_bitmap_bg);
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

        chatLayout.setTargetLanguageCode(DemoHelper.getInstance().getModel().getTargetLanguage());
    }

    private void addItemMenuAction() {
        MenuItemBean itemMenu = new MenuItemBean(0, R.id.action_chat_forward, 11, getString(R.string.action_forward));
        itemMenu.setResourceId(R.drawable.ease_chat_item_menu_forward);
        chatLayout.addItemMenu(itemMenu );
        MenuItemBean itemMenu1 = new MenuItemBean(0,R.id.action_chat_label,12,getString(R.string.action_report_label));
        itemMenu1.setResourceId(R.drawable.d_exclamationmark_in_triangle);
        chatLayout.addItemMenu(itemMenu1 );
//        chatLayout.setReportYourSelf(false);
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
        //名片扩展
        chatExtendMenu.registerMenuItem(R.string.attach_user_card, R.drawable.em_chat_user_card_selector, R.id.extend_item_user_card);
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

        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if(event == null) {
                return;
            }
            if(event.isMessageChange()) {
                chatLayout.getChatMessageListLayout().refreshToLatest();
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

        //更新用户属性刷新列表
        LiveDataBus.get().with(DemoConstant.CONTACT_ADD, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if(event == null) {
                return;
            }
            if(event != null) {
                chatLayout.getChatMessageListLayout().refreshMessages();
            }
        });

        LiveDataBus.get().with(DemoConstant.CONTACT_UPDATE, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if(event == null) {
                return;
            }
            if(event != null) {
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
                                EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VIDEO_CALL,conversationId,null, VideoCallActivity.class);
                                break;
                            case 1 :
                                EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VOICE_CALL,conversationId,null, VideoCallActivity.class);
                                break;
                        }
                    }
                })
                .show();
    }

    @Override
    public void onUserAvatarClick(String username) {
        if(!TextUtils.equals(username, DemoHelper.getInstance().getCurrentUser())) {
            EaseUser user = DemoHelper.getInstance().getUserInfo(username);
            if(user == null){
                    user = new EaseUser(username);
                }
                boolean isFriend =  DemoHelper.getInstance().getModel().isContact(username);
                if(isFriend){
                    user.setContact(0);
                }else{
                    user.setContact(3);
                }
                ContactDetailActivity.actionStart(mContext, user);
        }else{
            UserDetailActivity.actionStart(mContext,null,null);
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
    public boolean onBubbleClick(EMMessage message) {
        return false;
    }

    @Override
    public void onChatExtendMenuItemClick(View view, int itemId) {
        super.onChatExtendMenuItemClick(view, itemId);
        switch (itemId) {
            case R.id.extend_item_video_call:
                showSelectDialog();
                break;
            case R.id.extend_item_conference_call:
                Intent intent = new Intent(getContext(), ConferenceInviteActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(DemoConstant.EXTRA_CONFERENCE_GROUP_ID, conversationId);
                 getContext().startActivity(intent);
                 break;
            case R.id.extend_item_delivery://群消息回执
                showDeliveryDialog();
                break;
            case R.id.extend_item_user_card:
                EMLog.d(TAG,"select user card");
                Intent userCardIntent = new Intent(this.getContext(), SelectUserCardActivity.class);
                userCardIntent.putExtra("toUser",conversationId);
                startActivityForResult(userCardIntent, REQUEST_CODE_SELECT_USER_CARD);
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
                case REQUEST_CODE_SELECT_USER_CARD:
                    if(data != null) {
                        EaseUser user = (EaseUser) data.getSerializableExtra("user");
                        if(user != null) {
                            sendUserCardMessage(user);
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Send user card message
     * @param user
     */
    private void sendUserCardMessage(EaseUser user) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(DemoConstant.USER_CARD_EVENT);
        Map<String,String> params = new HashMap<>();
        params.put(DemoConstant.USER_CARD_ID,user.getUsername());
        params.put(DemoConstant.USER_CARD_NICK,user.getNickname());
        params.put(DemoConstant.USER_CARD_AVATAR,user.getAvatar());
        body.setParams(params);
        message.setBody(body);
        message.setTo(conversationId);
        chatLayout.sendMessage(message);
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
    public void onPreMenu(EasePopupWindowHelper helper, EMMessage message, View v) {
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
                if(v.getId() == R.id.subBubble){
                    helper.findItemVisible(R.id.action_chat_forward, false);
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
            case R.id.action_chat_reTranslate:
                new AlertDialog.Builder(getContext())
                        .setTitle(mContext.getString(R.string.using_translate))
                        .setMessage(mContext.getString(R.string.retranslate_prompt))
                        .setPositiveButton(mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                chatLayout.translateMessage(message, false);
                            }
                        }).show();
                return true;
            case R.id.action_chat_label:
                showLabelDialog(message);
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

    private void showLabelDialog(EMMessage message){
        new DemoListDialogFragment.Builder((BaseActivity) mContext)
                .setData(labels)
                .setCancelColorRes(R.color.black)
                .setWindowAnimations(R.style.animate_dialog)
                .setOnItemClickListener(new DemoListDialogFragment.OnDialogItemClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        showLabelDialog(message,labels[position]);
                    }
                })
                .show();
    }

    private void showLabelDialog(EMMessage message, String label){
        new LabelEditDialogFragment.Builder((BaseActivity) mContext)
            .setOnConfirmClickListener(new LabelEditDialogFragment.OnConfirmClickListener() {
                @Override
                public void onConfirm(View view, String reason) {
                EMLog.e("ReportMessage：", "msgId: "+message.getMsgId() + "label: " + label +  " reason: " + reason);
                new SimpleDialogFragment.Builder((BaseActivity) mContext)
                        .setTitle(getString(R.string.report_delete_title))
                        .setConfirmColor(R.color.em_color_brand)
                        .setOnConfirmClickListener(getString(R.string.confirm), new DemoDialogFragment.OnConfirmClickListener() {
                            @Override
                            public void onConfirmClick(View view) {
                                EMClient.getInstance().chatManager().asyncReportMessage(message.getMsgId(), label, reason, new EMCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        EMLog.e("ReportMessage：","onSuccess 举报成功");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(),"举报成功",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int code, String error) {
                                        EMLog.e("ReportMessage：","onError 举报失败: code " + code + "  : " + error);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(),"举报失败： code: " + code + " desc: " + error,Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onProgress(int progress, String status) {

                                    }
                                });
                            }
                        })
                        .showCancelButton(true)
                        .show();
                }
            }).show();
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

    @Override
    public void translateMessageFail(EMMessage message, int code, String error) {
        new AlertDialog.Builder(getContext())
                .setTitle(mContext.getString(R.string.unable_translate))
                .setMessage(error+".")
                .setPositiveButton(mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}