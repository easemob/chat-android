package com.hyphenate.chatdemo.section.me.test.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.databinding.DemoFragmentTestDatabaseBinding;
import com.hyphenate.chatdemo.section.base.BaseInitFragment;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.util.EMLog;

import java.util.List;

public class TestDatabaseFragment extends BaseInitFragment implements View.OnClickListener, EMMessageListener {

    private com.hyphenate.chatdemo.databinding.DemoFragmentTestDatabaseBinding databaseBinding;

    @Override
    protected View getLayoutView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        databaseBinding = DemoFragmentTestDatabaseBinding.inflate(inflater);
        return databaseBinding.getRoot();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        databaseBinding.btnMessageCount.setOnClickListener(this);
    }

    @Override
    protected void initListener() {
        super.initListener();
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        getMessageCount();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_message_count :
                getMessageCount();
                break;
        }
    }

    private void getMessageCount() {
        EaseThreadManager.getInstance().runOnIOThread(()-> {
            int messageCount = EMClient.getInstance().chatManager().getMessageCount();
            if(messageCount > -1) {
                runOnUiThread(()-> {
                    databaseBinding.tvMessageCount.setText("消息总数："+messageCount);
                });
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mContext != null && mContext.isFinishing()) {
            EMClient.getInstance().chatManager().removeMessageListener(this);
        }
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.conversationId());
            if(conversation != null) {
                int allMsgCount = conversation.getAllMsgCount();
                EMLog.e("database", "conversationId: "+conversation.conversationId() + " message count: "+allMsgCount);
                int messageCount = EMClient.getInstance().chatManager().getMessageCount();
                EMLog.e("database", "All messages count: "+messageCount);
            }
        }
    }
}
