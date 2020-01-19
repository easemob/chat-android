package com.hyphenate.chatuidemo.section.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.chatuidemo.section.base.BaseActivity;

public class SimpleDialogFragment extends DemoDialogFragment {
    public static final String MESSAGE_KEY = "message";
    private String message;

    public static void showDialog(BaseActivity context, String message, OnConfirmClickListener listener) {
        SimpleDialogFragment fragment = new SimpleDialogFragment();
        fragment.setOnConfirmClickListener(listener);
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_KEY, message);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragment.show(transaction, null);
    }

    public static void showDialog(BaseActivity context, @StringRes int message, OnConfirmClickListener listener) {
        SimpleDialogFragment fragment = new SimpleDialogFragment();
        fragment.setOnConfirmClickListener(listener);
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_KEY, context.getResources().getString(message));
        fragment.setArguments(bundle);
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragment.show(transaction, null);
    }

    @Override
    public void initArgument() {
        if(getArguments() != null) {
            message = getArguments().getString(MESSAGE_KEY);
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        if(!TextUtils.isEmpty(message)) {
            mTvDialogTitle.setText(message);
        }
    }

    @Override
    public void onConfirmClick(View v) {
        super.onConfirmClick(v);
        dismiss();
        if(mOnConfirmClickListener != null) {
            mOnConfirmClickListener.onConfirmClick(v);
        }
    }

    /**
     * 设置确定按钮的点击事件
     * @param listener
     */
    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.mOnConfirmClickListener = listener;
    }

}
