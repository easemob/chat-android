package com.hyphenate.chatdemo.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.widget.ArrowItemView;
import com.hyphenate.chatdemo.section.base.BaseInitActivity;
import com.hyphenate.chatdemo.section.contact.activity.ContactBlackListActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class PrivacyIndexActivity extends BaseInitActivity implements View.OnClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemBlackManager;
    private ArrowItemView itemEquipmentManager;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, PrivacyIndexActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_privacy_index;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemBlackManager = findViewById(R.id.item_black_manager);
        itemEquipmentManager = findViewById(R.id.item_equipment_manager);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemBlackManager.setOnClickListener(this);
        itemEquipmentManager.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_black_manager :
                ContactBlackListActivity.actionStart(mContext);
                break;
            case R.id.item_equipment_manager :

                break;
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
