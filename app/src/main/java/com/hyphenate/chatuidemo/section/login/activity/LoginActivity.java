package com.hyphenate.chatuidemo.section.login.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.base.BaseInitActivity;

public class LoginActivity extends BaseInitActivity implements View.OnClickListener {
    private TextView tv_login_register;

    public static void startAction(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_login;
    }

    @Override
    protected void initSystemFit() {
        setFitSystemForTheme(false, R.color.transparent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tv_login_register = findViewById(R.id.tv_login_register);
    }

    @Override
    protected void initListener() {
        super.initListener();
        tv_login_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_register :
                RegisterActivity.startAction(mContext);
                break;
        }
    }
}
