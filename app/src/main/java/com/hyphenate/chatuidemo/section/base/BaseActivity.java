package com.hyphenate.chatuidemo.section.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.hyphenate.chatuidemo.DemoApplication;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.enums.Status;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.UserActivityLifecycleCallbacks;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.utils.ToastUtils;
import com.hyphenate.chatuidemo.common.widget.EaseProgressDialog;
import com.hyphenate.chatuidemo.section.conference.CallFloatWindow;
import com.hyphenate.chatuidemo.section.conference.ConferenceActivity;
import com.hyphenate.chatuidemo.section.conference.ConferenceInviteActivity;
import com.hyphenate.easeui.utils.StatusBarCompat;

import java.util.List;

/**
 * 作为基础activity,放置一些公共的方法
 */
public class BaseActivity extends AppCompatActivity {
    public BaseActivity mContext;
    private EaseProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        checkIfConferenceExit();
    }


    /**
     * 初始化toolbar
     * @param toolbar
     */
    public void initToolBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//有返回
            getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示title
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    /**
     * 设置返回按钮的颜色
     * @param mContext
     * @param colorId
     */
    public static void setToolbarCustomColor(AppCompatActivity mContext, int colorId) {
        Drawable leftArrow = ContextCompat.getDrawable(mContext, R.drawable.abc_ic_ab_back_material);
        if(leftArrow != null) {
            leftArrow.setColorFilter(ContextCompat.getColor(mContext, colorId), PorterDuff.Mode.SRC_ATOP);
            if(mContext.getSupportActionBar() != null) {
                mContext.getSupportActionBar().setHomeAsUpIndicator(leftArrow);
            }
        }
    }

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm!=null&&getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null){
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                super.onBackPressed();
            }else {
                super.onBackPressed();
            }
        }else {
            super.onBackPressed();
        }

    }

    /**
     * hide keyboard
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm!=null&&getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null){
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * toast by string
     * @param message
     */
    public void showToast(String message) {
        ToastUtils.showToast(message);
    }

    /**
     * toast by string res
     * @param messageId
     */
    public void showToast(@StringRes int messageId) {
        ToastUtils.showToast(messageId);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){

        if(null != this.getCurrentFocus()){
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }

        return super.onTouchEvent(event);
    }


    /**
     * 通用页面设置
     */
    public void setFitSystemForTheme() {
        setFitSystemForTheme(true, R.color.white);
        setStatusBarTextColor(true);
    }

    /**
     * 通用页面，需要设置沉浸式
     * @param fitSystemForTheme
     */
    public void setFitSystemForTheme(boolean fitSystemForTheme) {
        setFitSystemForTheme(fitSystemForTheme, R.color.white);
        setStatusBarTextColor(false);
    }

    /**
     * 通用页面，需要设置沉浸式
     * @param fitSystemForTheme
     */
    public void setFitSystemForTheme2(boolean fitSystemForTheme) {
        setFitSystemForTheme(fitSystemForTheme, "#ffffffff");
        setStatusBarTextColor(true);
    }

    /**
     * 设置是否是沉浸式，并可设置状态栏颜色
     * @param fitSystemForTheme
     * @param colorId 颜色资源路径
     */
    public void setFitSystemForTheme(boolean fitSystemForTheme, @ColorRes int colorId) {
        setFitSystem(fitSystemForTheme);
        //初始设置
        StatusBarCompat.compat(this, ContextCompat.getColor(mContext, colorId));
    }

    /**
     * 修改状态栏文字颜色
     * @param isLight 是否是浅色字体
     */
    public void setStatusBarTextColor(boolean isLight) {
        StatusBarCompat.setLightStatusBar(mContext, !isLight);
    }


    /**
     * 设置是否是沉浸式，并可设置状态栏颜色
     * @param fitSystemForTheme true 不是沉浸式
     * @param color 状态栏颜色
     */
    public void setFitSystemForTheme(boolean fitSystemForTheme, String color) {
        setFitSystem(fitSystemForTheme);
        //初始设置
        StatusBarCompat.compat(mContext, Color.parseColor(color));
    }

    /**
     * 设置是否是沉浸式
     * @param fitSystemForTheme
     */
    public void setFitSystem(boolean fitSystemForTheme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if(fitSystemForTheme) {
            ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View parentView = contentFrameLayout.getChildAt(0);
            if (parentView != null && Build.VERSION.SDK_INT >= 14) {
                parentView.setFitsSystemWindows(true);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    /**
     * 解析Resource<T>
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if(response == null) {
            return;
        }
        if(response.status == Status.SUCCESS) {
            callback.hideLoading();
            callback.onSuccess(response.data);
        }else if(response.status == Status.ERROR) {
            callback.hideLoading();
            if(!callback.hideErrorMsg) {
                showToast(response.getMessage());
            }
            callback.onError(response.errorCode, response.getMessage());
        }else if(response.status == Status.LOADING) {
            callback.onLoading();
        }
    }

    public boolean isMessageChange(String message) {
        if(TextUtils.isEmpty(message)) {
            return false;
        }
        if(message.contains("message")) {
            return true;
        }
        return false;
    }

    public boolean isContactChange(String message) {
        if(TextUtils.isEmpty(message)) {
            return false;
        }
        if(message.contains("contact")) {
            return true;
        }
        return false;
    }

    public boolean isGroupInviteChange(String message) {
        if(TextUtils.isEmpty(message)) {
            return false;
        }
        if(message.contains("invite")) {
            return true;
        }
        return false;
    }

    public boolean isNotify(String message) {
        if(TextUtils.isEmpty(message)) {
            return false;
        }
        if(message.contains("invite")) {
            return true;
        }
        return false;
    }
    /**
     * 将此方法放置在基类，用于检查是否有正在进行的音视频会议
     */
    private void checkIfConferenceExit() {
        // 如果当前的activity是否是ConferenceActivity
        if(this instanceof ConferenceActivity || this instanceof ConferenceInviteActivity) {
            return;
        }
        UserActivityLifecycleCallbacks lifecycleCallbacks = DemoApplication.getInstance().getLifecycleCallbacks();
        if(lifecycleCallbacks == null) {
            return;
        }
        List<Activity> activityList = lifecycleCallbacks.getActivityList();
        if(activityList != null && activityList.size() > 0) {
            for (Activity activity : activityList) {
                if(activity instanceof ConferenceActivity) {
                    //如果没有显示悬浮框，则启动ConferenceActivity
                    if(activity.isFinishing()) {
                        return;
                    }
                    if(!CallFloatWindow.getInstance(DemoApplication.getInstance()).isShowing()) {
                        ConferenceActivity.startConferenceCall(this, null);
                    }
                }
            }
        }
    }

    public void showLoading() {
        showLoading(getString(R.string.loading));
    }

    public void showLoading(String message) {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if(mContext.isFinishing()) {
            return;
        }
        dialog = new EaseProgressDialog.Builder(mContext)
                .setLoadingMessage(message)
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .show();
    }

    public void hideLoading() {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
