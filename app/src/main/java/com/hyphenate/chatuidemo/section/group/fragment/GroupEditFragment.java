package com.hyphenate.chatuidemo.section.group.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseActivity;
import com.hyphenate.chatuidemo.section.base.BaseDialogFragment;
import com.hyphenate.easeui.utils.StatusBarCompat;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class GroupEditFragment extends BaseDialogFragment implements EaseTitleBar.OnRightClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private EditText etContent;
    private String content;
    private String hint;
    private OnSaveClickListener listener;
    private String title;

    public static void showDialog(BaseActivity activity, String title, String content, String hint, OnSaveClickListener listener) {
        GroupEditFragment fragment = new GroupEditFragment();
        fragment.setOnSaveClickListener(listener);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putString("hint", hint);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragment.show(transaction, null);
    }

    @Override
    public int getLayoutId() {
        return R.layout.em_fragment_group_edit;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.AppTheme);
        StatusBarCompat.setLightStatusBar(mContext, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            Window dialogWindow = getDialog().getWindow();
            //设置背景为透明
            int dialogHeight = getContextRect(mContext);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.dimAmount = 0.0f;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = dialogHeight == 0? ViewGroup.LayoutParams.MATCH_PARENT:dialogHeight;
            dialogWindow.setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取内容区域
    private int getContextRect(Activity activity){
        //应用区域
        Rect outRect1 = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        return outRect1.height();
    }

    @Override
    public void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if(bundle != null) {
            title = bundle.getString("title");
            content = bundle.getString("content");
            hint = bundle.getString("hint");
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        etContent = findViewById(R.id.et_content);

        if(TextUtils.isEmpty(content)) {
            etContent.setHint(hint);
        }else {
            etContent.setText(content);
        }

        titleBar.setTitle(title);
    }

    @Override
    public void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onRightClick(View view) {
        String content = etContent.getText().toString().trim();
        if(listener != null) {
            listener.onSaveClick(view, content);
        }
        dismiss();
    }

    @Override
    public void onBackPress(View view) {
        dismiss();
    }

    public interface OnSaveClickListener {
        void onSaveClick(View view, String content);
    }

}
