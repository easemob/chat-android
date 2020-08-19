package com.hyphenate.chatuidemo.common.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.hyphenate.chatuidemo.R;

import java.time.chrono.MinguoDate;

public class EditTextUtils {

    public static void changePwdDrawableRight(EditText editText, Drawable eyeOpen , Drawable eyeClose, Drawable left, Drawable top, Drawable bottom) {
        //标识密码是否能被看见
        final boolean[] canBeSeen = {false};
        editText.setOnTouchListener((v, event) -> {

            Drawable drawable = editText.getCompoundDrawables()[2];
            //如果右边没有图片，不再处理
            if (drawable == null)
                return false;
            //如果不是按下事件，不再处理
            if (event.getAction() != MotionEvent.ACTION_UP)
                return false;
            if (event.getX() > editText.getWidth()
                    - editText.getPaddingRight()
                    - drawable.getIntrinsicWidth())
            {

                if (canBeSeen[0])
                {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editText.setCompoundDrawablesWithIntrinsicBounds(left, top, eyeOpen, bottom);
                    canBeSeen[0] = false;
                } else
                {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

                    editText.setCompoundDrawablesWithIntrinsicBounds(left, top, eyeClose, bottom);
                    canBeSeen[0] = true;
                }
                editText.setSelection(editText.getText().toString().length());

                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();

                return true;
            }
            return false;
        });

    }

    public static void showRightDrawable(EditText editText, Drawable right) {
        String content = editText.getText().toString().trim();
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, TextUtils.isEmpty(content) ? null : right, null);
    }

    public static void clearEditTextListener(EditText editText) {
        editText.setOnTouchListener((v, event) -> {
            Drawable drawable = editText.getCompoundDrawables()[2];
            //如果右边没有图片，不再处理
            if (drawable == null)
                return false;
            //如果不是按下事件，不再处理
            if (event.getAction() != MotionEvent.ACTION_UP)
                return false;
            if (event.getX() > editText.getWidth()
                    - editText.getPaddingRight()
                    - drawable.getIntrinsicWidth()) {
                editText.setText("");
                return true;
            }
            return false;
        });
    }

    /**
     * 单行，根据关键字确定省略号的不同位置
     * @param textView
     * @param str
     * @param keyword
     * @param width
     * @return
     */
    public static String ellipsizeString(TextView textView, String str, String keyword, int width) {
        if(TextUtils.isEmpty(keyword)) {
            return str;
        }
        Paint paint = textView.getPaint();
        if(paint.measureText(str) < width) {
            return str;
        }
        int count = paint.breakText(str, 0, str.length(), true, width, null);
        int index = str.indexOf(keyword);
        //如果关键字在第一行,末尾显示省略号
        if(index + keyword.length() < count) {
            return str;
        }
        //如果关键字在最后，则起始位置显示省略号
        if(str.length() - index <= count - 3) {
            String end = str.substring(str.length() - count);
            end = "..." + end.substring(3);
            return end;
        }
        //如果是在中部的话，首尾显示省略号
        int subCount = (count - keyword.length()) / 2;
        String middle = str.substring(index - subCount, index + keyword.length() + subCount);
        middle = "..." + middle.substring(3);
        middle = middle.substring(0, middle.length() - 3) + "...";
        return middle;
    }

    public static SpannableStringBuilder highLightKeyword(Context context, String str, String keyword) {
        if(TextUtils.isEmpty(str) || TextUtils.isEmpty(keyword) || !str.contains(keyword)) {
            return null;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.em_color_brand)), str.indexOf(keyword), str.indexOf(keyword) + keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }
}
