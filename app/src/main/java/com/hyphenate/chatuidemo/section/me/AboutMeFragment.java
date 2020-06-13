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
import com.hyphenate.chatuidemo.section.me.activity.AboutHxActivity;
import com.hyphenate.chatuidemo.section.me.activity.SetIndexActivity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.StatusBarCompat;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class AboutMeFragment extends BaseInitFragment implements View.OnClickListener {
    private ConstraintLayout clUser;
    private TextView name;
    private ArrowItemView itemCommonSet;
    private ArrowItemView itemFeedback;
    private ArrowItemView itemAboutHx;
    private ArrowItemView itemDeveloperSet;
    private Button mBtnLogout;
    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_about_me;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        clUser = findViewById(R.id.cl_user);
        name = findViewById(R.id.name);
        itemCommonSet = findViewById(R.id.item_common_set);
        itemFeedback = findViewById(R.id.item_feedback);
        itemAboutHx = findViewById(R.id.item_about_hx);
        itemDeveloperSet = findViewById(R.id.item_developer_set);
        mBtnLogout = findViewById(R.id.btn_logout);

        name.setText(DemoHelper.getInstance().getCurrentUser());
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBtnLogout.setOnClickListener(this);
        clUser.setOnClickListener(this);
        itemCommonSet.setOnClickListener(this);
        itemFeedback.setOnClickListener(this);
        itemAboutHx.setOnClickListener(this);
        itemDeveloperSet.setOnClickListener(this);
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
                SetIndexActivity.actionStart(mContext);
                break;
            case R.id.item_feedback:
                showToast("意见反馈");
                break;
            case R.id.item_about_hx:
                AboutHxActivity.actionStart(mContext);
                break;
            case R.id.item_developer_set:
                showToast("开发者服务");
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
