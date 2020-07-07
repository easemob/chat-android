package com.hyphenate.chatuidemo.section.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class GroupManageIndexActivity extends BaseInitActivity implements View.OnClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemBlackManager;
    private ArrowItemView itemMuteManage;
    private Button btnTransfer;
    private String groupId;

    public static void actionStart(Context context, String groupId) {
        Intent intent = new Intent(context, GroupManageIndexActivity.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_group_manage;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemBlackManager = findViewById(R.id.item_black_manager);
        itemMuteManage = findViewById(R.id.item_mute_manage);
        btnTransfer = findViewById(R.id.btn_transfer);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemBlackManager.setOnClickListener(this);
        itemMuteManage.setOnClickListener(this);
        btnTransfer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_black_manager ://黑名单
                GroupMemberAuthorityActivity.actionStart(mContext, groupId, GroupMemberAuthorityActivity.TYPE_BLACK);
                break;
            case R.id.item_mute_manage ://禁言列表
                GroupMemberAuthorityActivity.actionStart(mContext, groupId, GroupMemberAuthorityActivity.TYPE_MUTE);
                break;
            case R.id.btn_transfer ://移交
                GroupMemberAuthorityActivity.actionStart(mContext, groupId);
                break;
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
