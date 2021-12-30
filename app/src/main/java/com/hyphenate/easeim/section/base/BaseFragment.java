package com.hyphenate.easeim.section.base;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.interfaceOrImplement.DialogCallBack;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.utils.ToastUtils;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;

public class BaseFragment extends EaseBaseFragment {
    public BaseActivity mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (BaseActivity) context;
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

    public void showDialog(@StringRes int message, DialogCallBack callBack) {
        showDialog(getResources().getString(R.string.em_dialog_default_title), getResources().getString(message), callBack);
    }

    public void showDialog(String message, DialogCallBack callBack) {
        showDialog(getResources().getString(R.string.em_dialog_default_title), message, callBack);
    }

    public void showDialog(@StringRes int title, @StringRes int message, DialogCallBack callBack) {
        showDialog(getResources().getString(title), getResources().getString(message), callBack);
    }

    public void showDialog(String title, String message, DialogCallBack callBack) {
        new AlertDialog.Builder(mContext)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(callBack != null) {
                                callBack.onClick(dialog, which);
                            }
                        }
                    })
                    .setNegativeButton(mContext.getString(R.string.cancel), null)
                    .show();
    }

    /**
     * 解析Resource<T>
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if(mContext != null) {
            mContext.parseResource(response, callback);
        }
    }

    public void showLoading() {
        if(mContext != null) {
            mContext.showLoading();
        }
    }

    public void showLoading(String message) {
        if(mContext != null) {
            mContext.showLoading(message);
        }
    }

    public void dismissLoading() {
        if(mContext != null) {
            mContext.dismissLoading();
        }
    }
}
