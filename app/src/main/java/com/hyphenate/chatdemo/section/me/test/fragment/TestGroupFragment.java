package com.hyphenate.easeim.section.me.test.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.databinding.DemoFragmentTestGroupBinding;
import com.hyphenate.easeim.section.base.BaseInitFragment;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.List;

public class TestGroupFragment extends BaseInitFragment implements View.OnClickListener {

    private DemoFragmentTestGroupBinding viewBinding;

    @Override
    protected View getLayoutView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        viewBinding = DemoFragmentTestGroupBinding.inflate(inflater);
        return viewBinding.getRoot();
    }

    @Override
    protected void initListener() {
        super.initListener();
        viewBinding.btnMuteGroupMember.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_mute_group_member ) {
            muteGroupMember();
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
        EMClient.getInstance().groupManager().aysncMuteGroupMembers(groupId, usernames, 1000000, new EMValueCallBack<EMGroup>() {
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
}
