package com.hyphenate.easeim.section.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.chat.fragment.ChatFragment;
import com.hyphenate.easeim.section.chat.viewmodel.ChatViewModel;
import com.hyphenate.easeim.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.easeim.section.group.activity.ChatRoomDetailActivity;
import com.hyphenate.easeim.section.group.activity.GroupDetailActivity;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class ChatActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, EaseTitleBar.OnRightClickListener, ChatFragment.OnFragmentInfoListener {
    private EaseTitleBar titleBarMessage;
    private String conversationId;
    private int chatType;
    private ChatFragment fragment;
    private String historyMsgId;
    private ChatViewModel viewModel;

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
        fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        bundle.putString(DemoConstant.HISTORY_MSG_ID, historyMsgId);
        bundle.putBoolean(EaseConstant.EXTRA_IS_ROAM, DemoHelper.getInstance().getModel().isMsgRoaming());
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "chat").commit();

        setTitleBarRight();
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
        }
    }

    @Override
    protected void initData() {
        super.initData();
        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
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
            EMConversation conversation = EMClient.getInstance()
                                                    .chatManager()
                                                    .getConversation(conversationId, EaseCommonUtils.getConversationType(chatType), false);
            if(conversation == null) {
                finish();
            }
        });
    }

    private void showSnackBar(String event) {
        Snackbar.make(titleBarMessage, event, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
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
            titleBarMessage.setTitle(getString(com.hyphenate.easeui.R.string.alert_during_typing));
        }else if(TextUtils.equals(action, "TypingEnd")) {
            titleBarMessage.setTitle(conversationId);
        }
    }
}
