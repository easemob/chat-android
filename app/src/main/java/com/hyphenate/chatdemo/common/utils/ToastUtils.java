package com.hyphenate.chatdemo.common.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.hyphenate.chatdemo.DemoApplication;
import com.hyphenate.chatdemo.R;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.lang.reflect.Field;

/**
 * Toast工具类，统一Toast样式，处理重复显示的问题，处理7.1.x版本crash的问题
 */
public class ToastUtils {
    private static final int DEFAULT = 0;
    private static final int SUCCESS = 1;
    private static final int FAIL = 2;
    private static final int TOAST_LAST_TIME = 1000;
    private static Toast toast;

    /**
     * 弹出成功的toast
     * @param message
     */
    public static void showSuccessToast(String message) {
        showCenterToast(null, message, SUCCESS, TOAST_LAST_TIME);
    }

    /**
     * 弹出成功的toast
     * @param message
     */
    public static void showSuccessToast(@StringRes int message) {
        showCenterToast(0, message, SUCCESS, TOAST_LAST_TIME);
    }

    /**
     * 弹出失败的toast
     * @param message
     */
    public static void showFailToast(String message) {
        showCenterToast(null, message, FAIL, TOAST_LAST_TIME);
    }

    /**
     * 弹出失败的toast
     * @param message
     */
    public static void showFailToast(@StringRes int message) {
        showCenterToast(0, message, FAIL, TOAST_LAST_TIME);
    }

