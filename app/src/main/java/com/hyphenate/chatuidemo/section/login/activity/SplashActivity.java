package com.hyphenate.chatuidemo.section.login.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.animation.AlphaAnimation;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.hyphenate.chatuidemo.MainActivity;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.utils.DemoLog;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.common.enums.Status;
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
    protected void initData() {
        super.initData();
        SplashViewModel model = new ViewModelProvider(this).get(SplashViewModel.class);
        model.getLoginData().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>(true) {
                @Override
                public void onSuccess(Boolean data) {
                    MainActivity.startAction(mContext);
                    finish();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    DemoLog.i("TAG", "error message = "+response.getMessage());
                    LoginActivity.startAction(mContext);
                    finish();
                }
            });

        });
    }
}
