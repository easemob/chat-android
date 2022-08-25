package com.hyphenate.chatdemo.section.dialog;

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

public class DemoDialogFragment extends BaseDialogFragment implements View.OnClickListener {
    public TextView mTvDialogTitle;
    public Button mBtnDialogCancel;
    public Button mBtnDialogConfirm;
    public OnConfirmClickListener mOnConfirmClickListener;
    public onCancelClickListener mOnCancelClickListener;
    public Group mGroupMiddle;

    public String title;
    private int confirmColor;
    private String confirm;
    private boolean showCancel;
    private int titleColor;
    private String cancel;
    private float titleSize;
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

        if(!TextUtils.isEmpty(title)) {
            mTvDialogTitle.setText(title);
        }
        if(titleColor != 0) {
            mTvDialogTitle.setTextColor(titleColor);
        }
        if(titleSize != 0) {
            mTvDialogTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize);
        }
        if(!TextUtils.isEmpty(confirm)) {
            mBtnDialogConfirm.setText(confirm);
        }
        if(confirmColor != 0) {
            mBtnDialogConfirm.setTextColor(confirmColor);
        }
        if(!TextUtils.isEmpty(cancel)) {
            mBtnDialogCancel.setText(cancel);
        }
        if(showCancel) {
            mGroupMiddle.setVisibility(View.VISIBLE);
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
        private String title;
        private int titleColor;
        private float titleSize;
        private boolean showCancel;
        private String confirmText;
        private OnConfirmClickListener listener;
        private onCancelClickListener cancelClickListener;
        private int confirmColor;
        private Bundle bundle;
        private String cancel;
        private String content;

        public Builder(BaseActivity context) {
            this.context = context;
        }

        public Builder setTitle(@StringRes int title) {
            this.title = context.getString(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitleColor(@ColorRes int color) {
            this.titleColor = ContextCompat.getColor(context, color);
            return this;
        }

        public Builder setTitleColorInt(@ColorInt int color) {
            this.titleColor = color;
            return this;
        }

        public Builder setTitleSize(float size) {
            this.titleSize = size;
            return this;
        }

        public Builder setContent(@StringRes int content) {
            this.content = context.getString(content);
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder showCancelButton(boolean showCancel) {
            this.showCancel = showCancel;
            return this;
        }

        public Builder setOnConfirmClickListener(@StringRes int confirm, OnConfirmClickListener listener) {
            this.confirmText = context.getString(confirm);
            this.listener = listener;
            return this;
        }

        public Builder setOnConfirmClickListener(String confirm, OnConfirmClickListener listener) {
            this.confirmText = confirm;
            this.listener = listener;
            return this;
        }

        public Builder setOnConfirmClickListener(OnConfirmClickListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setConfirmColor(@ColorRes int color) {
            this.confirmColor = ContextCompat.getColor(context, color);
            return this;
        }

        public Builder setConfirmColorInt(@ColorInt int color) {
            this.confirmColor = color;
            return this;
        }

        public Builder setOnCancelClickListener(@StringRes int cancel, onCancelClickListener listener) {
            this.cancel = context.getString(cancel);
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(String cancel, onCancelClickListener listener) {
            this.cancel = cancel;
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(onCancelClickListener listener) {
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setArgument(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public DemoDialogFragment build() {
            DemoDialogFragment fragment = getFragment();
            fragment.setTitle(title);
            fragment.setTitleColor(titleColor);
            fragment.setTitleSize(titleSize);
            fragment.setContent(content);
            fragment.showCancelButton(showCancel);
            fragment.setConfirmText(confirmText);
            fragment.setOnConfirmClickListener(this.listener);
            fragment.setConfirmColor(confirmColor);
            fragment.setCancelText(cancel);
            fragment.setOnCancelClickListener(cancelClickListener);
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

    private void setTitleSize(float titleSize) {
        this.titleSize = titleSize;
    }

    private void setCancelText(String cancel) {
        this.cancel = cancel;
    }

    private void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    private void showCancelButton(boolean showCancel) {
        this.showCancel = showCancel;
    }

    private void setConfirmText(String confirmText) {
        this.confirm = confirmText;
    }

    private void setConfirmColor(int confirmColor) {
        this.confirmColor = confirmColor;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void setContent(String content) {
        this.content = content;
    }
}
