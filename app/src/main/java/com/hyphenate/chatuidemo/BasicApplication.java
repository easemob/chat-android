package com.hyphenate.chatuidemo;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.hyphenate.chatuidemo.common.UserActivityLifecycleCallbacks;
import com.hyphenate.push.EMPushHelper;
import com.hyphenate.push.EMPushType;
import com.hyphenate.push.PushListener;
import com.hyphenate.util.EMLog;

public class BasicApplication extends Application {
    private UserActivityLifecycleCallbacks mLifecycleCallbacks = new UserActivityLifecycleCallbacks();

    @Override
    public void onCreate() {
        super.onCreate();
        initHx();
        registerActivityLifecycleCallbacks();
    }

    private void initHx() {
        // init hx sdk
        ChatHelper.getInstance().init(this);

        if(ChatHelper.getInstance().isMainProcess(this)) {
            HMSPushHelper.getInstance().initHMSAgent(this);
            EMPushHelper.getInstance().setPushListener(new PushListener() {
                @Override
                public void onError(EMPushType pushType, long errorCode) {
                    // TODO: 返回的errorCode仅9xx为环信内部错误，可从EMError中查询，其他错误请根据pushType去相应第三方推送网站查询。
                    EMLog.e("PushClient", "Push client occur a error: " + pushType + " - " + errorCode);
                }
            });
        }
    }

    private void registerActivityLifecycleCallbacks() {
        this.registerActivityLifecycleCallbacks(mLifecycleCallbacks);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
