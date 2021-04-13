package com.hyphenate.easeim.section.chat.views;

import android.content.Context;
import android.widget.TextView;

import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeim.R;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;

public class ChatRowVoiceCall extends EaseChatRow {
    private TextView contentView;

    public ChatRowVoiceCall(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(showSenderType ? R.layout.ease_row_sent_voice_call : R.layout.ease_row_received_voice_call, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(com.hyphenate.easeui.R.id.tv_chatcontent);
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        contentView.setText(txtBody.getMessage());
    }
}
