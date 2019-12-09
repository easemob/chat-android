package com.hyphenate.chatuidemo.section.login.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.base.BaseInitActivity;
import com.hyphenate.chatuidemo.common.ApiResponse;
import com.hyphenate.chatuidemo.common.EmResult;
import com.hyphenate.chatuidemo.common.Status;
import com.hyphenate.chatuidemo.section.RegisterViewModel;

public class RegisterActivity extends BaseInitActivity implements TextWatcher, View.OnClickListener {
    private Toolbar toolbarRegister;
    private EditText et_login_name;
    private EditText et_login_pwd;
    private EditText et_login_pwd_confirm;
    private Button btn_login;
    private String mUserName;
    private String mPwd;
    private String mPwdConfirm;
    private RegisterViewModel mViewModel;

    public static void startAction(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initSystemFit() {
        setFitSystemForTheme(false, R.color.transparent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_register;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        toolbarRegister = findViewById(R.id.toolbar_register);
        et_login_name = findViewById(R.id.et_login_name);
        et_login_pwd = findViewById(R.id.et_login_pwd);
        et_login_pwd_confirm = findViewById(R.id.et_login_pwd_confirm);
        btn_login = findViewById(R.id.btn_login);
        initToolBar(toolbarRegister);
        setToolbarCustomColor(mContext, R.color.white);
    }

    @Override
    protected void initListener() {
        super.initListener();
        et_login_name.addTextChangedListener(this);
        et_login_pwd.addTextChangedListener(this);
        et_login_pwd_confirm.addTextChangedListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        mViewModel.getRegisterObservable().observe(this, response -> {
            Log.e("TAG", "register result = "+response);
            if(response.status == Status.SUCCESS) {
                Log.e("TAG", "注册成功！");
            } else if(response.status == Status.ERROR) {
                Log.e("TAG", "注册失败" + response.getMessage(mContext));

            }else {
                Log.e("TAG", "正在注册");
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        checkEditContent();
    }

    private void checkEditContent() {
        setButtonEnable(false);
        mUserName = et_login_name.getText().toString().trim();
        mPwd = et_login_pwd.getText().toString().trim();
        mPwdConfirm = et_login_pwd_confirm.getText().toString().trim();
        if(!TextUtils.isEmpty(mPwd) && !TextUtils.isEmpty(mPwdConfirm) && !TextUtils.equals(mPwd, mPwdConfirm)) {
            Toast.makeText(mContext, getResources().getString(R.string.em_register_failed), Toast.LENGTH_SHORT).show();
            return;
        }
        setButtonEnable(!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPwd) && !TextUtils.isEmpty(mPwdConfirm));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login :
                registerToHx();
                break;
        }
    }

    private void registerToHx() {
        if(!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPwd) && !TextUtils.isEmpty(mPwdConfirm)) {
            mViewModel.register(mUserName, mPwd);
        }
    }

    private void setButtonEnable(boolean enable) {
        btn_login.setEnabled(enable);
    }
}
