package com.hyphenate.chatuidemo.section.login.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.MainActivity;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.enums.Status;
import com.hyphenate.chatuidemo.common.utils.ToastUtils;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.chatuidemo.section.login.activity.TestActivity;
import com.hyphenate.chatuidemo.section.login.viewmodels.LoginViewModel;

public class LoginFragment extends BaseInitFragment implements View.OnClickListener, TextWatcher {
    private EditText mEtLoginName;
    private EditText mEtLoginPwd;
    private TextView mTvLoginRegister;
    private TextView mTvLoginToken;
    private TextView mTvLoginServerSet;
    private Button mBtnLogin;
    private String mUserName;
    private String mPwd;
    private LoginViewModel mViewModel;
    private boolean isTokenFlag;//是否是token登录

    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_login;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mEtLoginName = findViewById(R.id.et_login_name);
        mEtLoginPwd = findViewById(R.id.et_login_pwd);
        mTvLoginRegister = findViewById(R.id.tv_login_register);
        mTvLoginToken = findViewById(R.id.tv_login_token);
        mTvLoginServerSet = findViewById(R.id.tv_login_server_set);
        mBtnLogin = findViewById(R.id.btn_login);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mEtLoginName.addTextChangedListener(this);
        mEtLoginPwd.addTextChangedListener(this);
        mTvLoginRegister.setOnClickListener(this);
        mTvLoginToken.setOnClickListener(this);
        mTvLoginServerSet.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        mViewModel = ViewModelProviders.of(mContext).get(LoginViewModel.class);
        mViewModel.getRegisterObservable().observe(this, response -> {
            if(response == null) {
                return;
            }
            if(response.status == Status.SUCCESS) {
                mEtLoginName.setText(TextUtils.isEmpty(response.data)?"":response.data);
                mEtLoginPwd.setText("");
            }
        });

        mViewModel.getLoginObservable().observe(this, response -> {
            if(response == null) {
                return;
            }
            if(response.status == Status.SUCCESS) {
                DemoHelper.getInstance().setAutoLogin(true);
                //跳转到主页
                MainActivity.startAction(mContext);
                mContext.finish();

            }else if(response.status == Status.ERROR) {
                ToastUtils.showFailToast(getResources().getString(R.string.em_login_failed), response.getMessage());

            }else if(response.status == Status.LOADING) {
                Log.e("TAG", "记载中");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_register :
                mViewModel.clearRegisterInfo();
                mViewModel.setPageSelect(1);
                break;
            case R.id.tv_login_token:
                isTokenFlag = !isTokenFlag;
                switchLogin();
//                TestActivity.startAction(mContext);
                break;
            case R.id.tv_login_server_set:
                mViewModel.setPageSelect(2);
                break;
            case R.id.btn_login:
                loginToServer();
                break;
        }
    }

    /**
     * 切换登录方式
     */
    private void switchLogin() {
        mEtLoginPwd.setText("");
        if(isTokenFlag) {
            mEtLoginPwd.setHint(R.string.em_login_token_hint);
            mTvLoginToken.setText(R.string.em_login_tv_pwd);
            mEtLoginPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        }else {
            mEtLoginPwd.setHint(R.string.em_login_password_hint);
            mTvLoginToken.setText(R.string.em_login_tv_token);
            mEtLoginPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    private void loginToServer() {
        if(TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPwd)) {
            ToastUtils.showToast(R.string.em_login_btn_info_incomplete);
            return;
        }
        mViewModel.login(mUserName, mPwd, isTokenFlag);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mUserName = mEtLoginName.getText().toString().trim();
        mPwd = mEtLoginPwd.getText().toString().trim();
        setButtonEnable(!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPwd));
    }

    private void setButtonEnable(boolean enable) {
        mBtnLogin.setEnabled(enable);
        //同时需要修改右侧drawalbeRight对应的资源
        Drawable rightDrawable;
        if(enable) {
            rightDrawable = ContextCompat.getDrawable(mContext, R.drawable.em_login_btn_right_enable);
        }else {
            rightDrawable = ContextCompat.getDrawable(mContext, R.drawable.em_login_btn_right_unable);
        }
        mBtnLogin.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
    }
}
