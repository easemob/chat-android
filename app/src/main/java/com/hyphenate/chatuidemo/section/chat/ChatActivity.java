package com.hyphenate.chatuidemo.section.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.db.DemoDbHelper;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.section.base.BaseDialogFragment;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.chat.fragment.ChatFragment;
import com.hyphenate.chatuidemo.section.chat.viewmodel.ChatViewModel;
import com.hyphenate.chatuidemo.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.chatuidemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.chatuidemo.section.group.ChatRoomDetailActivity;
import com.hyphenate.chatuidemo.section.group.GroupDetailActivity;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.chat.EaseChatFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.List;

public class ChatActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, EaseTitleBar.OnRightClickListener {
    private EaseTitleBar titleBarMessage;
    private String toChatUsername;
    private int chatType;
    private EaseChatFragment fragment;
    private String forwardMsgId;
    private ChatViewModel viewModel;

    public static void actionStart(Context context, String userId, int chatType) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_USER_ID, userId);
        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_chat;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        toChatUsername = intent.getStringExtra(EaseConstant.EXTRA_USER_ID);
        chatType = intent.getIntExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        forwardMsgId = intent.getStringExtra(DemoConstant.FORWARD_MSG_ID);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBarMessage = findViewById(R.id.title_bar_message);
        fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EaseConstant.EXTRA_USER_ID, toChatUsername);
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        bundle.putString(DemoConstant.FORWARD_MSG_ID, forwardMsgId);
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
        fragment.setIChatTitleProvider(new EaseChatFragment.IChatTitleProvider() {
            @Override
            public void provideTitle(int chatType, String title) {
                if(chatType == DemoConstant.CHATTYPE_SINGLE) {
                    LiveData<List<EaseUser>> titleObservable = DemoDbHelper.getInstance(mContext).getUserDao().loadUserById(title);
                    titleObservable.observe(mContext, users -> {
                        if(users != null && !users.isEmpty()) {
                            titleBarMessage.setTitle(users.get(0).getNickname());
                        }else {
                            titleBarMessage.setTitle(title);
                        }
                    });
                }else {
                    titleBarMessage.setTitle(title);
                }
            }
        });
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
                    messageViewModel.setMessageChange(DemoConstant.CONVERSATION_DELETE);
                }
            });
        });
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onRightClick(View view) {
        if(chatType == DemoConstant.CHATTYPE_SINGLE) {
            // 是否删除会话
            SimpleDialogFragment.showDialog(mContext, R.string.em_chat_delete_conversation, new BaseDialogFragment.OnConfirmClickListener() {
                @Override
                public void onConfirmClick(View view) {
                    EMConversation conversation = DemoHelper.getInstance().getConversation(toChatUsername,
                            EaseCommonUtils.getConversationType(chatType), true);
                    viewModel.deleteConversationById(conversation.conversationId());
                }
            });
        }else {
            // 跳转到群组设置
            if(chatType == DemoConstant.CHATTYPE_GROUP) {
                GroupDetailActivity.actionStart(mContext, toChatUsername);
            }else if(chatType == DemoConstant.CHATTYPE_CHATROOM) {
                ChatRoomDetailActivity.actionStart(mContext, toChatUsername);
            }
        }
    }
}
