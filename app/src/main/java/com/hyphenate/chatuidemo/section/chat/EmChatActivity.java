package com.hyphenate.chatuidemo.section.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.chat.fragment.ChatFragment;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.ui.chat.EaseChatFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class EmChatActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBarMessage;
    private String toChatUsername;
    private int chatType;
    private EaseChatFragment fragment;

    public static void actionStart(Context context, String userId, int chatType) {
        Intent intent = new Intent(context, EmChatActivity.class);
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
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBarMessage = findViewById(R.id.title_bar_message);
        fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EaseConstant.EXTRA_USER_ID, toChatUsername);
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "chat").commit();
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBarMessage.setOnBackPressListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        titleBarMessage.setTitle(getChatName());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent != null) {
            initIntent(intent);

        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    public String getChatName() {
        EMConversation.EMConversationType type = EaseCommonUtils.getConversationType(chatType);
        if(type == EMConversation.EMConversationType.ChatRoom) {
            EMChatRoom chatRoom = DemoHelper.getInstance().getChatroomManager().getChatRoom(toChatUsername);
            return chatRoom == null ? toChatUsername : chatRoom.getName();
        }else if(type == EMConversation.EMConversationType.GroupChat) {
            EMGroup group = DemoHelper.getInstance().getGroupManager().getGroup(toChatUsername);
            return group == null ? toChatUsername : group.getGroupName();
        }
        return toChatUsername;
    }
}
