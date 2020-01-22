package com.hyphenate.chatuidemo.section.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class GroupMemberTypeActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemAdmin;
    private ArrowItemView itemMember;
    private String groupId;
    private EMGroup group;
    private boolean isOwner;

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_chat_group_member_type;
    }

    public static void actionStart(Context context, String groupId, boolean owner) {
        Intent starter = new Intent(context, GroupMemberTypeActivity.class);
        starter.putExtra("groupId", groupId);
        starter.putExtra("isOwner", owner);
        context.startActivity(starter);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
        isOwner = intent.getBooleanExtra("isOwner", false);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemAdmin = findViewById(R.id.item_admin);
        itemMember = findViewById(R.id.item_member);

        group = DemoHelper.getInstance().getGroupManager().getGroup(groupId);

        initGroupData();
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemAdmin.setOnClickListener(this);
        itemMember.setOnClickListener(this);
    }

    private void initGroupData() {
        itemAdmin.getTvContent().setText(getString(R.string.em_group_member_type_member_num, group.getAdminList().size()));
        itemMember.getTvContent().setText(getString(R.string.em_group_member_type_member_num, group.getMemberCount()));
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_admin ://管理员
                showToast("跳转到管理员页面");
                break;
            case R.id.item_member ://成员
                showToast("跳转到成员页面");
                break;
        }
    }
}
