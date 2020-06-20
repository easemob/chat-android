package com.hyphenate.chatuidemo.section.dialog;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseActivity;

public class EditTextDialogFragment extends DemoDialogFragment {

    private EditText etInput;
    private String title;
    private String content;
    private int inputType;
    private ConfirmClickListener listener;

    public static void showDialog(BaseActivity context, String title, String content, ConfirmClickListener listener) {
        EditTextDialogFragment fragment = new EditTextDialogFragment();
        fragment.setOnConfirmClickListener(listener);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragment.show(transaction, null);
    }

    public static void showDialog(BaseActivity context, String title, String content, int inputType, ConfirmClickListener listener) {
        EditTextDialogFragment fragment = new EditTextDialogFragment();
        fragment.setOnConfirmClickListener(listener);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putInt("inputType", inputType);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragment.show(transaction, null);
    }

    @Override
    public int getMiddleLayoutId() {
        return R.layout.demo_fragment_dialog_edit;
    }

    @Override
    public void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if(bundle != null) {
            title = bundle.getString("title");
            content = bundle.getString("content");
            inputType = bundle.getInt("inputType", 0);
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        etInput = findViewById(R.id.et_input);
        mBtnDialogConfirm.setTextColor(ContextCompat.getColorStateList(mContext, R.color.demo_dialog_btn_text_brand_color_selector));
        mTvDialogTitle.setText(title);
        etInput.setText(content);
        if(inputType == DialogInputType.TYPE_CLASS_NUMBER) {
            etInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        }else if(inputType == DialogInputType.TYPE_CLASS_DECIMAL) {
            etInput.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }else if(inputType == DialogInputType.TYPE_CLASS_PASSWORD) {
            etInput.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else {
            etInput.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }

    @Override
    public void onConfirmClick(View v) {
        super.onConfirmClick(v);
        String content = etInput.getText().toString().trim();
        if(listener != null) {
            listener.onConfirmClick(v, content);
        }
        dismiss();
    }

    public void setOnConfirmClickListener(ConfirmClickListener listener) {
        this.listener = listener;
    }

    public interface ConfirmClickListener {
         void onConfirmClick(View view, String content);
    }


    public class DialogInputType {
        public static final int TYPE_CLASS_NONE = 0;
        public static final int TYPE_CLASS_NUMBER = 2;
        public static final int TYPE_CLASS_DECIMAL = 3;
        public static final int TYPE_CLASS_PASSWORD = 4;
    }
}
