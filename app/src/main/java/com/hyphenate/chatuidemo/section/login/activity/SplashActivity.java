package com.hyphenate.chatuidemo.section.login.activity;

import android.os.Bundle;
import android.view.animation.AlphaAnimation;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hyphenate.chatuidemo.MainActivity;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.base.BaseInitActivity;
import com.hyphenate.chatuidemo.common.ApiResponse;
import com.hyphenate.chatuidemo.common.Result;
import com.hyphenate.chatuidemo.common.Status;
import com.hyphenate.chatuidemo.section.login.viewmodels.SplashViewModel;

public class SplashActivity extends BaseInitActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.em_splash_activity;
    }

    @Override
    protected void initSystemFit() {
        setFitSystemForTheme(false, R.color.transparent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(300);
        findViewById(R.id.iv_splash).startAnimation(animation);
    }

    @Override
    protected void initData() {
        super.initData();
        SplashViewModel model = ViewModelProviders.of(this).get(SplashViewModel.class);
        model.getLoginData().observe(this, new Observer<ApiResponse<Result<Boolean>>>() {
            @Override
            public void onChanged(ApiResponse<Result<Boolean>> response) {
                if(response.status == Status.SUCCESS) {

                    if(response.data != null && response.data.getResult()) {
                        MainActivity.startAction(mContext);
                    }else {
                        LoginActivity.startAction(mContext);
                    }
                    finish();

                }
            }
        });
    }
}
