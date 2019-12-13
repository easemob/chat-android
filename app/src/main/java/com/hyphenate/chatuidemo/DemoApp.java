package com.hyphenate.chatuidemo;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.hyphenate.chatuidemo.common.interfaceOrImplement.UserActivityLifecycleCallbacks;
import com.hyphenate.chatuidemo.common.utils.PreferenceManager;

public class DemoApp extends Application {
    private static DemoApp instance;
    private UserActivityLifecycleCallbacks mLifecycleCallbacks = new UserActivityLifecycleCallbacks();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initHx();
        registerActivityLifecycleCallbacks();
    }

    private void initHx() {
        // 初始化PreferenceManager
        PreferenceManager.init(this);
        // init hx sdk
        if(DemoHelper.getInstance().getAutoLogin()) {
            DemoHelper.getInstance().init(this);
        }

    }

    private void registerActivityLifecycleCallbacks() {
        this.registerActivityLifecycleCallbacks(mLifecycleCallbacks);
    }

    public static DemoApp getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
