package com.hyphenate.easeim.section.chat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.hyphenate.easeim.DemoApplication;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.chat.fragment.VideoCallFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class ChatVideoCallActivity extends BaseInitActivity {
    EaseTitleBar titleBar;
    private VideoCallFragment fragment;

    public static void actionStart(Context context, String toChatName) {
        Intent intent = new Intent(context, ChatVideoCallActivity.class);
        intent.putExtra("username", toChatName);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_common_fragment;
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
        fragment = new VideoCallFragment();
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
         * 为了在{@link com.hyphenate.easeim.common.interfaceOrImplement.UserActivityLifecycleCallbacks#restartSingleInstanceActivity(Activity)}
         * 中获取到是否是点击悬浮框事件，此处需要进行传递
         */
        boolean isClickByFloat = intent.getBooleanExtra("isClickByFloat", false);
        getIntent().putExtra("isClickByFloat", isClickByFloat);
        if(fragment != null && fragment.isAdded()) {
            fragment.onNewIntent(intent);
        }
    }

    @Override
    protected void onUserLeaveHint() {
        if(fragment != null && fragment.isAdded()) {
            fragment.onUserLeaveHint();
        }
    }

    @Override
    public void finish() {
        super.finish();
        DemoApplication.getInstance().getLifecycleCallbacks().makeMainTaskToFront(this);
    }

    @Override
    public void onBackPressed() {
        if(fragment != null && !fragment.isBackPress) {
            fragment.onBackPress();
        }else {
            super.onBackPressed();
        }
    }
}
