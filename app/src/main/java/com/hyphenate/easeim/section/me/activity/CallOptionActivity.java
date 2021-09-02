package com.hyphenate.easeim.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeim.common.widget.SwitchItemView;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linan on 16/11/29.
 */
public class CallOptionActivity extends BaseInitActivity implements SwitchItemView.OnCheckedChangeListener {
    private EaseTitleBar titleBar;
    private SwitchItemView rlSwitchOfflineCallPush;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CallOptionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_call_option;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        rlSwitchOfflineCallPush = findViewById(R.id.rl_switch_offline_call_push);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                onBackPressed();
            }
        });
        rlSwitchOfflineCallPush.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        rlSwitchOfflineCallPush.getSwitch().setChecked(PreferenceManager.getInstance().isPushCall());
    }



    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rl_switch_offline_call_push:
                break;
            default:
                break;
        }
    }

    private abstract class MyTextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
