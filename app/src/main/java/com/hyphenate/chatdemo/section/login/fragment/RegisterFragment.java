package com.hyphenate.chatdemo.section.login.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.EMError;
import com.hyphenate.chatdemo.BuildConfig;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatdemo.common.utils.CustomCountDownTimer;
import com.hyphenate.chatdemo.section.base.WebViewActivity;
import com.hyphenate.easeui.utils.EaseEditTextUtils;
import com.hyphenate.chatdemo.common.utils.ToastUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.chatdemo.section.base.BaseInitFragment;
import com.hyphenate.chatdemo.section.login.viewmodels.LoginViewModel;
import com.hyphenate.util.EMLog;

public class RegisterFragment extends BaseInitFragment implements TextWatcher, View.OnClickListener, EaseTitleBar.OnBackPressListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private EaseTitleBar mToolbarRegister;
    private EditText mEtLoginName;
    private EditText mEtLoginPwd;
    private EditText mEtLoginPwdConfirm;
    private EditText mEtLoginPhoneNumber;
    private EditText mEtLoginVerificationCode;
    private EditText mEtImgVerificationCode;
    private Button mBtnLogin;
    private TextView mBtnGetCode;
    private CheckBox cbSelect;
    private TextView tvAgreement;
    private String mUserName;
    private String mPwd;
    private String mPwdConfirm;
    private LoginViewModel mViewModel;
    private Drawable clear;
    private Drawable eyeOpen;
    private Drawable eyeClose;
    private ImageView mImgCodeView;
    private Spinner areaCode;
    private String mAreaCode ="+86";
    private String image_id ="";
    public  String imgBaseUrl = BuildConfig.APP_SERVER_PROTOCOL + "://" + BuildConfig.APP_SERVER_DOMAIN + BuildConfig.APP_VERIFICATION_CODE;
    private String phoneNum = "";
    private String smsCode = "";
    private String imageCode = "";
    private CustomCountDownTimer count;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_register;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mToolbarRegister = findViewById(R.id.toolbar_register);
        mEtLoginName = findViewById(R.id.et_login_phone);
        mEtLoginPwd = findViewById(R.id.ll_login_code);
        mEtLoginPwdConfirm = findViewById(R.id.et_login_pwd_confirm);
        mEtLoginPhoneNumber = findViewById(R.id.et_phone_number);
        mEtLoginVerificationCode = findViewById(R.id.et_verification_code);
        mEtImgVerificationCode = findViewById(R.id.et_img_verification_code);
        mBtnGetCode = findViewById(R.id.bt_get_code);
        mImgCodeView = findViewById(R.id.img_code);
        areaCode = findViewById(R.id.areaCode);
        mBtnLogin = findViewById(R.id.btn_login);
        cbSelect = findViewById(R.id.bottom_select);
        tvAgreement = findViewById(R.id.tv_agreement);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mEtLoginName.addTextChangedListener(this);
        mEtLoginPwd.addTextChangedListener(this);
        mEtLoginPwdConfirm.addTextChangedListener(this);
        mEtImgVerificationCode.addTextChangedListener(this);
        mEtLoginPhoneNumber.addTextChangedListener(this);
        mEtLoginVerificationCode.addTextChangedListener(this);
        mBtnLogin.setOnClickListener(this);
        mToolbarRegister.setOnBackPressListener(this);
        cbSelect.setOnCheckedChangeListener(this);
        mBtnGetCode.setOnClickListener(this);
        EaseEditTextUtils.clearEditTextListener(mEtLoginName);
        areaCode.setOnItemSelectedListener(this);
        mImgCodeView.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.areaCode,
                android.R.layout.simple_dropdown_item_1line);
        areaCode.setAdapter(adapter);
        tvAgreement.setText(getSpannable());
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        mViewModel = new ViewModelProvider(mContext).get(LoginViewModel.class);
        mViewModel.getImageVerificationCode();
        mViewModel.getRegisterFromAppServeObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>(true) {
                @Override
                public void onSuccess(Boolean data) {
                    ToastUtils.showToast(getResources().getString(R.string.em_register_success));
                    onBackPress();
                }

                @Override
                public void onError(int code, String message) {
                    if(code == EMError.USER_ALREADY_EXIST) {
                        ToastUtils.showToast(R.string.demo_error_user_already_exist);
                    }else {
                        ToastUtils.showToast("code: " + code + " "+message);
                    }
                    EMLog.e("RegisterFragment","注册失败： code: " + code + " message: "+ message);
                }

                @Override
                public void onLoading(Boolean data) {
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

        mViewModel.getVerificationCodeObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(@Nullable Boolean data) {
                    ToastUtils.showToast("发送短信验证码成功");
                    count.start();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    mViewModel.getImageVerificationCode();
                    ToastUtils.showToast("发送验证码失败： code: " + code + " message: "+ message);
                    EMLog.e("RegisterFragment","发送短信验证码失败： code: " + code + " message: "+ message);
                }

            });
        });

        mViewModel.getImgVerificationCodeObservable().observe(this,response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(@Nullable String data) {
                    image_id = data;
                    String image_url = imgBaseUrl + image_id;
                    if (null != getActivity())
                    Glide.with(getActivity())
                            .load(image_url)
                            .apply(new RequestOptions()
                            .error(R.drawable.ease_default_image))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mImgCodeView);
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    EMLog.e("RegisterFragment","获取图片验证码失败： code: " + code + " message: "+ message);
                }
            });
        });

        //切换密码可见不可见的两张图片
        eyeClose = getResources().getDrawable(R.drawable.d_pwd_hide);
        eyeOpen = getResources().getDrawable(R.drawable.d_pwd_show);
        clear = getResources().getDrawable(R.drawable.d_clear);
        EaseEditTextUtils.changePwdDrawableRight(mEtLoginPwd, eyeClose, eyeOpen, null, null, null);
        EaseEditTextUtils.changePwdDrawableRight(mEtLoginPwdConfirm, eyeClose, eyeOpen, null, null, null);
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
        mUserName = mEtLoginName.getText().toString().trim();
        mPwd = mEtLoginPwd.getText().toString().trim();
        mPwdConfirm = mEtLoginPwdConfirm.getText().toString().trim();
        if (mEtLoginPhoneNumber.getText().length() > 0){
            phoneNum = String.valueOf(mEtLoginPhoneNumber.getText()).trim();
        }

        if (mEtLoginVerificationCode.getText().length() > 0){
            smsCode = String.valueOf(mEtLoginVerificationCode.getText()).trim();
        }
        EMLog.e("checkEditContent",String.valueOf(mEtImgVerificationCode.getText()));
        if (mEtImgVerificationCode.getText().length() > 0){
            imageCode = String.valueOf(mEtImgVerificationCode.getText()).trim();
        }
        EaseEditTextUtils.showRightDrawable(mEtLoginName, clear);
        EaseEditTextUtils.showRightDrawable(mEtLoginPwd, eyeClose);
        EaseEditTextUtils.showRightDrawable(mEtLoginPwdConfirm, eyeClose);
        setButtonEnable(!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPwd) && !TextUtils.isEmpty(mPwdConfirm) && cbSelect.isChecked()
        && !TextUtils.isEmpty(phoneNum) && !TextUtils.isEmpty(smsCode) );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login :
                registerToHx();
                 break;
            case R.id.bt_get_code:
                if (mEtLoginPhoneNumber.getText().length() <= 0){
                    ToastUtils.showToast("请输入手机号");
                    break;
                }
                if (TextUtils.isEmpty(imageCode)){
                    ToastUtils.showToast("请输入图片验证码");
                    break;
                }
                count = new CustomCountDownTimer(mBtnGetCode, 60000, 1000);
                //mViewModel.postVerificationCode(String.valueOf(mEtLoginPhoneNumber.getText()),image_id,imageCode);
                break;
            case R.id.img_code:
                mViewModel.getImageVerificationCode();
                break;
        }
    }

    private void registerToHx() {
        if(!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPwd) && !TextUtils.isEmpty(mPwdConfirm)
        && !TextUtils.isEmpty(image_id) && !TextUtils.isEmpty(phoneNum) && !TextUtils.isEmpty(smsCode)) {
            if(!TextUtils.equals(mPwd, mPwdConfirm)) {
                showToast(R.string.em_password_confirm_error);
                return;
            }
            mViewModel.registerFromAppServe(mUserName,mPwd,phoneNum,smsCode,image_id,imageCode);
        }
    }

    private void setButtonEnable(boolean enable) {
        mBtnLogin.setEnabled(enable);
        //同时需要修改右侧drawalbeRight对应的资源
//        Drawable rightDrawable;
//        if(enable) {
//            rightDrawable = ContextCompat.getDrawable(mContext, R.drawable.demo_login_btn_right_enable);
//        }else {
//            rightDrawable = ContextCompat.getDrawable(mContext, R.drawable.demo_login_btn_right_unable);
//        }
//        mBtnLogin.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
    }

    @Override
    public void onBackPress(View view) {
        onBackPress();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.bottom_select :
                setButtonEnable(!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPwd) &&
                        !TextUtils.isEmpty(mPwdConfirm) && isChecked && !TextUtils.isEmpty(smsCode) && !TextUtils.isEmpty(phoneNum));
                break;
        }
    }

    private SpannableString getSpannable() {
        SpannableString spanStr = new SpannableString(getString(R.string.em_login_agreement));
        //设置下划线
        //spanStr.setSpan(new UnderlineSpan(), 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new MyClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                WebViewActivity.actionStart(mContext, getString(R.string.em_register_service_agreement_url));
            }
        }, 7, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //spanStr.setSpan(new UnderlineSpan(), 10, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new MyClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                WebViewActivity.actionStart(mContext, getString(R.string.em_register_privacy_agreement_url));
            }
        }, 14, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (areaCode.getItemAtPosition(position) instanceof String){
            mAreaCode = String.valueOf(areaCode.getItemAtPosition(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private abstract class MyClickableSpan extends ClickableSpan {

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.bgColor = Color.TRANSPARENT;
            ds.setColor(ContextCompat.getColor(mContext, R.color.blue));
        }
    }

}
