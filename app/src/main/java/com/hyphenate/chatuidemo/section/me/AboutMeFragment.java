package com.hyphenate.chatuidemo.section.me;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hyphenate.EMCallBack;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.DialogCallBack;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.section.base.BaseDialogFragment;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.chatuidemo.section.dialog.DemoDialogFragment;
import com.hyphenate.chatuidemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.chatuidemo.section.login.activity.LoginActivity;

public class AboutMeFragment extends BaseInitFragment implements View.OnClickListener {
    private Button mBtnLogout;
    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_about_me;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mBtnLogout = findViewById(R.id.btn_logout);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBtnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout :
                logout();
                break;
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
