package com.hyphenate.chatuidemo.section.friends.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.livedatas.MessageChangeLiveData;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.chatuidemo.section.friends.viewmodels.NewGroupViewModel;
import com.hyphenate.chatuidemo.section.group.GroupPickContactsActivity;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class NewGroupActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, EaseTitleBar.OnRightClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int ADD_NEW_CONTACTS = 10;
    private EaseTitleBar titleBar;
    private EditText etGroupName;
    private EditText etGroupIntroduction;
    private CheckBox cbPublic;
    private TextView secondDesc;
    private CheckBox cbMemberInviter;
    private int maxUsers = 200;
    private NewGroupViewModel viewModel;

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
        etGroupName = findViewById(R.id.et_group_name);
        etGroupIntroduction = findViewById(R.id.et_group_introduction);
        cbPublic = findViewById(R.id.cb_public);
        secondDesc = findViewById(R.id.second_desc);
        cbMemberInviter = findViewById(R.id.cb_member_inviter);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
        cbPublic.setOnCheckedChangeListener(this);
        cbMemberInviter.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(NewGroupViewModel.class);
        viewModel.groupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMGroup>() {
                @Override
                public void onSuccess(EMGroup data) {
                    MessageChangeLiveData.getInstance().postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
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
        String groupName = etGroupName.getText().toString().trim();
        if(TextUtils.isEmpty(groupName)) {
            SimpleDialogFragment.showDialog(mContext, R.string.em_group_new_name_cannot_be_empty, null);
            return;
        }
        GroupPickContactsActivity.actionStartForResult(mContext, ADD_NEW_CONTACTS);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_public :
                if(isChecked){
                    secondDesc.setText(R.string.em_group_new_need_owner_approval);
                }else{
                    secondDesc.setText(R.string.em_group_new_open_invite);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == ADD_NEW_CONTACTS) {
                if(data == null) {
                    return;
                }
                String[] newmembers = data.getStringArrayExtra("newmembers");
                String groupName = etGroupName.getText().toString().trim();
                String desc = etGroupIntroduction.getText().toString();
                EMGroupOptions option = new EMGroupOptions();
                option.maxUsers = maxUsers;
                option.inviteNeedConfirm = true;
                String reason = getString(R.string.em_group_new_invite_join_group, DemoHelper.getInstance().getCurrentUser(), groupName);
                if(cbPublic.isChecked()){
                    option.style = cbMemberInviter.isChecked() ? EMGroupStyle.EMGroupStylePublicJoinNeedApproval : EMGroupStyle.EMGroupStylePublicOpenJoin;
                }else{
                    option.style = cbMemberInviter.isChecked() ? EMGroupStyle.EMGroupStylePrivateMemberCanInvite : EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                }
                viewModel.createGroup(groupName, desc, newmembers, reason, option);
            }
        }
    }
}
