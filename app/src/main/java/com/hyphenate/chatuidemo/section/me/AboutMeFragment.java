package com.hyphenate.chatuidemo.section.me;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.chatuidemo.section.dialog.DemoDialogFragment;
import com.hyphenate.chatuidemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.chatuidemo.section.friends.activity.ContactDetailActivity;
import com.hyphenate.chatuidemo.section.login.activity.LoginActivity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.StatusBarCompat;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class AboutMeFragment extends BaseInitFragment implements View.OnClickListener {
    private ConstraintLayout clUser;
    private TextView name;
    private ArrowItemView itemCommonSet;
    private ArrowItemView itemPrivacySet;
    private ArrowItemView itemMessageSet;
    private ArrowItemView itemServiceSet;
    private Button mBtnLogout;
    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_about_me;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        clUser = findViewById(R.id.cl_user);
        name = findViewById(R.id.name);
        itemCommonSet = findViewById(R.id.item_common_set);
        itemPrivacySet = findViewById(R.id.item_privacy_set);
        itemMessageSet = findViewById(R.id.item_message_set);
        itemServiceSet = findViewById(R.id.item_service_set);
        mBtnLogout = findViewById(R.id.btn_logout);

        name.setText(DemoHelper.getInstance().getCurrentUser());
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBtnLogout.setOnClickListener(this);
        clUser.setOnClickListener(this);
        itemCommonSet.setOnClickListener(this);
        itemPrivacySet.setOnClickListener(this);
        itemMessageSet.setOnClickListener(this);
        itemServiceSet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout :
                logout();
                break;
            case R.id.cl_user:
                EaseUser user = new EaseUser();
                user.setUsername(DemoHelper.getInstance().getCurrentUser());
                ContactDetailActivity.actionStart(mContext, user);
                break;
            case R.id.item_common_set:

                break;
            case R.id.item_privacy_set:
                PrivacyIndexActivity.actionStart(mContext);
                break;
            case R.id.item_message_set:

                break;
            case R.id.item_service_set:

                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        StatusBarCompat.compat(mContext, ContextCompat.getColor(mContext, R.color.transparent));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden) {
            StatusBarCompat.compat(mContext, ContextCompat.getColor(mContext, R.color.white));
        }else {
            StatusBarCompat.compat(mContext, ContextCompat.getColor(mContext, R.color.transparent));
        }
    }

    private void logout() {
        SimpleDialogFragment.showDialog(mContext, "是否退出？", new DemoDialogFragment.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(View view) {
                DemoHelper.getInstance().logout(true, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        LoginActivity.startAction(mContext);
                        mContext.finish();
                    }

                    @Override
                    public void onError(int code, String error) {
                        ThreadManager.getInstance().runOnMainThread(()-> showToast(error));
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        });
    }
}
