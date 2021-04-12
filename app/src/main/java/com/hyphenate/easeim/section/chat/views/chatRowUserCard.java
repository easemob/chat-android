package com.hyphenate.easeim.section.chat.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class chatRowUserCard extends EaseChatRow {
    private TextView nickNameView;
    private TextView userIdView;
    private ImageView headImageView;

    public chatRowUserCard(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(showSenderType ? R.layout.ease_row_sent_user_card : R.layout.ease_row_received_user_card, this);
    }

    @Override
    protected void onFindViewById() {
        nickNameView = (TextView) findViewById(R.id.user_nick_name);
        userIdView = (TextView) findViewById(R.id.user_id);
        headImageView = (ImageView)findViewById(R.id.head_Image_view);
    }

    @Override
    protected void onSetUpView() {
        EMCustomMessageBody messageBody = (EMCustomMessageBody) message.getBody();
        Map<String,String> params = messageBody.getParams();
        String uId = params.get(DemoConstant.USER_CARD_ID);
        userIdView.setText(uId);
        String nickName = params.get(DemoConstant.USER_CARD_NICK);
        nickNameView.setText(nickName);
        String headUrl = params.get(DemoConstant.USER_CARD_AVATAR);
        Glide.with(getContext()).load(headUrl).placeholder(R.drawable.em_login_logo).error(R.drawable.em_login_logo).into(headImageView);
    }
}

