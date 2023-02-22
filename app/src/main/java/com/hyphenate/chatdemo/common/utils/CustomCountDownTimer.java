package com.hyphenate.chatdemo.common.utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.hyphenate.chatdemo.R;

public class CustomCountDownTimer extends CountDownTimer {
    private TextView mTextView;

    public CustomCountDownTimer(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setClickable(false); //设置不可点击
        mTextView.setText(mTextView.getContext().getString(R.string.em_login_get_code_again_time, (int)(millisUntilFinished / 1000)));
        mTextView.setTextColor(ContextCompat.getColor(mTextView.getContext(), R.color.em_color_common_text_gray)); //设置按钮为灰色，这时是不能点击的
    }

    @Override
    public void onFinish() {
        mTextView.setText(mTextView.getContext().getString(R.string.em_login_get_code_again));
        mTextView.setTextColor(Color.parseColor("#fe009FFF"));
        mTextView.setClickable(true);//重新获得点击
        // mTextView.setBackgroundResource(R.drawable.bg_identify_code_normal);  //还原背景色
    }
}