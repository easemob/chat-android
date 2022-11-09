package com.hyphenate.chatdemo.section.login.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chatdemo.BuildConfig;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatdemo.common.utils.CountDownTimerUtils;
import com.hyphenate.chatdemo.common.utils.ToastUtils;
import com.hyphenate.chatdemo.section.base.BaseInitFragment;
import com.hyphenate.chatdemo.section.login.activity.ChangePwdActivity;
import com.hyphenate.chatdemo.section.login.viewmodels.ChangePwdViewModel;
import com.hyphenate.chatdemo.section.login.viewmodels.LoginViewModel;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.EMLog;

public class ChangePwdFragment extends BaseInitFragment implements EaseTitleBar.OnBackPressListener, TextWatcher, View.OnClickListener,AdapterView.OnItemSelectedListener{

    private EaseTitleBar mToolbarRegister;
    private EditText mEtLoginName;
    private EditText mEtPhoneNum;
    private EditText mEtLoginVerificationCode;
    private EditText mEtImgVerificationCode;
    private ImageView mImgCodeView;
    private TextView mBtnGetCode;
    private Button mBtnNext;
    private Spinner areaCode;
    private LoginViewModel mViewModel;
    private ChangePwdViewModel mChangeViewModel;
    private String mAreaCode ="+86";
    private String phoneNum = "";
    private String smsCode = "";
    private String userName = "";
    private String imageCode = "";
    private String image_id ="";
    private CountDownTimerUtils count;
    public  String imgBaseUrl = BuildConfig.APP_SERVER_PROTOCOL + "://" + BuildConfig.APP_SERVER_DOMAIN + BuildConfig.APP_VERIFICATION_CODE;


    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_change_pwd;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mToolbarRegister = findViewById(R.id.toolbar_register);
        mEtLoginName = findViewById(R.id.et_login_name);
        mEtPhoneNum = findViewById(R.id.et_phone_number);
        mEtLoginVerificationCode = findViewById(R.id.et_verification_code);
        mEtImgVerificationCode = findViewById(R.id.et_img_verification_code);
        mImgCodeView = findViewById(R.id.img_code);
        mBtnGetCode = findViewById(R.id.bt_get_code);
        mBtnNext = findViewById(R.id.btn_next);
        areaCode = findViewById(R.id.areaCode);
    }

    @Override
    protected void initData() {
        super.initData();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.areaCode,
                android.R.layout.simple_dropdown_item_1line);
        areaCode.setAdapter(adapter);
        mViewModel = new ViewModelProvider(mContext).get(LoginViewModel.class);
        mChangeViewModel = new ViewModelProvider(mContext).get(ChangePwdViewModel.class);
        mChangeViewModel.clearRegisterInfo();
        mViewModel.getVerificationCodeObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(@Nullable Boolean data) {
                    ToastUtils.showToast("发送验证码成功");
                    count.start();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    mViewModel.getImageVerificationCode();
                    ToastUtils.showToast("发送验证码失败： code: " + code + " message: "+ message);
                    EMLog.e("mViewModel","发送验证码失败： code: " + code + " message: "+ message);
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
            });
        });
        mViewModel.getImageVerificationCode();

        mChangeViewModel.getCheckObservable().observe(this,response ->{
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(@Nullable Boolean data) {
                    Intent intent = new Intent(getActivity(),ChangePwdActivity.class);
                    intent.putExtra("userName",userName);
                    startActivity(intent);
                    onBackPress();
                }
            });
        });

    }

    @Override
    protected void initListener() {
        super.initListener();
        mBtnNext.setOnClickListener(this);
        mImgCodeView.setOnClickListener(this);
        mBtnGetCode.setOnClickListener(this);
        mEtLoginName.addTextChangedListener(this);
        mEtPhoneNum.addTextChangedListener(this);
        mEtLoginVerificationCode.addTextChangedListener(this);
        mEtImgVerificationCode.addTextChangedListener(this);
        areaCode.setOnItemSelectedListener(this);
        mToolbarRegister.setOnBackPressListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                mChangeViewModel.checkObservable(userName,phoneNum,smsCode);
                break;
            case R.id.img_code:
                mViewModel.getImageVerificationCode();
                break;
            case R.id.bt_get_code:
                if (mEtPhoneNum.getText().length() <= 0){
                    ToastUtils.showToast("请输入手机号");
                    break;
                }
                if (TextUtils.isEmpty(imageCode)){
                    ToastUtils.showToast("请输入图片验证码");
                    break;
                }
                count = new CountDownTimerUtils(mBtnGetCode, 60000, 1000);
                mViewModel.postVerificationCode(String.valueOf(mEtPhoneNum.getText()),image_id,imageCode);
                break;
        }
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

    private void checkEditContent() {
        userName = mEtLoginName.getText().toString().trim();
        phoneNum = mEtPhoneNum.getText().toString().trim();
        smsCode  = mEtLoginVerificationCode.getText().toString().trim();
        imageCode = mEtImgVerificationCode.getText().toString().trim();

        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(phoneNum) && !TextUtils.isEmpty(smsCode) && !TextUtils.isEmpty(imageCode)){
            mBtnNext.setEnabled(true);
        }else {
            mBtnNext.setEnabled(false);
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPress();
    }
}