    /**
     * 弹出默认的toast
     * @param message
     */
    public static void showToast(String message) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showBottomToast(null, message, DEFAULT, TOAST_LAST_TIME);
    }

    /**
     * 弹出默认的toast
     * @param message
     */
    public static void showToast(@StringRes int message) {
        showBottomToast(0, message, DEFAULT, TOAST_LAST_TIME);
    }

    /**
     * 弹出成功的toast，有标题
     * @param title
     * @param message
     */
    public static void showSuccessToast(String title, String message) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showCenterToast(title, message, SUCCESS, TOAST_LAST_TIME);
    }

    /**
     * 弹出成功的toast，有标题
     * @param title
     * @param message
     */
    public static void showSuccessToast(@StringRes int title, @StringRes int message) {
        showCenterToast(title, message, SUCCESS, TOAST_LAST_TIME);
    }

    /**
     * 弹出失败的toast，有标题
     * @param title
     * @param message
     */
    public static void showFailToast(String title, String message) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showCenterToast(title, message, FAIL, TOAST_LAST_TIME);
    }

    /**
     * 弹出失败的toast，有标题
     * @param title
     * @param message
     */
    public static void showFailToast(@StringRes int title, @StringRes int message) {
        showCenterToast(title, message, FAIL, TOAST_LAST_TIME);
    }

    /**
     * 弹出成功的toast，有标题，可以设置显示时长
     * @param title
     * @param message
     * @param duration
     */
    public static void showSuccessToast(String title, String message, int duration) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showCenterToast(title, message, SUCCESS, duration);
    }

    /**
     * 弹出成功的toast，有标题，可以设置显示时长
     * @param title
     * @param message
     * @param duration
     */
    public static void showSuccessToast(@StringRes int title, @StringRes int message, int duration) {
        showCenterToast(title, message, SUCCESS, duration);
    }

    /**
     * 弹出失败的toast，有标题，可以设置显示时长
     * @param title
     * @param message
     * @param duration
     */
    public static void showFailToast(String title, String message, int duration) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showCenterToast(title, message, FAIL, duration);
    }

    /**
     * 弹出失败的toast，有标题，可以设置显示时长
     * @param title
     * @param message
     * @param duration
     */
    public static void showFailToast(@StringRes int title, @StringRes int message, int duration) {
        showCenterToast(title, message, FAIL, duration);
    }

    /**
     * 弹出toast，无图标，无标题，可以设置显示时长
     * @param message
     * @param duration
     */
    public static void showToast(String message, int duration) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showCenterToast(null, message, DEFAULT, duration);
    }

    /**
     * 弹出toast，无图标，无标题，可以设置显示时长
     * @param message
     * @param duration
     */
    public static void showToast(@StringRes int message, int duration) {
        showCenterToast(0, message, DEFAULT, duration);
    }

    /**
     * 在屏幕中部显示，在此处传入application
     * @param title
     * @param message
     * @param type
     * @param duration
     */
    public static void showCenterToast(String title, String message, int type, int duration) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showToast(DemoApplication.getInstance(), title, message, type, duration, Gravity.CENTER);
    }

    /**
     * 在屏幕中部显示，在此处传入application
     * @param title
     * @param message
     * @param type
     * @param duration
     */
    public static void showCenterToast(@StringRes int title, @StringRes int message, int type, int duration) {
        showToast(DemoApplication.getInstance(), title, message, type, duration, Gravity.CENTER);
    }

    /**
     * 在屏幕底部显示，在此处传入application
     * @param title
     * @param message
     * @param type
     * @param duration
     */
    public static void showBottomToast(String title, String message, int type, int duration) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        showToast(DemoApplication.getInstance(), title, message, type, duration, Gravity.BOTTOM);
    }

    /**
     * 在屏幕底部显示，在此处传入application
     * @param title
     * @param message
     * @param type
     * @param duration
     */
    public static void showBottomToast(@StringRes int title, @StringRes int message, int type, int duration) {
        showToast(DemoApplication.getInstance(), title, message, type, duration, Gravity.BOTTOM);
    }

    /**
     * 此处判断toast不为空，选择cancel，是因为toast因为类型不同（是否显示图片）或者是否有标题，会导致不同的toast展示
     * @param context
     * @param title
     * @param message
     * @param type
     * @param duration
     * @param gravity
     */
    public static void showToast(Context context, @StringRes int title, @StringRes int message, int type, int duration, int gravity) {
        showToast(context, title == 0 ? null:context.getString(title), context.getString(message), type, duration, gravity);
    }

    /**
     * 此处判断toast不为空，选择cancel，是因为toast因为类型不同（是否显示图片）或者是否有标题，会导致不同的toast展示
     * @param context
     * @param title
     * @param message
     * @param type
     * @param duration
     * @param gravity
     */
    public static void showToast(Context context, String title, String message, int type, int duration, int gravity) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
        //保证在主线程中展示toast
        EaseThreadManager.getInstance().runOnMainThread(() -> {
            if(toast != null) {
                toast.cancel();
            }
            toast = getToast(context, title, message, type, duration, gravity);
            toast.show();
        });

    }

    private static Toast getToast(Context context, String title, String message, int type, int duration, int gravity) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.demo_toast_layout, null);
        toast.setView(toastView);
        ImageView ivToast = toastView.findViewById(R.id.iv_toast);
        TextView tvToastTitle = toastView.findViewById(R.id.tv_toast_title);
        TextView tvToastContent = toastView.findViewById(R.id.tv_toast_content);
        if(TextUtils.isEmpty(title)) {
            tvToastTitle.setVisibility(View.GONE);
        }else {
            tvToastTitle.setVisibility(View.VISIBLE);
            tvToastTitle.setText(title);
        }

        if(!TextUtils.isEmpty(message)) {
            tvToastContent.setText(message);
        }

        ivToast.setVisibility(View.VISIBLE);
        if(type == SUCCESS) {
            ivToast.setImageResource(R.drawable.em_toast_success);
        }else if(type == FAIL) {
            ivToast.setImageResource(R.drawable.em_toast_fail);
        }else {
            ivToast.setVisibility(View.GONE);
        }
        int yOffset = 0;
        if(gravity == Gravity.BOTTOM || gravity == Gravity.TOP) {
            yOffset = (int) EaseCommonUtils.dip2px(context, 50);
        }
        toast.setDuration(duration);
        toast.setGravity(gravity, 0, yOffset);
        hookToast(toast);
        return toast;
    }

    /**
     * 为了解决7.1.x版本toast可以导致crash的问题
     * @param toast
     */
    private static void hookToast(Toast toast) {
        Class<Toast> cToast = Toast.class;
        try {
            //TN是private的
            Field fTn = cToast.getDeclaredField("mTN");
            fTn.setAccessible(true);

            //获取tn对象
            Object oTn = fTn.get(toast);
            //获取TN的class，也可以直接通过Field.getType()获取。
            Class<?> cTn = oTn.getClass();
            Field fHandle = cTn.getDeclaredField("mHandler");

            //重新set->mHandler
            fHandle.setAccessible(true);
            fHandle.set(oTn, new HandlerProxy((Handler) fHandle.get(oTn)));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static class HandlerProxy extends Handler {

        private Handler mHandler;

        public HandlerProxy(Handler handler) {
            this.mHandler = handler;
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                mHandler.handleMessage(msg);
            } catch (WindowManager.BadTokenException e) {
                //ignore
            }
        }
    }


}
