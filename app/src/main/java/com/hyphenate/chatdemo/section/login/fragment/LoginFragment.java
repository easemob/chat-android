package com.hyphenate.chatdemo.section.login.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatdemo.DemoApplication;
import com.hyphenate.chatdemo.DemoHelper;
import com.hyphenate.chatdemo.MainActivity;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.db.DemoDbHelper;
import com.hyphenate.chatdemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatdemo.common.utils.CustomCountDownTimer;
import com.hyphenate.chatdemo.common.utils.PhoneNumberUtils;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseEditTextUtils;
import com.hyphenate.chatdemo.common.utils.ToastUtils;
import com.hyphenate.chatdemo.section.base.BaseInitFragment;
import com.hyphenate.chatdemo.section.login.viewmodels.LoginFragmentViewModel;
import com.hyphenate.chatdemo.section.login.viewmodels.LoginViewModel;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.Locale;

public class LoginFragment extends BaseInitFragment implements View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener, TextView.OnEditorActionListener {
    private EditText mEtLoginPhone;
    private EditText mEtLoginCode;
    private TextView mTvLoginRegister;
    private TextView mTvLoginToken;
    private TextView mTvResetPassword;
    private Button mBtnLogin;
    private CheckBox cbSelect;
    private TextView tvAgreement;
    private String mUserPhone;
    private String mCode;
    private LoginViewModel mViewModel;
    private boolean isTokenFlag;//是否是token登录
    private LoginFragmentViewModel mFragmentViewModel;
    private Drawable clear;
    private Drawable eyeOpen;
    private Drawable eyeClose;
    private boolean isClick;
    private TextView tvVersion;
    private int COUNTS = 5 ;
    private long DURATION = (long) (3 * 1000);
    private long[] mHits= new long[COUNTS];
    private TextView mTvGetCode;
    private CustomCountDownTimer countDownTimer;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_login;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mEtLoginPhone = findViewById(R.id.et_login_phone);
        mEtLoginCode = findViewById(R.id.et_login_code);
        mTvLoginRegister = findViewById(R.id.tv_login_register);
        mTvLoginToken = findViewById(R.id.tv_login_token);
        mBtnLogin = findViewById(R.id.btn_login);
        tvAgreement = findViewById(R.id.tv_agreement);
        cbSelect = findViewById(R.id.cb_select);
        tvVersion = findViewById(R.id.tv_version);
        mTvResetPassword = findViewById(R.id.tv_login_reset_password);
        mTvGetCode = findViewById(R.id.tv_get_code);
    }


    @Override
    protected void initListener() {
        super.initListener();
        mEtLoginPhone.addTextChangedListener(this);
        mEtLoginCode.addTextChangedListener(this);
        mTvLoginRegister.setOnClickListener(this);
        mTvLoginToken.setOnClickListener(this);
        tvVersion.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mTvGetCode.setOnClickListener(this);
        mTvResetPassword.setOnClickListener(this);
        cbSelect.setOnCheckedChangeListener(this);
        mEtLoginCode.setOnEditorActionListener(this);
        EaseEditTextUtils.clearEditTextListener(mEtLoginPhone);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mFragmentViewModel = new ViewModelProvider(this).get(LoginFragmentViewModel.class);
        mFragmentViewModel.getLoginFromAppServeObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>(true) {
                @Override
                public void onSuccess(String data) {
                    Log.e("login", "login success");
                    EMClient.getInstance().loginWithToken(mUserPhone, data, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            DemoDbHelper.getInstance(DemoApplication.getInstance()).initDb(mUserPhone);
                            DemoHelper.getInstance().setAutoLogin(true);
                            //跳转到主页
                            MainActivity.startAction(mContext);
                            mContext.finish();
                        }

                        @Override
                        public void onError(int code, String error) {

                        }
                    });
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    if(code == EMError.USER_AUTHENTICATION_FAILED) {
                        ToastUtils.showToast(R.string.demo_error_user_authentication_failed);
                    }else {
                        ToastUtils.showToast(message);
                    }
                }

                @Override
                public void onLoading(String data) {
                    super.onLoading(data);
                    showLoading();
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    LoginFragment.this.dismissLoading();
                }
            });

        });

        mFragmentViewModel.getLoginObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EaseUser>(true) {
                @Override
                public void onSuccess(EaseUser data) {
                    Log.e("login", "login success");
                    DemoHelper.getInstance().setAutoLogin(true);
                    DemoDbHelper.getInstance(DemoApplication.getInstance()).initDb(data.getUsername());
                    //跳转到主页
                    MainActivity.startAction(mContext);
                    mContext.finish();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    if(code == EMError.USER_AUTHENTICATION_FAILED) {
                        ToastUtils.showToast(R.string.demo_error_user_authentication_failed);
                    }else {
                        ToastUtils.showToast(message);
                    }
                }

                @Override
                public void onLoading(EaseUser data) {
                    super.onLoading(data);
                    showLoading();
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    LoginFragment.this.dismissLoading();
                }
            });

        });
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        mViewModel = new ViewModelProvider(mContext).get(LoginViewModel.class);
        mViewModel.getRegisterObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>(true) {
                @Override
                public void onSuccess(String data) {
                    mEtLoginPhone.setText(TextUtils.isEmpty(data)?"":data);
                    mEtLoginCode.setText("");
                }
            });

        });
        mViewModel.getVerificationCodeObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(@Nullable Boolean data) {
                    if(!mContext.isFinishing() && countDownTimer != null) {
                        countDownTimer.start();
                        ToastUtils.showToast(mContext.getString(R.string.em_login_post_code));
                    }
                }

                @Override
                public void onLoading(@Nullable Boolean data) {
                    super.onLoading(data);
                    showLoading();
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    dismissLoading();
                }
            });
        });
        DemoDbHelper.getInstance(mContext).getDatabaseCreatedObservable().observe(getViewLifecycleOwner(), response -> {
            Log.i("login", "本地数据库初始化完成");
        });
    }

    @Override
    protected void initData() {
        super.initData();
        // 保证切换fragment后相关状态正确
        boolean enableTokenLogin = DemoHelper.getInstance().getModel().isEnableTokenLogin();
        mTvLoginToken.setVisibility(enableTokenLogin ? View.VISIBLE : View.GONE);
        if(!TextUtils.isEmpty(DemoHelper.getInstance().getCurrentLoginUser())) {
            mEtLoginPhone.setText(DemoHelper.getInstance().getCurrentLoginUser());
        }
        tvVersion.setText("V"+ EMClient.VERSION);
        if(isTokenFlag) {
            switchLogin();
        }
        tvAgreement.setText(getSpannable());
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        //切换密码可见不可见的两张图片
        clear = getResources().getDrawable(R.drawable.d_clear);
        EaseEditTextUtils.showRightDrawable(mEtLoginPhone, clear);
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
                break;
            case R.id.tv_version:
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] =  SystemClock.uptimeMillis();
                if (mHits[0] >= SystemClock.uptimeMillis() - DURATION) {
                    mViewModel.setPageSelect(2);
                }
                break;
            case R.id.btn_login:
                hideKeyboard();
                loginToServer();
                break;
            case R.id.tv_login_reset_password:
                mViewModel.clearRegisterInfo();
                mViewModel.setPageSelect(3);
                break;
            case R.id.tv_get_code:
                getVerificationCode();
                break;
        }
    }

    private void getVerificationCode() {
        if (TextUtils.isEmpty(mUserPhone)){
            ToastUtils.showToast(mContext.getString(R.string.em_login_phone_empty));
            return;
        }
        if(!PhoneNumberUtils.isPhoneNumber(mUserPhone)) {
            ToastUtils.showToast(mContext.getString(R.string.em_login_phone_illegal));
            return;
        }
        if(countDownTimer == null) {
            countDownTimer = new CustomCountDownTimer(mTvGetCode, 60000, 1000);
        }
        mViewModel.postVerificationCode(mUserPhone);
    }

    /**
     * 切换登录方式
     */
    private void switchLogin() {
        mEtLoginCode.setText("");
        if(isTokenFlag) {
            mEtLoginCode.setHint(R.string.em_login_token_hint);
            mTvLoginToken.setText(R.string.em_login_tv_pwd);
            mEtLoginCode.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        }else {
            mEtLoginCode.setHint(R.string.em_login_password_hint);
            mTvLoginToken.setText(R.string.em_login_tv_token);
            mEtLoginCode.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    private void loginToServer() {
        if(TextUtils.isEmpty(mUserPhone)) {
            ToastUtils.showToast(mContext.getString(R.string.em_login_phone_empty));
            return;
        }
        if(!PhoneNumberUtils.isPhoneNumber(mUserPhone)) {
            ToastUtils.showToast(mContext.getString(R.string.em_login_phone_illegal));
            return;
        }
        if(TextUtils.isEmpty(mCode)) {
            ToastUtils.showToast(R.string.em_login_code_empty);
            return;
        }
        if(!PhoneNumberUtils.isNumber(mCode)) {
            ToastUtils.showToast(mContext.getString(R.string.em_login_illegal_code));
            return;
        }
        if(!cbSelect.isChecked()) {
            ToastUtils.showToast(mContext.getString(R.string.em_login_not_select_agreement));
            return;
        }
        isClick = true;
        if (DemoHelper.getInstance().getModel().isDeveloperMode()){
            mFragmentViewModel.login(mUserPhone, mCode,isTokenFlag);
        }else {
            mFragmentViewModel.loginFromAppServe(mUserPhone, mCode);
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
        mUserPhone = mEtLoginPhone.getText().toString().trim();
        mCode = mEtLoginCode.getText().toString().trim();
        EaseEditTextUtils.showRightDrawable(mEtLoginPhone, clear);
        EaseEditTextUtils.showRightDrawable(mEtLoginCode, isTokenFlag ? null : eyeClose);
        setButtonEnable(!TextUtils.isEmpty(mUserPhone) && !TextUtils.isEmpty(mCode));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_select :
                setButtonEnable(!TextUtils.isEmpty(mUserPhone) && !TextUtils.isEmpty(mCode) && isChecked);
                break;
        }
    }

    private void setButtonEnable(boolean enable) {
        mBtnLogin.setEnabled(enable);
        if(mEtLoginCode.hasFocus()) {
            mEtLoginCode.setImeOptions(enable ? EditorInfo.IME_ACTION_DONE : EditorInfo.IME_ACTION_PREVIOUS);
        }else if(mEtLoginPhone.hasFocus()) {
            mEtLoginCode.setImeOptions(enable ? EditorInfo.IME_ACTION_DONE : EditorInfo.IME_ACTION_NEXT);
        }

        //同时需要修改右侧drawalbeRight对应的资源
//        Drawable rightDrawable;
//        if(enable) {
//            rightDrawable = ContextCompat.getDrawable(mContext, R.drawable.demo_login_btn_right_enable);
//        }else {
//            rightDrawable = ContextCompat.getDrawable(mContext, R.drawable.demo_login_btn_right_unable);
//        }
//        mBtnLogin.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
    }

    private SpannableString getSpannable() {
        String language = Locale.getDefault().getLanguage();
        boolean isZh = language.startsWith("zh");
        SpannableString spanStr = new SpannableString(getString(R.string.em_login_agreement));
        int start1 = 29;
        int end1 = 45;
        int start2 = 50;
        int end2 = spanStr.length();
        if(isZh) {
            start1 = 5;
            end1 = 13;
            start2 = 14;
            end2 = spanStr.length();
        }
        //设置下划线
        //spanStr.setSpan(new UnderlineSpan(), 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spanStr.setSpan(new MyClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                jumpToAgreement();
            }
        }, start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new ForegroundColorSpan(Color.WHITE), start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //spanStr.setSpan(new UnderlineSpan(), 10, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new MyClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                jumpToProtocol();
            }
        }, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new ForegroundColorSpan(Color.WHITE), start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE) {
            if(!TextUtils.isEmpty(mUserPhone) && !TextUtils.isEmpty(mCode)) {
                hideKeyboard();
                loginToServer();
                return true;
            }
        }
        return false;
    }

    private void jumpToAgreement() {
        Uri uri = Uri.parse("http://www.easemob.com/agreement");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    private void jumpToProtocol() {
        Uri uri = Uri.parse("http://www.easemob.com/protocol");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    private abstract class MyClickableSpan extends ClickableSpan {

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.bgColor = Color.TRANSPARENT;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isClick = false;
    }

}
