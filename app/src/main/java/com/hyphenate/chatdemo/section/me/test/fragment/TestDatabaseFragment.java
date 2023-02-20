package com.hyphenate.chatdemo.section.me.test.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.hyphenate.chatdemo.common.livedatas.LiveDataBus;
import com.hyphenate.chatdemo.databinding.DemoFragmentTestDatabaseBinding;
import com.hyphenate.chatdemo.section.base.BaseInitFragment;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestDatabaseFragment extends BaseInitFragment implements View.OnClickListener, EMMessageListener {

    private com.hyphenate.chatdemo.databinding.DemoFragmentTestDatabaseBinding databaseBinding;
    private int timeSpent;

    @Override
    protected View getLayoutView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        databaseBinding = DemoFragmentTestDatabaseBinding.inflate(inflater);
        return databaseBinding.getRoot();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        databaseBinding.btnMessageCount.setOnClickListener(this);
        databaseBinding.btnRemoveMessages.setOnClickListener(this);
        databaseBinding.btnGenerateMessage.setOnClickListener(this);
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
            case R.id.btn_remove_messages:
                deleteMessages();
                break;
            case R.id.btn_generate_message:
                generateMessages();
                break;
        }
    }

    private void deleteMessages() {
        String count = databaseBinding.etMessageCount.getText().toString().trim();
        Integer removeCount = 0;
        try {
            removeCount = Integer.valueOf(count);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if(removeCount == 0) {
            showToast("Please input a count you want to remove");
            return;
        }
        Integer finalRemoveCount = removeCount;
        showToast("Start to delete messages");
        EaseThreadManager.getInstance().runOnIOThread(()-> {
            long startTimestamp = System.currentTimeMillis();
            int messageCount = EMClient.getInstance().chatManager().getMessageCount();
            boolean removeMessagesExceedCount = EMClient.getInstance().chatManager().removeMessagesExceedCount(finalRemoveCount);
            if(removeMessagesExceedCount) {
                long endTimestamp = System.currentTimeMillis();
                EMLog.e("deleteMessages", "removeMessagesExceedCount remove messages count: "+(messageCount - finalRemoveCount)+" cost time: "+(endTimestamp - startTimestamp) + " ms");
                showToast("Delete Successful");
                Iterator<Map.Entry<String, EMConversation>> iterator = EMClient.getInstance().chatManager().getAllConversations().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, EMConversation> next = iterator.next();
                    next.getValue().clear();
                }
                messageCount = EMClient.getInstance().chatManager().getMessageCount();
                EMLog.e("tag", "剩余的消息总数："+messageCount);
            }
        });
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
        getMessageCount();
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

    private void generateMessages() {
        String conversationId = databaseBinding.etConversationId.getText().toString().trim();
        if(TextUtils.isEmpty(conversationId)) {
            showToast("请输入会话id");
            return;
        }
        generateConversationMessages(conversationId);
    }

    private void generateConversationMessages(String conversationId) {
        showToast("开始生成消息，会话id："+conversationId);
        EaseThreadManager.getInstance().runOnIOThread(()-> {
            long currentTimeMillis = System.currentTimeMillis();
            EMMessage message = null;
            List<EMMessage> msgList = new ArrayList<>();
            EMClient.getInstance().chatManager().getConversation(conversationId, EMConversation.EMConversationType.Chat, true);
            for(int i = 0; i < 10000; i++) {
                message = EMMessage.createTextSendMessage(currentTimeMillis + ": "+(i+1), conversationId);
                message.setStatus(EMMessage.Status.SUCCESS);
                msgList.add(message);
            }
            EMClient.getInstance().chatManager().importMessages(msgList);
            EaseThreadManager.getInstance().runOnMainThread(()-> {
                showToast("生成消息完成："+conversationId);
            });
        });

    }
}
