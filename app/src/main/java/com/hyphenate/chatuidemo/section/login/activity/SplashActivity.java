package com.hyphenate.chatuidemo.section.login.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.hyphenate.chatuidemo.MainActivity;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.utils.DemoLog;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.login.viewmodels.SplashViewModel;

public class SplashActivity extends BaseInitActivity {
    private ImageView ivSplash;
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
    }

    @Override
    protected void initData() {
        super.initData();
        model = new ViewModelProvider(this).get(SplashViewModel.class);

        Glide.with(mContext)
                .load(R.drawable.em_splash_bg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.with(new DrawableCrossFadeFactory.Builder(500).setCrossFadeEnabled(true).build()))
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        loginSDK();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loginSDK();
                        return false;
                    }
                })
                .into(ivSplash);


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
                    DemoLog.i("TAG", "error message = "+response.getMessage());
                    LoginActivity.startAction(mContext);
                    finish();
                }
            });

        });
    }
}
