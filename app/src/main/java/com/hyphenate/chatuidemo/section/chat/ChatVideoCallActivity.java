package com.hyphenate.chatuidemo.section.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.easeui.ui.chat.fragment.EaseVideoCallFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class ChatVideoCallActivity extends BaseInitActivity {
    EaseTitleBar titleBar;
    private EaseVideoCallFragment fragment;

    public static void actionStart(Context context, String toChatName) {
        Intent intent = new Intent(context, ChatVideoCallActivity.class);
        intent.putExtra("username", toChatName);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_common_fragment;
    }

    @Override
    protected void initSystemFit() {
        setFitSystemForTheme(true, R.color.ease_chat_video_bg);
        setStatusBarTextColor(true);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        titleBar = findViewById(R.id.title_bar);
        titleBar.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        fragment = new EaseVideoCallFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username", getIntent().getStringExtra("username"));
        bundle.putBoolean("isComingCall", getIntent().getBooleanExtra("isComingCall", false));
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if(fragment != null) {
            fragment.onBackPress();
        }else {
            super.onBackPressed();
        }
    }
}
