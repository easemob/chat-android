package com.hyphenate.chatdemo.section.group.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.section.base.BaseActivity;
import com.hyphenate.chatdemo.section.base.BaseDialogFragment;
import com.hyphenate.easeui.utils.StatusBarCompat;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class GroupMemberDetailFragment extends BaseDialogFragment implements EaseTitleBar.OnRightClickListener, EaseTitleBar.OnBackPressListener, TextWatcher, View.OnClickListener {
    private EaseTitleBar titleBar;
    private EditText etContent;
    private OnSaveClickListener listener;
    private String title;
    private String content;
    private String hint;
    private ImageButton cleanBtn;

    @Override
    public int getLayoutId() {
        return R.layout.demo_fragment_group_member_detail;
    }

    public static void showDialog(BaseActivity activity, String title, String content, String hint,OnSaveClickListener listener) {
        GroupMemberDetailFragment fragment = new GroupMemberDetailFragment();
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
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        etContent = findViewById(R.id.et_content);
        cleanBtn = findViewById(R.id.clear);

        if(TextUtils.isEmpty(content)) {
            etContent.setHint(hint);
        }else {
            etContent.setText(content);
        }

        titleBar.setTitle(title);
        titleBar.getRightText().setTextColor(getResources().getColor(R.color.em_login_color_btn_enable_left));
        checkEditContent();

    }

    @Override
    public void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
        etContent.addTextChangedListener(this);
        cleanBtn.setOnClickListener(this);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        checkEditContent();
    }

    @Override
    public void onClick(View v) {
        etContent.setText("");
        checkEditContent();
    }

    public interface OnSaveClickListener {
        void onSaveClick(View view, String content);
    }

    //检查输入内容
    private void checkEditContent() {
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(content)){
            cleanBtn.setVisibility(View.GONE);
            titleBar.getRightText().setTextColor(getResources().getColor(R.color.em_color_common_text_gray));
        }else{
            cleanBtn.setVisibility(View.VISIBLE);
            titleBar.getRightText().setTextColor(getResources().getColor(R.color.em_login_color_btn_enable_left));
        }
    }
}
