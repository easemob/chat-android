package com.hyphenate.easeim.section.dialog;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.base.BaseActivity;
import com.hyphenate.easeim.section.base.BaseDialogFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.DensityUtil;

import java.lang.reflect.Field;
import java.util.Calendar;

public class TimePickerDialogFragment extends BaseDialogFragment {
    private TextView tvTitle;
    private TimePicker pickerStart;
    private TimePicker pickerEnd;
    private TextView tvDivider;
    private TextView btnCancel;
    private TextView btnSubmit;

    private String preStartTime, preEndTime;
    private String startTime;
    private String endTime;
    private String divider;
    private boolean showMinute;
    private String title;
    private int titleColor;
    private float titleSize;
    private boolean showCancel;
    private String confirmText;
    private OnTimePickSubmitListener listener;
    private OnTimePickCancelListener cancelClickListener;
    private int confirmColor;
    private String cancel;

    @Override
    public int getLayoutId() {
        return R.layout.demo_fragment_time_picker_dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            Window dialogWindow = getDialog().getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.dimAmount = 0.6f;
            lp.width = (int) (EaseCommonUtils.getScreenInfo(mContext)[0] - DensityUtil.dip2px(mContext, 20) * 2);
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.gravity =  Gravity.BOTTOM;
            lp.y = DensityUtil.dip2px(mContext, 10);
            setDialogParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tvTitle = findViewById(R.id.tv_title);
        pickerStart = findViewById(R.id.picker_start);
        tvDivider = findViewById(R.id.tv_divider);
        pickerEnd = findViewById(R.id.picker_end);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    @Override
    public void initListener() {
        super.initListener();
        pickerStart.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                startTime = getTime(hourOfDay, minute);
            }
        });

        pickerEnd.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                endTime = getTime(hourOfDay, minute);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cancelClickListener != null) {
                    cancelClickListener.onClickCancel(v);
                }
                dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onClickSubmit(v, startTime, endTime);
                }
                dismiss();
            }
        });
    }

    private String getTime(int hour, int minute) {
        String h = getDoubleDigit(hour);
        String m = getDoubleDigit(minute);
        return showMinute ?  h + ":" + m : h+":00";
    }

    private String getDoubleDigit(int time) {
        return time < 10 ? "0" + time : "" + time;
    }

    @Override
    public void initData() {
        super.initData();
        if(!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if(titleColor != 0) {
            tvTitle.setTextColor(titleColor);
        }
        if(titleSize != 0) {
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize);
        }
        if(!TextUtils.isEmpty(confirmText)) {
            btnSubmit.setText(confirmText);
        }
        if(confirmColor != 0) {
            btnSubmit.setTextColor(confirmColor);
        }
        if(!TextUtils.isEmpty(cancel)) {
            btnCancel.setText(cancel);
        }
        if(showCancel) {
            btnCancel.setVisibility(View.VISIBLE);
        }else {
            btnCancel.setVisibility(View.GONE);
        }

        pickerStart.setIs24HourView(true);
        pickerEnd.setIs24HourView(true);
        pickerStart.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        pickerEnd.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        setTimePickerDividerColor(pickerStart);
        setTimePickerDividerColor(pickerEnd);
        hideMinute(pickerStart);
        hideMinute(pickerEnd);

        Calendar calendar = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pickerStart.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            pickerStart.setMinute(calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            pickerEnd.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            pickerEnd.setMinute(calendar.get(Calendar.MINUTE));
        }else {
            pickerStart.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            pickerStart.setCurrentMinute(calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            pickerEnd.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            pickerEnd.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }

        if(!TextUtils.isEmpty(preStartTime) && !TextUtils.isEmpty(preEndTime)) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pickerStart.setHour(Integer.parseInt(preStartTime.substring(0, preStartTime.indexOf(":"))));
                pickerStart.setMinute(Integer.parseInt(preStartTime.substring(preStartTime.indexOf(":") + 1)));

                pickerEnd.setHour(Integer.parseInt(preEndTime.substring(0, preEndTime.indexOf(":"))));
                pickerEnd.setMinute(Integer.parseInt(preEndTime.substring(preEndTime.indexOf(":") + 1)));
            } else {
                pickerStart.setCurrentHour(Integer.parseInt(preStartTime.substring(0, preStartTime.indexOf(":"))));
                pickerStart.setCurrentMinute(Integer.parseInt(preStartTime.substring(preStartTime.indexOf(":") + 1)));

                pickerEnd.setCurrentHour(Integer.parseInt(preEndTime.substring(0, preEndTime.indexOf(":"))));
                pickerEnd.setCurrentMinute(Integer.parseInt(preEndTime.substring(preEndTime.indexOf(":") + 1)));
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startTime = getTime(pickerStart.getHour(), pickerStart.getMinute());
            endTime = getTime(pickerEnd.getHour(), pickerEnd.getMinute());
        }else {
            startTime = getTime(pickerStart.getCurrentHour(), pickerStart.getCurrentMinute());
            endTime = getTime(pickerEnd.getCurrentHour(), pickerEnd.getCurrentMinute());
        }
    }

    /**
     * 设置分割线的颜色
     * @param timePicker
     */
    private void setTimePickerDividerColor(TimePicker timePicker) {
        View child = timePicker.getChildAt(0);
        if(child instanceof LinearLayout) {
            LinearLayout llFirst = (LinearLayout) child;
            View subChild = llFirst.getChildAt(1);
            if(subChild instanceof LinearLayout) {
                LinearLayout mSpinners = (LinearLayout) subChild;
                for (int i = 0; i < mSpinners.getChildCount(); i++) {
                    if (mSpinners.getChildAt(i) instanceof NumberPicker) {
                        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
                        //setPickerMargin((NumberPicker) mSpinners.getChildAt(i));
                        for (Field pf : pickerFields) {
                            if (pf.getName().equals("mSelectionDivider")) {
                                pf.setAccessible(true);
                                try {
                                    pf.set(mSpinners.getChildAt(i), new ColorDrawable());
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (Resources.NotFoundException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void hideMinute(TimePicker timePicker) {
        Field[] declaredFields = timePicker.getClass().getDeclaredFields();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (Field datePickerField : declaredFields) {
                if ("mDelegate".equals(datePickerField.getName())) {
                    datePickerField.setAccessible(true);
                    Object dayPicker = new Object();
                    try{
                        dayPicker =datePickerField.get(timePicker);
                    } catch(IllegalAccessException e) {
                        e.printStackTrace();
                    } catch(IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    if(dayPicker == null) {
                        return;
                    }
                    Field[] fields = dayPicker.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        hideMinuteAndDivider(dayPicker, field);
                    }
                }
            }
        }else {
            for (Field datePickerField : declaredFields) {
                hideMinuteAndDivider(timePicker, datePickerField);
            }
        }

    }

    private void hideMinuteAndDivider(Object dayPicker, Field field) {
        if("mMinuteSpinner".equals(field.getName())) {
            field.setAccessible(true);
            Object minute = null;
            try {
                minute = field.get(dayPicker);
                ((View)minute).setVisibility(View.GONE);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if("mDivider".equals(field.getName())) {
            field.setAccessible(true);
            Object divider = null;
            try {
                divider = field.get(dayPicker);
                ((View)divider).setVisibility(View.GONE);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnTimePickCancelListener {
        void onClickCancel(View view);
    }

    public interface OnTimePickSubmitListener {
        void onClickSubmit(View view, String start, String end);
    }

    public static class Builder {
        public BaseActivity context;
        private String title;
        private int titleColor;
        private float titleSize;
        private boolean showCancel;
        private String confirmText;
        private OnTimePickSubmitListener listener;
        private OnTimePickCancelListener cancelClickListener;
        private int confirmColor;
        private String cancel;
        private boolean showMinute;
        private String divider;
        private String startTime;
        private String endTime;

        public Builder(BaseActivity context) {
            this.context = context;
        }

        public Builder setTitle(@StringRes int title) {
            this.title = context.getString(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitleColor(@ColorRes int color) {
            this.titleColor = ContextCompat.getColor(context, color);
            return this;
        }

        public Builder setTitleColorInt(@ColorInt int color) {
            this.titleColor = color;
            return this;
        }

        public Builder setTitleSize(float size) {
            this.titleSize = size;
            return this;
        }

        public Builder showCancelButton(boolean showCancel) {
            this.showCancel = showCancel;
            return this;
        }

        public Builder setOnTimePickSubmitListener(@StringRes int confirm, OnTimePickSubmitListener listener) {
            this.confirmText = context.getString(confirm);
            this.listener = listener;
            return this;
        }

        public Builder setOnTimePickSubmitListener(String confirm, OnTimePickSubmitListener listener) {
            this.confirmText = confirm;
            this.listener = listener;
            return this;
        }

        public Builder setOnTimePickSubmitListener(OnTimePickSubmitListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setConfirmColor(@ColorRes int color) {
            this.confirmColor = ContextCompat.getColor(context, color);
            return this;
        }

        public Builder setConfirmColorInt(@ColorInt int color) {
            this.confirmColor = color;
            return this;
        }

        public Builder setOnTimePickCancelListener(@StringRes int cancel, OnTimePickCancelListener listener) {
            this.cancel = context.getString(cancel);
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnTimePickCancelListener(String cancel, OnTimePickCancelListener listener) {
            this.cancel = cancel;
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnTimePickCancelListener(OnTimePickCancelListener listener) {
            this.cancelClickListener = listener;
            return this;
        }

        public Builder showMinute(boolean showMinute) {
            this.showMinute = showMinute;
            return this;
        }

        public Builder setDividerText(String text) {
            this.divider = text;
            return this;
        }

        public Builder setDividerText(@StringRes int text) {
            this.divider = context.getString(text);
            return this;
        }

        public Builder setStartTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setEndTime(String endTime) {
            this.endTime = endTime;
            return this;
        }

        public TimePickerDialogFragment build() {
            TimePickerDialogFragment fragment = getFragment();
            fragment.setTitle(title);
            fragment.setTitleColor(titleColor);
            fragment.setTitleSize(titleSize);
            fragment.showCancelButton(showCancel);
            fragment.setConfirmText(confirmText);
            fragment.setOnConfirmClickListener(this.listener);
            fragment.setConfirmColor(confirmColor);
            fragment.setCancelText(cancel);
            fragment.setOnCancelClickListener(cancelClickListener);
            fragment.showMinute(showMinute);
            fragment.setDividerText(divider);
            fragment.setStartTime(startTime);
            fragment.setEndTime(endTime);
            return fragment;
        }

        protected TimePickerDialogFragment getFragment() {
            return new TimePickerDialogFragment();
        }

        public TimePickerDialogFragment show() {
            TimePickerDialogFragment fragment = build();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragment.show(transaction, null);
            return fragment;
        }
    }

    private void setDividerText(String divider) {
        this.divider = divider;
    }

    private void setStartTime(String startTime) {
        this.preStartTime = startTime;
    }

    private void setEndTime(String endTime) {
        this.preEndTime = endTime;
    }

    private void showMinute(boolean showMinute) {
        this.showMinute = showMinute;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void setTitleSize(float titleSize) {
        this.titleSize = titleSize;
    }

    private void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    private void showCancelButton(boolean showCancel) {
        this.showCancel = showCancel;
    }

    private void setConfirmText(String confirmText) {
        this.confirmText = confirmText;
    }

    private void setOnConfirmClickListener(OnTimePickSubmitListener listener) {
        this.listener = listener;
    }

    private void setConfirmColor(int confirmColor) {
        this.confirmColor = confirmColor;
    }

    private void setCancelText(String cancel) {
        this.cancel = cancel;
    }

    private void setOnCancelClickListener(OnTimePickCancelListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
    }
}

