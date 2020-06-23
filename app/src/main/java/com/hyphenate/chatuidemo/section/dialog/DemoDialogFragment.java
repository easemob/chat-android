package com.hyphenate.chatuidemo.section.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseDialogFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;

public class DemoDialogFragment extends BaseDialogFragment implements View.OnClickListener {
    public TextView mTvDialogTitle;
    public Button mBtnDialogCancel;
    public Button mBtnDialogConfirm;
    public OnConfirmClickListener mOnConfirmClickListener;
    public onCancelClickListener mOnCancelClickListener;
    public Group mGroupMiddle;

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
     * 点击了取消按钮
     * @param v
     */
    public void onCancelClick(View v) {
        dismiss();
    }

    /**
     * 点击了确认按钮
     * @param v
     */
    public void onConfirmClick(View v) {

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
}
