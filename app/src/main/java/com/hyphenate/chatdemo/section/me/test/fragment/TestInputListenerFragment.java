package com.hyphenate.chatdemo.section.me.test.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatdemo.databinding.DemoFragmentTestInputListenerBinding;
import com.hyphenate.chatdemo.section.base.BaseInitFragment;

import java.util.List;

public class TestInputListenerFragment extends BaseInitFragment implements EMMessageListener {

    private DemoFragmentTestInputListenerBinding viewBinding;
    private static final String MSG_TYPING_BEGIN = "TypingBegin";
    private static final int TYPING_SHOW_TIME = 10000;
    private Handler typingHandler;
    private long previousChangedTimeStamp;
    private static final int MSG_TYPING_END = 1;
    private String currentConversationId;
    private String toChatUsername = "ljn";

    @Override
    protected View getLayoutView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        viewBinding = DemoFragmentTestInputListenerBinding.inflate(inflater);
        return viewBinding.getRoot();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        currentConversationId = viewBinding.etInputUsername.getText().toString().trim();
    }

    @Override
    protected void initListener() {
        super.initListener();
        EMClient.getInstance().chatManager().addMessageListener(this);
        viewBinding.etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(viewBinding.etInputUsername.getText().toString().trim())) {
                    Toast.makeText(mContext, "Please input a username", Toast.LENGTH_SHORT).show();
                    return;
                }
                textChange();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        viewBinding.etInputUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)) {
                    currentConversationId = s.toString().trim();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        initTypingHandler();
    }

    private void initTypingHandler() {
        typingHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MSG_TYPING_END :
                        cancelTimer();
                        break;
                }
            }
        };
    }

    private void cancelTimer() {
        // 这里需更新 UI，不再显示“对方正在输入”
        runOnUiThread(()-> viewBinding.tvStatus.setText(""));
        if(typingHandler != null) {
            typingHandler.removeCallbacksAndMessages(null);
        }
    }

    private void textChange() {
        long currentTimestamp = System.currentTimeMillis();
        if(currentTimestamp - previousChangedTimeStamp > 5) {
            sendBeginTyping();
            previousChangedTimeStamp = currentTimestamp;
        }
    }

    private void sendBeginTyping() {
        EMMessage beginMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        EMCmdMessageBody body = new EMCmdMessageBody(MSG_TYPING_BEGIN);
        // Only deliver this cmd msg to online users
        body.deliverOnlineOnly(true);
        beginMsg.addBody(body);
        beginMsg.setTo(toChatUsername);
        EMClient.getInstance().chatManager().sendMessage(beginMsg);
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {

    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        for (EMMessage msg : messages) {
            if(!TextUtils.equals(msg.conversationId(), currentConversationId)) {
                return;
            }
            EMCmdMessageBody body = (EMCmdMessageBody) msg.getBody();
            if(TextUtils.equals(body.action(), MSG_TYPING_BEGIN)) {
                // 这里需更新 UI，显示“对方正在输入”
                runOnUiThread(()-> viewBinding.tvStatus.setText("对方正在输入"));
                beginTimer();
            }
        }
    }

    private void beginTimer() {
        if(typingHandler != null) {
            typingHandler.removeMessages(MSG_TYPING_END);
            typingHandler.sendEmptyMessageDelayed(MSG_TYPING_END, TYPING_SHOW_TIME);
        }
    }
}
