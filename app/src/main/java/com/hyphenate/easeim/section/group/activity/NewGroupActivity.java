package com.hyphenate.easeim.section.group.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.widget.ArrowItemView;
import com.hyphenate.easeim.common.widget.SwitchItemView;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.chat.activity.ChatActivity;
import com.hyphenate.easeim.section.dialog.EditTextDialogFragment;
import com.hyphenate.easeim.section.dialog.SimpleDialogFragment;
import com.hyphenate.easeim.section.contact.viewmodels.NewGroupViewModel;
import com.hyphenate.easeim.section.group.fragment.GroupEditFragment;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

public class NewGroupActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, EaseTitleBar.OnRightClickListener, View.OnClickListener, SwitchItemView.OnCheckedChangeListener {
    private static final int ADD_NEW_CONTACTS = 10;
    private EaseTitleBar titleBar;
    private ArrowItemView itemGroupName;
    private ArrowItemView itemGroupProfile;
    private ArrowItemView itemGroupMaxUsers;
    private SwitchItemView itemSwitchPublic;
    private SwitchItemView itemSwitchInvite;
    private ArrowItemView itemGroupMembers;
    private int maxUsers = 200;
    private static final int MAX_GROUP_USERS = 2000;
    private NewGroupViewModel viewModel;
    private String[] newmembers;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, NewGroupActivity.class);
        context.startActivity(starter);
    }

    public static void actionStart(Context context, String[] newmembers) {
        Intent intent = new Intent(context, NewGroupActivity.class);
        intent.putExtra("newmembers", newmembers);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_new_group;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        newmembers = intent.getStringArrayExtra("newmembers");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemGroupName = findViewById(R.id.item_group_name);
        itemGroupProfile = findViewById(R.id.item_group_profile);
        itemGroupMaxUsers = findViewById(R.id.item_group_max_users);
        itemSwitchPublic = findViewById(R.id.item_switch_public);
        itemSwitchInvite = findViewById(R.id.item_switch_invite);
        itemGroupMembers = findViewById(R.id.item_group_members);

        itemGroupName.getTvContent().setHint(getString(R.string.em_group_new_name_hint));
        itemGroupProfile.getTvContent().setHint(getString(R.string.em_group_new_profile_hint));
        itemGroupMaxUsers.getTvContent().setText(String.valueOf(maxUsers));
        titleBar.getRightText().setTextColor(ContextCompat.getColor(mContext, R.color.em_color_brand));
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
        itemGroupName.setOnClickListener(this);
        itemGroupProfile.setOnClickListener(this);
        itemGroupMaxUsers.setOnClickListener(this);
        itemGroupMembers.setOnClickListener(this);
        itemSwitchPublic.setOnCheckedChangeListener(this);
        itemSwitchInvite.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(NewGroupViewModel.class);
        viewModel.groupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMGroup>() {
                @Override
                public void onSuccess(EMGroup data) {
                    LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
                    //跳转到群组聊天页面
                    ChatActivity.actionStart(mContext, data.getGroupId(), DemoConstant.CHATTYPE_GROUP);
                    finish();
                }
            });
        });

        if(newmembers != null) {
            setGroupMembersNum(newmembers.length+"");
        }else {
            setGroupMembersNum("0");
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onRightClick(View view) {
        checkGroupInfo();
    }

    private void checkGroupInfo() {
        String groupName = itemGroupName.getTvContent().getText().toString().trim();
        if(TextUtils.isEmpty(groupName)) {
            new SimpleDialogFragment.Builder(mContext)
                    .setTitle(R.string.em_group_new_name_cannot_be_empty)
                    .show();
            return;
        }
        String desc = itemGroupProfile.getTvContent().getText().toString();
        EMGroupOptions option = new EMGroupOptions();
        option.maxUsers = maxUsers;
        option.inviteNeedConfirm = true;
        String reason = getString(R.string.em_group_new_invite_join_group, DemoHelper.getInstance().getCurrentUser(), groupName);
        if(itemSwitchPublic.getSwitch().isChecked()){
            option.style = itemSwitchInvite.getSwitch().isChecked() ? EMGroupStyle.EMGroupStylePublicJoinNeedApproval : EMGroupStyle.EMGroupStylePublicOpenJoin;
        }else{
            option.style = itemSwitchInvite.getSwitch().isChecked() ? EMGroupStyle.EMGroupStylePrivateMemberCanInvite : EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
        }
        viewModel.createGroup(groupName, desc, newmembers, reason, option);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == ADD_NEW_CONTACTS) {
                if(data == null) {
                    setGroupMembersNum("0");
                    return;
                }
                newmembers = data.getStringArrayExtra("newmembers");
                if(newmembers != null) {
                    setGroupMembersNum(newmembers.length+"");
                }else {
                    setGroupMembersNum("0");
                }
            }
        }
    }

    private void setGroupMembersNum(String num) {
        itemGroupMembers.getTvContent().setText(num);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_group_name :
                showGroupNameDialog();
                break;
            case R.id.item_group_profile :
                showProfileDialog();
                break;
            case R.id.item_group_max_users :
                setGroupMaxUsersDialog();
                break;
            case R.id.item_group_members :
                GroupPickContactsActivity.actionStartForResult(mContext, newmembers, ADD_NEW_CONTACTS);
                break;
        }
    }

    private void showGroupNameDialog() {
        new EditTextDialogFragment.Builder(mContext)
                .setContent(itemGroupName.getTvContent().getText().toString().trim())
                .setConfirmClickListener(new EditTextDialogFragment.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, String content) {
                        if(!TextUtils.isEmpty(content)) {
                            itemGroupName.getTvContent().setText(content);
                        }
                    }
                })
                .setTitle(R.string.em_group_new_name_hint)
                .show();
    }

    private void showProfileDialog() {
        GroupEditFragment.showDialog(mContext,
                getString(R.string.em_group_new_profile),
                itemGroupProfile.getTvContent().getText().toString().trim(),
                getString(R.string.em_group_new_profile_hint),
                new GroupEditFragment.OnSaveClickListener() {
                    @Override
                    public void onSaveClick(View view, String content) {
                        //群简介
                        if(!TextUtils.isEmpty(content)) {
                            itemGroupProfile.getTvContent().setText(content);
                        }
                    }
                });
    }

    private void setGroupMaxUsersDialog() {
        new EditTextDialogFragment.Builder(mContext)
                .setContent(itemGroupMaxUsers.getTvContent().getText().toString().trim())
                .setContentInputType(InputType.TYPE_CLASS_NUMBER)
                .setConfirmClickListener(new EditTextDialogFragment.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, String content) {
                        if(!TextUtils.isEmpty(content)) {
                            maxUsers = Integer.valueOf(content);
                            if(maxUsers > MAX_GROUP_USERS) {
                                maxUsers = Integer.valueOf(itemGroupMaxUsers.getTvContent().getText().toString().trim());
                                showToast("建群最大人数不能超过2000！");
                                return;
                            }
                            itemGroupMaxUsers.getTvContent().setText(content);
                        }
                    }
                })
                .setTitle(R.string.em_group_set_max_users)
                .show();
    }

    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.item_switch_public :
                //操作则将switch_invite按钮还原
                itemSwitchInvite.getSwitch().setChecked(false);
                if(isChecked){
                    itemSwitchPublic.getTvHint().setText(R.string.em_group_new_if_public_check_hint);
                    itemSwitchInvite.getTvTitle().setText(R.string.em_group_new_need_owner_approval_public);
                    itemSwitchInvite.getTvHint().setText(R.string.em_group_new_need_owner_approval_uncheck_hint);
                }else{
                    itemSwitchPublic.getTvHint().setText(R.string.em_group_new_if_public_uncheck_hint);
                    itemSwitchInvite.getTvTitle().setText(R.string.em_group_new_open_invite);
                    itemSwitchInvite.getTvHint().setText(R.string.em_group_new_open_invite_uncheck_hint);
                }
                break;
            case R.id.item_switch_invite:
                if(itemSwitchPublic.getSwitch().isChecked()) {
                    itemSwitchInvite.getTvHint().setText(isChecked
                            ? R.string.em_group_new_need_owner_approval_check_hint
                            : R.string.em_group_new_need_owner_approval_uncheck_hint);
                }else {
                    itemSwitchInvite.getTvHint().setText(isChecked
                            ? R.string.em_group_new_open_invite_check_hint
                            : R.string.em_group_new_open_invite_uncheck_hint);
                }

                break;
        }
    }
}
