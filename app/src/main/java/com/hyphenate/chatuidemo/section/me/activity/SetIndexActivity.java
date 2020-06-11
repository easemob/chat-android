package com.hyphenate.chatuidemo.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.me.PrivacyIndexActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class SetIndexActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemSecurity;
    private ArrowItemView itemNotification;
    private ArrowItemView itemCommonSet;
    private ArrowItemView itemPrivacy;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SetIndexActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_set_index;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemSecurity = findViewById(R.id.item_security);
        itemNotification = findViewById(R.id.item_notification);
        itemCommonSet = findViewById(R.id.item_common_set);
        itemPrivacy = findViewById(R.id.item_privacy);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemSecurity.setOnClickListener(this);
        itemNotification.setOnClickListener(this);
        itemCommonSet.setOnClickListener(this);
        itemPrivacy.setOnClickListener(this);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_security ://账号与安全
                AccountSecurityActivity.actionStart(mContext);
                break;
            case R.id.item_notification ://消息设置

                break;
            case R.id.item_common_set ://通用

                break;
            case R.id.item_privacy ://隐私
                PrivacyIndexActivity.actionStart(mContext);
                break;
        }
    }
}
