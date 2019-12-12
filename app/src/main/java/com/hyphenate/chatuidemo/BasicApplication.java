package com.hyphenate.chatuidemo;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.hyphenate.chatuidemo.core.interfaceOrImplement.UserActivityLifecycleCallbacks;
import com.hyphenate.push.EMPushHelper;
import com.hyphenate.push.EMPushType;
import com.hyphenate.push.PushListener;
import com.hyphenate.util.EMLog;

public class BasicApplication extends Application {
    private static BasicApplication instance;
    private UserActivityLifecycleCallbacks mLifecycleCallbacks = new UserActivityLifecycleCallbacks();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initHx();
        registerActivityLifecycleCallbacks();
    }

    private void initHx() {
        // init hx sdk
        if(DemoHelper.getInstance().getAutoLogin()) {
            DemoHelper.getInstance().init(this);
        }

    }

    private void registerActivityLifecycleCallbacks() {
        this.registerActivityLifecycleCallbacks(mLifecycleCallbacks);
    }

    public static BasicApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
