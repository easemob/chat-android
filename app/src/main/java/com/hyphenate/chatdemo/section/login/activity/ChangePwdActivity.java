package com.hyphenate.chatdemo.section.login.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatdemo.common.utils.ToastUtils;
import com.hyphenate.chatdemo.section.base.BaseInitActivity;
import com.hyphenate.chatdemo.section.login.viewmodels.ChangePwdViewModel;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class ChangePwdActivity extends BaseInitActivity implements View.OnClickListener , TextWatcher, EaseTitleBar.OnBackPressListener {

    private EditText mEtChangePassword;
    private EditText mEtChangeConfirmPassword;
    private Button mBtnConfirm;
    private EaseTitleBar mTitleBar;
    private ChangePwdViewModel viewModel;
    private String mNewPassword = "";
    private String mConfirmPwd = "";
    private String userName = "";

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_change_password;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar = findViewById(R.id.toolbar_register);
        mEtChangePassword = findViewById(R.id.et_login_pwd);
        mEtChangeConfirmPassword = findViewById(R.id.et_login_pwd_confirm);
        mBtnConfirm = findViewById(R.id.btn_confirm);
    }

    @Override
    protected void initData() {
        super.initData();
        if (getIntent().hasExtra("userName"))
            userName = getIntent().getStringExtra("userName");
        viewModel = new ViewModelProvider(mContext).get(ChangePwdViewModel.class);
        viewModel.getChangeObservable().observe(this,response ->{
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(@Nullable Boolean data) {
                    onBackPressed();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                }
            });
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBtnConfirm.setOnClickListener(this);
        mEtChangePassword.addTextChangedListener(this);
        mEtChangeConfirmPassword.addTextChangedListener(this);
        mTitleBar.setOnBackPressListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirm:
                if (!TextUtils.equals(mNewPassword,mConfirmPwd)){
                    ToastUtils.showToast(R.string.em_check_pwd);
                    return;
                }
                viewModel.changePasswordFromAppServe(userName,mNewPassword);
                break;
        }
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

    public void checkEditContent(){
        mNewPassword = mEtChangePassword.getText().toString().trim();
        mConfirmPwd = mEtChangeConfirmPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(mNewPassword) && !TextUtils.isEmpty(mConfirmPwd)){
            mBtnConfirm.setEnabled(true);
        }else {
            mBtnConfirm.setEnabled(false);
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
