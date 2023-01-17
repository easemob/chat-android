package com.hyphenate.chatdemo.section.dialog;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.section.base.BaseActivity;

import java.util.Locale;

public class DemoAgreementDialogFragment extends DemoDialogFragment{
    @Override
    public int getMiddleLayoutId() {
        return R.layout.demo_fragment_middle_agreement;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        TextView tv_privacy = findViewById(R.id.tv_privacy);
        tv_privacy.setText(getSpannable());
        tv_privacy.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private SpannableString getSpannable() {
        String language = Locale.getDefault().getLanguage();
        boolean isZh = language.startsWith("zh");
        SpannableString spanStr = new SpannableString(getString(R.string.demo_login_dialog_content_privacy));
        int start1 = 18;
        int end1 = 25;
        int start2 = 30;
        int end2 = 44;
        if(isZh) {
            start1 = 5;
            end1 = 13;
            start2 = 14;
            end2 = 22;
        }
        //设置下划线
        //spanStr.setSpan(new UnderlineSpan(), 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spanStr.setSpan(new MyClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                jumpToAgreement();
            }
        }, start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new ForegroundColorSpan(Color.RED), start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //spanStr.setSpan(new UnderlineSpan(), 10, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new MyClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                jumpToProtocol();
            }
        }, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new ForegroundColorSpan(Color.RED), start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    private void jumpToAgreement() {
        Uri uri = Uri.parse("http://www.easemob.com/agreement");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    private void jumpToProtocol() {
        Uri uri = Uri.parse("http://www.easemob.com/protocol");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    private abstract class MyClickableSpan extends ClickableSpan {

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.bgColor = Color.TRANSPARENT;
        }
    }

    public static class Builder extends DemoDialogFragment.Builder {

        public Builder(BaseActivity context) {
            super(context);
        }

        @Override
        protected DemoDialogFragment getFragment() {
            return new DemoAgreementDialogFragment();
        }
    }
}
