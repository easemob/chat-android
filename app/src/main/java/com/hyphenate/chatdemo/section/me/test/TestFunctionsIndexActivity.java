package com.hyphenate.chatdemo.section.me.test;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMPresence;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.databinding.ActivityTestFunctionsIndexBinding;
import com.hyphenate.chatdemo.section.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.List;

public class TestFunctionsIndexActivity extends BaseInitActivity implements View.OnClickListener {

    private ActivityTestFunctionsIndexBinding viewBinding;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TestFunctionsIndexActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected View getContentView() {
        viewBinding = ActivityTestFunctionsIndexBinding.inflate(getLayoutInflater());
        return viewBinding.getRoot();
    }

    @Override
    protected void initListener() {
        super.initListener();
        viewBinding.btnMuteGroupMember.setOnClickListener(this);
        viewBinding.btnPresenceUsername.setOnClickListener(this);
        viewBinding.titleBar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_mute_group_member ) {
            muteGroupMember();
        }else if(v.getId() == R.id.btn_presence_username) {
            presenceUsername();
        }

    }

    private void muteGroupMember() {
        String username = viewBinding.etMuteGroupMember.getText().toString().trim();
        String groupId = viewBinding.etMuteGroupId.getText().toString().trim();
        if(TextUtils.isEmpty(username)) {
            showToast("Username should not be null");
            return;
        }
        if(TextUtils.isEmpty(groupId)) {
            showToast("GroupId should not be null");
            return;
        }
        List<String> usernames = new ArrayList<>();
        usernames.add(username);
        EMClient.getInstance().groupManager().asyncMuteGroupMembers(groupId, usernames, 1000000, new EMValueCallBack<EMGroup>() {
            @Override
            public void onSuccess(EMGroup value) {
                showToast("Mute user: "+username+" in group: "+groupId+" success");
            }

            @Override
            public void onError(int error, String errorMsg) {
                showToast("Mute failed, error: "+error+" errormsg: "+errorMsg);
                EMLog.e("TAG", "Mute failed, error: "+error+" errormsg: "+errorMsg);
            }
        });
    }

    private void presenceUsername() {
        String username = viewBinding.etPresenceUsername.getText().toString().trim();
        if(TextUtils.isEmpty(username)) {
            showToast("Username should not be null");
            return;
        }
        List<String> usernames = new ArrayList<>();
        usernames.add(username);
        EMClient.getInstance().presenceManager().subscribePresences(usernames, 100000, new EMValueCallBack<List<EMPresence>>() {
            @Override
            public void onSuccess(List<EMPresence> value) {
                showToast("Presence user: "+username+" success");
            }

            @Override
            public void onError(int error, String errorMsg) {
                showToast("Presence failed, error: "+error+" errormsg: "+errorMsg);
                EMLog.e("TAG", "Presence failed, error: "+error+" errormsg: "+errorMsg);
            }
        });
    }
}
