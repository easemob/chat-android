package com.hyphenate.chatdemo.section.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.section.base.BaseActivity;
import com.hyphenate.chatdemo.section.base.BaseDialogFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.lang.reflect.Field;
import java.util.Objects;

public class DemoDialogFragment extends BaseDialogFragment implements View.OnClickListener {
    public TextView mTvDialogTitle;
    public Button mBtnDialogCancel;
    public Button mBtnDialogConfirm;
    public OnConfirmClickListener mOnConfirmClickListener;
    public onCancelClickListener mOnCancelClickListener;
    public DialogInterface.OnDismissListener dismissListener;
    public Group mGroupMiddle;

    public String title;
    public String content;

    @Override
    public int getLayoutId() {
        return R.layout.demo_fragment_dialog_base;
    }

    @Override
    public void setChildView(View view) {
        super.setChildView(view);
        int layoutId = getMiddleLayoutId();
        if(layoutId > 0) {
            RelativeLayout middleParent = view.findViewById(R.id.rl_dialog_middle);
            if(middleParent != null) {
                LayoutInflater.from(mContext).inflate(layoutId, middleParent);
                //同时使middleParent可见
                view.findViewById(R.id.group_middle).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //宽度填满，高度自适应
        try {
            Window dialogWindow = getDialog().getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialogWindow.setAttributes(lp);

            View view = getView();
            if(view != null) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                if(params instanceof FrameLayout.LayoutParams) {
                    int margin = (int) EaseCommonUtils.dip2px(mContext, 30);
                    ((FrameLayout.LayoutParams) params).setMargins(margin, 0, margin, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int showAllowingStateLoss(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        try {
            Field dismissed = DemoDialogFragment.class.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field shown = DemoDialogFragment.class.getDeclaredField("mShownByMe");
            shown.setAccessible(true);
            shown.set(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        transaction.add(this, tag);
        try {
            Field viewDestroyed = DemoDialogFragment.class.getDeclaredField("mViewDestroyed");
            viewDestroyed.setAccessible(true);
            viewDestroyed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        int mBackStackId = transaction.commitAllowingStateLoss();
        try {
            Field backStackId = DemoDialogFragment.class.getDeclaredField("mBackStackId");
            backStackId.setAccessible(true);
            backStackId.set(this, mBackStackId);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return mBackStackId;
    }

    /**
     * 获取中间布局的id
     * @return
     */
    public int getMiddleLayoutId() {
        return 0;
    }

    public void initView(Bundle savedInstanceState) {
        mTvDialogTitle = findViewById(R.id.tv_dialog_title);
        mBtnDialogCancel = findViewById(R.id.btn_dialog_cancel);
        mBtnDialogConfirm = findViewById(R.id.btn_dialog_confirm);
        mGroupMiddle = findViewById(R.id.group_middle);

        Bundle bundle = getArguments();
        if(bundle != null) {
            title = bundle.getString(ParameterName.titleString);
            if(!TextUtils.isEmpty(title)) {
                mTvDialogTitle.setText(title);
            }

            content = bundle.getString(ParameterName.contentString);

            int titleColor = bundle.getInt(ParameterName.titleColorInt, 0);
            if(titleColor != 0) {
                mTvDialogTitle.setTextColor(titleColor);
            }

            int titleSize = bundle.getInt(ParameterName.titleSize, 0);
            if(titleSize != 0) {
                mTvDialogTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize);
            }

            String confirm = bundle.getString(ParameterName.confirmString);
            if(!TextUtils.isEmpty(confirm)) {
                mBtnDialogConfirm.setText(confirm);
            }
            int confirmColor = bundle.getInt(ParameterName.confirmColorInt, 0);
            if(confirmColor != 0) {
                mBtnDialogConfirm.setTextColor(confirmColor);
            }

            String cancel = bundle.getString(ParameterName.cancelString);
            if(!TextUtils.isEmpty(cancel)) {
                mBtnDialogCancel.setText(cancel);
            }

            boolean showCancel = bundle.getBoolean(ParameterName.showCancel, false);
            if(showCancel) {
                mGroupMiddle.setVisibility(View.VISIBLE);
            }

            boolean canceledOnTouchOutside = bundle.getBoolean(ParameterName.canceledOnTouchOutside, false);
            if(getDialog() != null) {
                getDialog().setCanceledOnTouchOutside(canceledOnTouchOutside);
            }
        }
    }

    public void initListener() {
        mBtnDialogCancel.setOnClickListener(this);
        mBtnDialogConfirm.setOnClickListener(this);
    }

    public void initData() {}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog_cancel :
                onCancelClick(v);
                break;
            case R.id.btn_dialog_confirm:
                onConfirmClick(v);
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(dismissListener != null) {
            dismissListener.onDismiss(getDialog());
        }
    }

    /**
     * 设置确定按钮的点击事件
     * @param listener
     */
    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.mOnConfirmClickListener = listener;
    }

    /**
     * 设置取消事件
     * @param cancelClickListener
     */
    public void setOnCancelClickListener(onCancelClickListener cancelClickListener) {
        this.mOnCancelClickListener = cancelClickListener;
    }

    private void setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    /**
     * 点击了取消按钮
     * @param v
     */
    public void onCancelClick(View v) {
        dismiss();
        if(mOnCancelClickListener != null) {
            mOnCancelClickListener.onCancelClick(v);
        }
    }

    /**
     * 点击了确认按钮
     * @param v
     */
    public void onConfirmClick(View v) {
        dismiss();
        if(mOnConfirmClickListener != null) {
            mOnConfirmClickListener.onConfirmClick(v);
        }
    }

    /**
     * 确定事件的点击事件
     */
    public interface OnConfirmClickListener {
        void onConfirmClick(View view);
    }

    /**
     * 点击取消
     */
    public interface onCancelClickListener {
        void onCancelClick(View view);
    }

    public static class Builder {
        public BaseActivity context;
        private OnConfirmClickListener listener;
        private onCancelClickListener cancelClickListener;
        private DialogInterface.OnDismissListener dismissListener;
        private DemoDialogFragment currentFragment;
        protected final Bundle bundle;

        public Builder(BaseActivity context) {
            this.context = context;
            this.bundle = new Bundle();
        }

        public Builder setTitle(@StringRes int title) {
            this.bundle.putString(ParameterName.titleString, context.getString(title));
            return this;
        }

        public Builder setTitle(String title) {
            this.bundle.putString(ParameterName.titleString, title);
            return this;
        }

        public Builder setTitleColor(@ColorRes int color) {
            this.bundle.putInt(ParameterName.titleColorInt, ContextCompat.getColor(context, color));
            return this;
        }

        public Builder setTitleColorInt(@ColorInt int color) {
            this.bundle.putInt(ParameterName.titleColorInt, color);
            return this;
        }

        public Builder setTitleSize(float size) {
            this.bundle.putFloat(ParameterName.titleSize, size);
            return this;
        }

        public Builder setContent(@StringRes int content) {
            this.bundle.putString(ParameterName.contentString, context.getString(content));
            return this;
        }

        public Builder setContent(String content) {
            this.bundle.putString(ParameterName.contentString, content);
            return this;
        }

        public Builder showCancelButton(boolean showCancel) {
            this.bundle.putBoolean(ParameterName.showCancel, showCancel);
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean cancel) {
            this.bundle.putBoolean(ParameterName.canceledOnTouchOutside, cancel);
            return this;
        }

        public Builder setOnConfirmClickListener(@StringRes int confirm, OnConfirmClickListener listener) {
            this.bundle.putString(ParameterName.confirmString, context.getString(confirm));
            this.listener = listener;
            return this;
        }

        public Builder setOnConfirmClickListener(String confirm, OnConfirmClickListener listener) {
            this.bundle.putString(ParameterName.confirmString, confirm);
            this.listener = listener;
            return this;
        }

        public Builder setOnConfirmClickListener(OnConfirmClickListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setConfirmColor(@ColorRes int color) {
            this.bundle.putInt(ParameterName.confirmColorInt, ContextCompat.getColor(context, color));
            return this;
        }

        public Builder setConfirmColorInt(@ColorInt int color) {
            this.bundle.putInt(ParameterName.confirmColorInt, color);
            return this;
        }

        public Builder setOnCancelClickListener(@StringRes int cancel, onCancelClickListener listener) {
            this.bundle.putString(ParameterName.cancelString, context.getString(cancel));
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(String cancel, onCancelClickListener listener) {
            this.bundle.putString(ParameterName.cancelString, cancel);
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(onCancelClickListener listener) {
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        public Builder setArgument(Bundle bundle) {
            if(bundle != null) {
                this.bundle.putAll(bundle);
            }
            return this;
        }

        public DemoDialogFragment build() {
            DemoDialogFragment fragment = getFragment();
            fragment.setOnConfirmClickListener(this.listener);
            fragment.setOnCancelClickListener(cancelClickListener);
            fragment.setOnDismissListener(this.dismissListener);
            fragment.setArguments(bundle);
            return fragment;
        }

        protected DemoDialogFragment getFragment() {
            return new DemoDialogFragment();
        }

        public DemoDialogFragment show() {
            DemoDialogFragment fragment = build();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragment.showAllowingStateLoss(transaction, null);
            return fragment;
        }
    }

    private static final class ParameterName {
        public static final String titleString = "titleString";
        public static final String titleColorInt = "titleColorInt";
        public static final String titleSize = "titleSize";
        public static final String contentString = "contentString";
        public static final String showCancel = "showCancel";
        public static final String canceledOnTouchOutside = "canceledOnTouchOutside";
        public static final String confirmString = "confirmString";
        public static final String confirmColorInt = "confirmColorInt";
        public static final String cancelString = "cancelString";
    }
}
