package com.hyphenate.easeim.common.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.hyphenate.easeim.DemoApplication;

public class AppMetaDataHelper {
    private static AppMetaDataHelper instance;
    private Bundle metaBundle;
    private AppMetaDataHelper(){getMetaBundle();}

    public static AppMetaDataHelper getInstance() {
        if(instance == null) {
            synchronized (AppMetaDataHelper.class) {
                instance = new AppMetaDataHelper();
            }
        }
        return instance;
    }

    private void getMetaBundle() {
        Context context = DemoApplication.getInstance().getApplicationContext();
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if(info != null) {
                metaBundle = info.metaData;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从manifestPlaceholders中获取定义的值
     * @param key
     * @return
     */
    public String getPlaceholderValue(String key) {
        if(metaBundle != null) {
            return metaBundle.getString(key, "");
        }
        return "";
    }
}

