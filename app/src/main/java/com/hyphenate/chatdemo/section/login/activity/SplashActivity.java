package com.hyphenate.chatdemo.section.login.activity;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chatdemo.DemoApplication;
import com.hyphenate.chatdemo.MainActivity;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatdemo.common.utils.PreferenceManager;
import com.hyphenate.chatdemo.section.base.BaseInitActivity;
import com.hyphenate.chatdemo.section.dialog.DemoAgreementDialogFragment;
import com.hyphenate.chatdemo.section.dialog.DemoDialogFragment;
import com.hyphenate.chatdemo.section.login.viewmodels.SplashViewModel;
import com.hyphenate.util.EMLog;

public class SplashActivity extends BaseInitActivity {
    private ImageView ivSplash;
    private ImageView ivProduct;
    private SplashViewModel model;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_splash_activity;
    }

    @Override
    protected void initSystemFit() {
        setFitSystemForTheme(false, R.color.transparent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        ivSplash = findViewById(R.id.iv_splash);
        ivProduct = findViewById(R.id.iv_product);
    }

    @Override
    protected void initData() {
        super.initData();
        model = new ViewModelProvider(this).get(SplashViewModel.class);
        ivSplash.animate()
                .alpha(1)
                .setDuration(500)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        checkIfAgreePrivacy();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();

        ivProduct.animate()
                .alpha(1)
                .setDuration(500)
                .start();

    }

    private void checkIfAgreePrivacy() {
        if(PreferenceManager.getInstance().isAgreeAgreement()) {
            loginSDK();
        }else {
            showPrivacyDialog();
        }
    }

    private void showPrivacyDialog() {
        new DemoAgreementDialogFragment.Builder(mContext)
                .setTitle(R.string.demo_login_dialog_title)
                .setOnConfirmClickListener(R.string.demo_login_dialog_confirm, new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        PreferenceManager.getInstance().setAgreeAgreement(true);
                        DemoApplication.getInstance().initSDK();
                        loginSDK();
                    }
                })
                .setConfirmColor(R.color.red)
                .setOnCancelClickListener(R.string.demo_login_dialog_cancel, new DemoDialogFragment.onCancelClickListener() {
                    @Override
                    public void onCancelClick(View view) {
                        System.exit(1);
                    }
                })
                .show();
    }

    private void loginSDK() {
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
                    EMLog.i("TAG", "error message = "+response.getMessage());
                    LoginActivity.startAction(mContext);
                    finish();
                }
            });

        });
    }
}
