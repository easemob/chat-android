package com.hyphenate.chatdemo.section.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatdemo.DemoHelper;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.constant.DemoConstant;
import com.hyphenate.chatdemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatdemo.section.base.BaseInitActivity;
import com.hyphenate.chatdemo.section.chat.fragment.ChatFragment;
import com.hyphenate.chatdemo.section.chat.viewmodel.ChatViewModel;
import com.hyphenate.chatdemo.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.chatdemo.section.group.GroupHelper;
import com.hyphenate.chatdemo.section.group.activity.ChatRoomDetailActivity;
import com.hyphenate.chatdemo.section.group.activity.GroupDetailActivity;
import com.hyphenate.chatdemo.section.group.viewmodels.GroupDetailViewModel;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class ChatActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, EaseTitleBar.OnRightClickListener, ChatFragment.OnFragmentInfoListener {
    private EaseTitleBar titleBarMessage;
    private String conversationId;
    private int chatType;
    private ChatFragment fragment;
    private String historyMsgId;
    private ChatViewModel viewModel;
    private GroupDetailViewModel groupDetailViewModel;
    private TextView tvTitle;
    private TextView subTitle;
    private EMConversation conversation;

    public static void actionStart(Context context, String conversationId, int chatType) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_chat;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        conversationId = intent.getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID);
        chatType = intent.getIntExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        historyMsgId = intent.getStringExtra(DemoConstant.HISTORY_MSG_ID);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBarMessage = findViewById(R.id.title_bar_message);
        tvTitle = findViewById(R.id.tv_title);
        subTitle = findViewById(R.id.sub_title);
        initChatFragment();
        setTitleBarRight();
    }

    private void initChatFragment() {
        fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        bundle.putString(DemoConstant.HISTORY_MSG_ID, historyMsgId);
        bundle.putBoolean(EaseConstant.EXTRA_IS_ROAM, DemoHelper.getInstance().getModel().isMsgRoaming());
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "chat").commit();
    }

    private void setTitleBarRight() {
        if(chatType == DemoConstant.CHATTYPE_SINGLE) {
            titleBarMessage.setRightImageResource(R.drawable.chat_user_info);
        }else {
            titleBarMessage.setRightImageResource(R.drawable.chat_group_info);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBarMessage.setOnBackPressListener(this);
        titleBarMessage.setOnRightClickListener(this);
        fragment.setOnFragmentInfoListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent != null) {
            initIntent(intent);
            initChatFragment();
            initData();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        conversation = EMClient.getInstance().chatManager().getConversation(conversationId);
        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        groupDetailViewModel = new ViewModelProvider(this).get(GroupDetailViewModel.class);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.getDeleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    finish();
                    EaseEvent event = EaseEvent.create(DemoConstant.CONVERSATION_DELETE, EaseEvent.TYPE.MESSAGE);
                    messageViewModel.setMessageChange(event);
                }
            });
        });
        viewModel.getChatRoomObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMChatRoom>() {
                @Override
                public void onSuccess(@Nullable EMChatRoom data) {
                    setDefaultTitle();
                }
            });
        });
        messageViewModel.getMessageChange().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isGroupLeave() && TextUtils.equals(conversationId, event.message)) {
                finish();
            }
        });
        messageViewModel.getMessageChange().with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isChatRoomLeave() && TextUtils.equals(conversationId,  event.message)) {
                finish();
            }
        });
        messageViewModel.getMessageChange().with(DemoConstant.MESSAGE_FORWARD, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isMessageChange()) {
                showSnackBar(event.event);
            }
        });
        messageViewModel.getMessageChange().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(conversation == null) {
                finish();
            }
        });
        groupDetailViewModel.getGroupObservable().observe(this, response-> {
            parseResource(response, new OnResourceParseCallback<EMGroup>() {
                @Override
                public void onSuccess(@Nullable EMGroup data) {
                    String extension = data.getExtension();
                    if(!TextUtils.equals("default", extension)) {
                        subTitle.setText(R.string.chat_temp_hint);
                        subTitle.setVisibility(View.VISIBLE);
                    }
                }
            });
        });

        setDefaultTitle();
        checkGroupInfo();
    }

    private void checkGroupInfo() {
        if(chatType == DemoConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(conversationId);
            if(group == null || TextUtils.isEmpty(group.getExtension())) {
                groupDetailViewModel.getGroup(conversationId);
            }else {
                if(!TextUtils.equals("default", group.getExtension())) {
                    subTitle.setText(R.string.chat_temp_hint);
                    subTitle.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showSnackBar(String event) {
        Snackbar.make(titleBarMessage, event, Snackbar.LENGTH_SHORT).show();
    }

    private void setDefaultTitle() {
        String title;
        if(chatType == DemoConstant.CHATTYPE_GROUP) {
            title = GroupHelper.getGroupName(conversationId);
        }else if(chatType == DemoConstant.CHATTYPE_CHATROOM) {
            EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(conversationId);
            if(room == null) {
                viewModel.getChatRoom(conversationId);
                return;
            }
            title =  TextUtils.isEmpty(room.getName()) ? conversationId : room.getName();
        }else {
            EaseUserProfileProvider userProvider = EaseIM.getInstance().getUserProvider();
            if(userProvider != null) {
                EaseUser user = userProvider.getUser(conversationId);
                if(user != null) {
                    title = user.getNickname();
                }else {
                    title = conversationId;
                }
            }else {
                title = conversationId;
            }
        }
        this.tvTitle.setText(title);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
        if (chatType == DemoConstant.CHATTYPE_GROUP){
            DemoHelper.getInstance().reLoadUserInfoFromDb();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            if (chatType == DemoConstant.CHATTYPE_GROUP){
                DemoHelper.getInstance().reLoadUserInfoFromDb();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRightClick(View view) {
        if(chatType == DemoConstant.CHATTYPE_SINGLE) {
            //跳转到单聊设置页面
            SingleChatSetActivity.actionStart(mContext, conversationId);
        }else {
            // 跳转到群组设置
            if(chatType == DemoConstant.CHATTYPE_GROUP) {
                GroupDetailActivity.actionStart(mContext, conversationId);
            }else if(chatType == DemoConstant.CHATTYPE_CHATROOM) {
                ChatRoomDetailActivity.actionStart(mContext, conversationId);
            }
        }
    }

    @Override
    public void onChatError(int code, String errorMsg) {
        showToast(errorMsg);
    }

    @Override
    public void onOtherTyping(String action) {
        if (TextUtils.equals(action, "TypingBegin")) {
            this.tvTitle.setText(getString(com.hyphenate.easeui.R.string.alert_during_typing));
        }else if(TextUtils.equals(action, "TypingEnd")) {
            setDefaultTitle();
        }
    }
}
