package com.hyphenate.chatdemo.section.contact.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chatdemo.DemoHelper;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatdemo.common.utils.ToastUtils;
import com.hyphenate.chatdemo.section.base.BaseInitActivity;
import com.hyphenate.chatdemo.section.contact.viewmodels.ContactsViewModel;


public class SetRemarkActivity extends BaseInitActivity {
    private ContactsViewModel viewModel;
    private EditText edtRemark;
    private ImageView ivClear;
    private TextView tvCount;
    private String userId;
    private TextView tvSave;
    private ImageView ivBack;

    public static void actionStart(Activity context, String targetId) {
        Intent intent = new Intent(context, SetRemarkActivity.class);
        intent.putExtra("targetId", targetId);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        userId = getIntent().getStringExtra("targetId");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_remark;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        edtRemark = findViewById(R.id.edt_remark);
        ivClear = findViewById(R.id.iv_delete);
        tvCount = findViewById(R.id.tv_count);
        tvSave = findViewById(R.id.tv_done);
        ivBack=findViewById(R.id.iv_back);

    }

    @Override
    protected void initListener() {
        super.initListener();

        edtRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                tvCount.setText(length + "/16");
                edtRemark.setSelection(length);
                tvSave.setEnabled(length > 0);
                ivClear.setVisibility(length > 0 ? View.VISIBLE : View.GONE);
            }
        });
        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtRemark.setText("");
            }
        });
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remark = edtRemark.getText().toString().trim();
                viewModel.setContactRemark(userId, remark);
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(ContactsViewModel.class);
        viewModel.setRemarkObservable().observe(this, result->{
            parseResource(result, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    finish();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    ToastUtils.showToast(message);
                }
            });
        });

        String remark = DemoHelper.getInstance().getContactsRemarks().get(userId);
        if(!TextUtils.isEmpty(remark)) {
            edtRemark.setText(remark);
        }
    }
}
