package com.hyphenate.easeim.section.group.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.base.BaseActivity;
import com.hyphenate.easeim.section.base.BaseDialogFragment;
import com.hyphenate.easeui.utils.StatusBarCompat;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class GroupEditFragment extends BaseDialogFragment implements EaseTitleBar.OnRightClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private EditText etContent;
    private String content;
    private String hint;
    private OnSaveClickListener listener;
    private String title;
    private boolean canEdit;

    public static void showDialog(BaseActivity activity, String title, String content, String hint, OnSaveClickListener listener) {
        showDialog(activity, title, content, hint, true, listener);
    }

    public static void showDialog(BaseActivity activity, String title, String content, String hint, boolean canEdit, OnSaveClickListener listener) {
        GroupEditFragment fragment = new GroupEditFragment();
        fragment.setOnSaveClickListener(listener);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putString("hint", hint);
        bundle.putBoolean("canEdit", canEdit);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragment.show(transaction, null);
    }

    @Override
    public int getLayoutId() {
        return R.layout.demo_fragment_group_edit;
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
        setDialogFullParams();
    }

    @Override
    public void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if(bundle != null) {
            title = bundle.getString("title");
            content = bundle.getString("content");
            hint = bundle.getString("hint");
            canEdit = bundle.getBoolean("canEdit");
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

        etContent.setEnabled(canEdit);
        titleBar.setRightLayoutVisibility(canEdit ? View.VISIBLE : View.GONE);

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
