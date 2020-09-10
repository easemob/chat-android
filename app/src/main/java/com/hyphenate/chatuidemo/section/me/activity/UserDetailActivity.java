package com.hyphenate.chatuidemo.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.constant.DemoConstant;
import com.hyphenate.chatuidemo.common.livedatas.LiveDataBus;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;

public class UserDetailActivity extends BaseInitActivity {
    private EaseTitleBar titleBar;
    private ArrowItemView itemHxId;
    private ArrowItemView itemNickname;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UserDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_user_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemHxId = findViewById(R.id.item_hx_id);
        itemNickname = findViewById(R.id.item_nickname);
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
        itemNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfflinePushNickActivity.actionStart(mContext);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        itemHxId.getTvContent().setText(DemoHelper.getInstance().getCurrentUser());
        getNickname();
        LiveDataBus.get().with(DemoConstant.REFRESH_NICKNAME, Boolean.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event) {
                getNickname();
            }
        });
    }

    private void getNickname() {
        EMPushConfigs configs = null;
        try {
            configs = EMClient.getInstance().pushManager().getPushConfigsFromServer();
            String nickname = configs.getDisplayNickname();
            if(!TextUtils.isEmpty(nickname)) {
                itemNickname.getTvContent().setText(nickname);
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }
}
