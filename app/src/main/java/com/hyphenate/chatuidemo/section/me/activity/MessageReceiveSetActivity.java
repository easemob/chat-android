package com.hyphenate.chatuidemo.section.me.activity;

import android.os.Bundle;
import android.view.View;

import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.widget.SwitchItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class MessageReceiveSetActivity extends BaseInitActivity implements SwitchItemView.OnCheckedChangeListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private SwitchItemView rlSwitchNotification;
    private SwitchItemView rlSwitchSound;
    private SwitchItemView rlSwitchVibrate;
    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_message_receive_set;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        rlSwitchNotification = findViewById(R.id.rl_switch_notification);
        rlSwitchSound = findViewById(R.id.rl_switch_sound);
        rlSwitchVibrate = findViewById(R.id.rl_switch_vibrate);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        rlSwitchNotification.setOnCheckedChangeListener(this);
        rlSwitchSound.setOnCheckedChangeListener(this);
        rlSwitchVibrate.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rl_switch_notification ://接收新消息通知

                break;
            case R.id.rl_switch_sound ://声音

                break;
            case R.id.rl_switch_vibrate ://震动

                break;
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
