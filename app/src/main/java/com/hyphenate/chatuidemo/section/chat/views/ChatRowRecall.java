package com.hyphenate.chatuidemo.section.chat.views;

import android.content.Context;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;

public class ChatRowRecall extends EaseChatRow {
    private TextView contentView;

    public ChatRowRecall(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(R.layout.demo_row_recall_message, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.text_content);
    }

    @Override
    protected void onSetUpView() {
        // 设置显示内容
        String messageStr = null;
        if (message.direct() == EMMessage.Direct.SEND) {
            messageStr = String.format(context.getString(R.string.msg_recall_by_self));
        } else {
            messageStr = String.format(context.getString(R.string.msg_recall_by_user), message.getFrom());
        }
        contentView.setText(messageStr);
    }
}
