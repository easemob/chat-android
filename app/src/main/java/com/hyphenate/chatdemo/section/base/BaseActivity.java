package com.hyphenate.chatdemo.section.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.EMCallBack;
import com.hyphenate.chatdemo.DemoApplication;
import com.hyphenate.chatdemo.DemoHelper;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.constant.DemoConstant;
import com.hyphenate.chatdemo.common.enums.Status;
import com.hyphenate.chatdemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatdemo.common.interfaceOrImplement.UserActivityLifecycleCallbacks;
import com.hyphenate.chatdemo.common.livedatas.LiveDataBus;
import com.hyphenate.chatdemo.common.net.Resource;
import com.hyphenate.chatdemo.common.utils.DateUtils;
import com.hyphenate.chatdemo.common.utils.ToastUtils;
import com.hyphenate.chatdemo.common.widget.EaseProgressDialog;
import com.hyphenate.chatdemo.section.login.activity.LoginActivity;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.StatusBarCompat;
import com.hyphenate.util.EMLog;

import java.util.List;

/**
 * 作为基础activity,放置一些公共的方法
 */
public class BaseActivity extends AppCompatActivity {
    public BaseActivity mContext;
    private EaseProgressDialog dialog;
    private AlertDialog logoutDialog;
    private long dialogCreateTime;//dialog生成事件，用以判断dialog的展示时间
    private Handler handler = new Handler();//用于dialog延迟消失

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setGrayWhiteModel();
        clearFragmentsBeforeCreate();
        registerAccountObservable();
    }

    /**
     * 设置为灰白蒙层模式
     */
    private void setGrayWhiteModel() {
        boolean enableGrayWhiteModel = getResources().getBoolean(R.bool.enable_gray_white_model);
        if(!enableGrayWhiteModel) {
            return;
        }
        if(!DateUtils.isGrayWhiteDate(mContext)) {
            return;
        }
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        getWindow().getDecorView().setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }

    /**
     * 添加账号异常监听
     */
    protected void registerAccountObservable() {
        LiveDataBus.get().with(DemoConstant.ACCOUNT_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(!event.isAccountChange()) {
                return;
            }
            String accountEvent = event.event;
            if(TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_REMOVED) ||
                TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD) ||
                TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_KICKED_BY_OTHER_DEVICE)) {
                DemoHelper.getInstance().logout(false, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        DemoHelper.getInstance().getModel().setPhoneNumber("");
                        finishOtherActivities();
                        startActivity(new Intent(mContext, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(int code, String error) {
                        EMLog.e("logout", "logout error: error code = "+code + " error message = "+error);
                        showToast("logout error: error code = "+code + " error message = "+error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }else if(TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_CONFLICT)
                    || TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_REMOVED)
                    || TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_FORBIDDEN)) {
                DemoHelper.getInstance().getModel().setPhoneNumber("");
                DemoHelper.getInstance().logout(false, null);
                showExceptionDialog(accountEvent);
            }
        });
    }

    private void showExceptionDialog(String accountEvent) {
        if(logoutDialog != null && logoutDialog.isShowing() && !mContext.isFinishing()) {
            logoutDialog.dismiss();
        }
        logoutDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.em_account_logoff_notification)
                .setMessage(getExceptionMessageId(accountEvent))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishOtherActivities();
                        startActivity(new Intent(mContext, LoginActivity.class));
                        finish();
                    }
                })
                .setCancelable(false)
                .create();
        logoutDialog.show();
    }

    private int getExceptionMessageId(String exceptionType) {
        if(exceptionType.equals(DemoConstant.ACCOUNT_CONFLICT)) {
            return R.string.em_account_connect_conflict;
        } else if (exceptionType.equals(DemoConstant.ACCOUNT_REMOVED)) {
            return R.string.em_account_user_remove;
        } else if (exceptionType.equals(DemoConstant.ACCOUNT_FORBIDDEN)) {
            return R.string.em_account_user_forbidden;
        }
        return R.string.Network_error;
    }

    /**
     * 结束除了当前Activity外的其他Activity
     */
    protected void finishOtherActivities() {
        UserActivityLifecycleCallbacks lifecycleCallbacks = DemoApplication.getInstance().getLifecycleCallbacks();
        if(lifecycleCallbacks == null) {
            finish();
            return;
        }
        List<Activity> activities = lifecycleCallbacks.getActivityList();
        if(activities == null || activities.isEmpty()) {
            finish();
            return;
        }
        for(Activity activity : activities) {
            if(activity != lifecycleCallbacks.current()) {
                activity.finish();
            }
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading();
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
            callback.onLoading(response.data);
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
        dialogCreateTime = System.currentTimeMillis();
        dialog = new EaseProgressDialog.Builder(mContext)
                .setLoadingMessage(message)
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .show();
    }

    public void dismissLoading() {
        if(dialog != null && dialog.isShowing()) {
            //如果dialog的展示时间过短，则延迟1s再消失
            if(System.currentTimeMillis() - dialogCreateTime < 500 && !isFinishing()) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    }
                }, 1000);
            }else {
                dialog.dismiss();
                dialog = null;
            }

        }
    }

    /**
     * 处理因为Activity重建导致的fragment叠加问题
     */
    public void clearFragmentsBeforeCreate() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() == 0){
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : fragments) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.commitNow();
    }
}
