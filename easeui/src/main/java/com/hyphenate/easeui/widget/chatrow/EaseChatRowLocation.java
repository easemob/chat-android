package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;

/**
 * location row
 */
public class EaseChatRowLocation extends EaseChatRow {
    private TextView locationView;
    private TextView tvLocationName;
    private EMLocationMessageBody locBody;

    public EaseChatRowLocation(Context context, boolean isSender) {
        super(context, isSender);
    }

    public EaseChatRowLocation(Context context, EMMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.ease_row_received_location
                : R.layout.ease_row_sent_location, this);
    }

    @Override
    protected void onFindViewById() {
    	locationView = (TextView) findViewById(R.id.tv_location);
    	tvLocationName = findViewById(R.id.tv_location_name);
    }

    @Override
    protected void onSetUpView() {
		locBody = (EMLocationMessageBody) message.getBody();
		locationView.setText(locBody.getAddress());
    }

    @Override
    protected void onMessageCreate() {
        progressBar.setVisibility(View.VISIBLE);
        statusView.setVisibility(View.GONE);
    }

    @Override
    protected void onMessageSuccess() {
        progressBar.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
    }

    @Override
    protected void onMessageError() {
        progressBar.setVisibility(View.GONE);
        statusView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onMessageInProgress() {
        progressBar.setVisibility(View.VISIBLE);
        statusView.setVisibility(View.GONE);
    }

}
