package com.hyphenate.easeim.section.me.test.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMPresence;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.databinding.DemoFragmentTestPresenceBinding;
import com.hyphenate.easeim.section.base.BaseInitFragment;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.List;

public class TestPresenceFragment extends BaseInitFragment implements View.OnClickListener {

    private DemoFragmentTestPresenceBinding viewBinding;

    @Override
    protected View getLayoutView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        viewBinding = DemoFragmentTestPresenceBinding.inflate(inflater);
        return viewBinding.getRoot();
    }

    @Override
    protected void initListener() {
        super.initListener();
        viewBinding.btnPresenceUsername.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_presence_username) {
            presenceUsername();
        }
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
