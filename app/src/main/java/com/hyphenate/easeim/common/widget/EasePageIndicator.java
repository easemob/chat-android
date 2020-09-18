package com.hyphenate.easeim.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hyphenate.easeim.R;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.util.ArrayList;
import java.util.List;

public class EasePageIndicator extends LinearLayout {
    private List<View> indicators = new ArrayList<>();
    private int checkedPosition = 0;
    public EasePageIndicator(Context context) {
        super(context);
    }

    public EasePageIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EasePageIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setup(int num) {
        int count = num;
        if(num < 0 ) {
            count = 0;
        }
        int delta = count - indicators.size();
        if(delta > 0) {
            for(int i = 0; i < delta; i++) {
                indicators.add(createIndicator());
            }
        }else {
            for(int i = indicators.size() - 1; i >= indicators.size() + delta ; i--) {
                indicators.remove(i);
                removeViewAt(i);
            }
        }
    }

    public View createIndicator() {
        View indicator = new View(getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams((int)EaseCommonUtils.dip2px(getContext(), 16f), (int)EaseCommonUtils.dip2px(getContext(), 4f));
        indicator.setLayoutParams(lp);
        indicator.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.demo_indicator_selector));
        addView(indicator);
        if(indicator.getLayoutParams() instanceof MarginLayoutParams) {
            int margin = (int) EaseCommonUtils.dip2px(getContext(), 5f);
            ((MarginLayoutParams) indicator.getLayoutParams()).setMargins(margin, 0, margin, 0);
            indicator.requestLayout();
        }
        return indicator;
    }

    public void setItemChecked(int position) {
        if(position >= indicators.size() || position < 0) {
            return;
        }
        if(checkedPosition > -1 && checkedPosition < indicators.size()) {
            indicators.get(checkedPosition).setSelected(false);
        }
        checkedPosition = position;
        indicators.get(checkedPosition).setSelected(true);
    }
}
