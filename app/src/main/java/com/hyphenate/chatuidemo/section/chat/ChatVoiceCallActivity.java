package com.hyphenate.chatuidemo.section.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hyphenate.chatuidemo.DemoApplication;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.chat.fragment.EaseVoiceCallFragment;
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
        return R.layout.demo_common_fragment;
    }

    @Override
    protected void initSystemFit() {
        setFitSystemForTheme(true, R.color.ease_chat_voice_bg);
        setStatusBarTextColor(true);
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
        bundle.putBoolean("isComingCall", getIntent().getBooleanExtra("isComingCall", false));
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment).commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /**
         * 为了在{@link com.hyphenate.chatuidemo.common.interfaceOrImplement.UserActivityLifecycleCallbacks#restartSingleInstanceActivity(Activity)}
         * 中获取到是否是点击悬浮框事件，此处需要进行传递
         */
        boolean isClickByFloat = intent.getBooleanExtra("isClickByFloat", false);
        getIntent().putExtra("isClickByFloat", isClickByFloat);
        if(fragment != null && fragment.isAdded()) {
            fragment.onNewIntent(intent);
        }
    }

    @Override
    public void finish() {
        super.finish();
        DemoApplication.getInstance().getLifecycleCallbacks().makeMainTaskToFront(this);
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
