package com.hyphenate.chatuidemo.section.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.ui.chat.EaseChatFragment;
import com.hyphenate.easeui.ui.chat.EaseChatRoomChatFragment;
import com.hyphenate.easeui.ui.chat.EaseGroupChatFragment;
import com.hyphenate.easeui.ui.chat.EaseSingleChatFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class EmChatActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBarMessage;
    private String toChatUsername;
    private int chatType;

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
        toChatUsername = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        chatType = getIntent().getIntExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBarMessage = findViewById(R.id.title_bar_message);
        EaseChatFragment fragment = null;
        switch (chatType) {
            case EaseConstant.CHATTYPE_SINGLE :
                fragment = new EaseSingleChatFragment();
                break;
            case EaseConstant.CHATTYPE_GROUP :
                fragment = new EaseGroupChatFragment();
                break;
            case EaseConstant.CHATTYPE_CHATROOM :
                fragment = new EaseChatRoomChatFragment();
                break;
        }
        if(fragment == null) {
            finish();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(EaseConstant.EXTRA_USER_ID, toChatUsername);
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
        titleBarMessage.setTitle(toChatUsername);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
