package com.hyphenate.chatuidemo.section.friends.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.livedatas.LiveDataBus;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.common.widget.SwitchItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.dialog.EditTextDialogFragment;
import com.hyphenate.chatuidemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.chatuidemo.section.friends.viewmodels.NewGroupViewModel;
import com.hyphenate.chatuidemo.section.group.GroupPickContactsActivity;
import com.hyphenate.chatuidemo.section.group.fragment.GroupEditFragment;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;

import androidx.annotation.Nullable;
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
    private NewGroupViewModel viewModel;
    private String[] newmembers;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, NewGroupActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_new_group;
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
                    finish();
                }
            });
        });
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
            SimpleDialogFragment.showDialog(mContext, R.string.em_group_new_name_cannot_be_empty, null);
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
                    return;
                }
                newmembers = data.getStringArrayExtra("newmembers");
                if(newmembers != null) {
                    itemGroupMembers.getTvContent().setText(newmembers.length+"");
                }
            }
        }
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
                GroupPickContactsActivity.actionStartForResult(mContext, ADD_NEW_CONTACTS);
                break;
        }
    }

    private void showGroupNameDialog() {
        EditTextDialogFragment.showDialog(mContext,
                getString(R.string.em_group_new_name_hint),
                itemGroupName.getTvContent().getText().toString().trim(), new EditTextDialogFragment.ConfirmClickListener(){

                    @Override
                    public void onConfirmClick(View view, String content) {
                        if(!TextUtils.isEmpty(content)) {
                            itemGroupName.getTvContent().setText(content);
                        }
                    }
                });
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
        EditTextDialogFragment.showDialog(mContext,
                getString(R.string.em_group_set_max_users),
                itemGroupMaxUsers.getTvContent().getText().toString().trim(),
                EditTextDialogFragment.DialogInputType.TYPE_CLASS_NUMBER,
                new EditTextDialogFragment.ConfirmClickListener(){

                    @Override
                    public void onConfirmClick(View view, String content) {
                        if(!TextUtils.isEmpty(content)) {
                            maxUsers = Integer.valueOf(content);
                            itemGroupMaxUsers.getTvContent().setText(content);
                        }
                    }
                });
    }

    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.item_switch_public :
                if(isChecked){
                    itemSwitchInvite.getTvTitle().setText(R.string.em_group_new_need_owner_approval);
                    itemSwitchInvite.getTvHint().setText("");
                }else{
                    itemSwitchInvite.getTvTitle().setText(R.string.em_group_new_open_invite);
                    itemSwitchInvite.getTvHint().setText(R.string.em_group_new_open_invite_hint);
                }
                break;
        }
    }
}
