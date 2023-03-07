package com.hyphenate.chatdemo.common.utils;

import android.os.SystemClock;
import android.view.View;

public class FastClickUtils {

    private static long lastClickTime = -1L;
    private static long lastClickViewId = -1L;

    public static boolean isFastClick(View view,int interval){
        long currentClickTime = SystemClock.elapsedRealtime();
        boolean isFastClick = false;
        if (lastClickViewId == view.getId()) {
            if (currentClickTime - lastClickTime < interval) {
                isFastClick = true;
            }
        }
        lastClickViewId = view.getId();
        lastClickTime = currentClickTime;
        return isFastClick;
    }
}
