package com.hyphenate.chatdemo.common.utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class CountDownTimerUtils extends CountDownTimer {
    private TextView mTextView;

    public CountDownTimerUtils(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setClickable(false); //设置不可点击
        mTextView.setText(millisUntilFinished / 1000 + " s");  //设置倒计时时间
        // mTextView.setBackgroundResource(R.drawable.bg_identify_code_press); //设置按钮为灰色，这时是不能点击的
        SpannableString spannableString = new SpannableString(mTextView.getText().toString());  //获取按钮上的文字
        ForegroundColorSpan span = new ForegroundColorSpan(Color.GRAY);
        spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将倒计时的时间设置为灰色
        mTextView.setText(spannableString);
    }

    @Override
    public void onFinish() {
        mTextView.setText("获取验证码");
        mTextView.setTextColor(Color.parseColor("#feffffff"));
        mTextView.setClickable(true);//重新获得点击
        // mTextView.setBackgroundResource(R.drawable.bg_identify_code_normal);  //还原背景色
    }
}