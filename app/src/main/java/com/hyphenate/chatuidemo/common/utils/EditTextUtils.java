package com.hyphenate.chatuidemo.common.utils;

import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.EditText;

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
}
