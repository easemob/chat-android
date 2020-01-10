package com.hyphenate.chatuidemo.section.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.easeui.ui.chat.fragment.EaseVoiceCallFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class ChatVoiceCallActivity extends BaseInitActivity {
    EaseTitleBar titleBar;
    private EaseVoiceCallFragment fragment;

    public static void actionStart(Context context, String toChatName) {
        Intent intent = new Intent(context, ChatVoiceCallActivity.class);
        intent.putExtra("username", toChatName);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_common_fragment;
    }

    @Override
    protected void initSystemFit() {
        setFitSystemForTheme(true, R.color.ease_chat_voice_bg, false);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        titleBar.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        fragment = new EaseVoiceCallFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username", getIntent().getStringExtra("username"));
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
