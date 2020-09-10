package com.hyphenate.chatuidemo.section.group.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.constant.DemoConstant;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.chat.activity.ChatActivity;
import com.hyphenate.chatuidemo.section.contact.viewmodels.PublicGroupViewModel;
import com.hyphenate.easeui.widget.EaseTitleBar;

import androidx.lifecycle.ViewModelProvider;

public class GroupSimpleDetailActivity extends BaseInitActivity implements View.OnClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemGroupName;
    private ArrowItemView itemGroupOwner;
    private TextView tvGroupIntroduction;
    private EditText etReason;
    private Button btnAddToGroup;
    private String groupId;
    private EMGroup group;
    private PublicGroupViewModel viewModel;

    public static void actionStart(Context context, String groupId) {
        Intent starter = new Intent(context, GroupSimpleDetailActivity.class);
        starter.putExtra("groupId", groupId);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_group_simle_details;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = getIntent().getStringExtra("groupId");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemGroupName = findViewById(R.id.item_group_name);
        itemGroupOwner = findViewById(R.id.item_group_owner);
        tvGroupIntroduction = findViewById(R.id.tv_group_introduction);
        etReason = findViewById(R.id.et_reason);
        btnAddToGroup = findViewById(R.id.btn_add_to_group);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        btnAddToGroup.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(PublicGroupViewModel.class);
        viewModel.getGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMGroup>() {
                @Override
                public void onSuccess(EMGroup data) {
                    group = data;
                    setGroupInfo(group);
                }
            });
        });
        viewModel.getJoinObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    if(data) {
                        if(group.isMemberOnly()) {
                            showToast(R.string.send_the_request_is);
                        }else {
                            showToast(R.string.Join_the_group_chat);
                            ChatActivity.actionStart(mContext, group.getGroupId(), DemoConstant.CHATTYPE_GROUP);
                            finish();
                        }

                    }
                }
            });
        });
        viewModel.getGroup(groupId);

        group = DemoHelper.getInstance().getGroupManager().getGroup(groupId);
        if(group != null) {
            setGroupInfo(group);
        }
    }

    private void setGroupInfo(EMGroup group) {
        itemGroupName.getTvContent().setText(group.getGroupName());
        itemGroupOwner.getTvContent().setText(group.getOwner());
        tvGroupIntroduction.setText(group.getDescription());
        if(!group.getMembers().contains(EMClient.getInstance().getCurrentUser())){
            btnAddToGroup.setEnabled(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_to_group :
                addToGroup();
                break;
        }
    }

    private void addToGroup() {
        String reason = etReason.getText().toString().trim();
        if(TextUtils.isEmpty(reason)) {
            reason = getString(R.string.em_public_group_request_to_join);
        }
        viewModel.joinGroup(group, reason);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
