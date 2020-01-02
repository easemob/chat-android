package com.hyphenate.easeui.ui;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class EaseBaseFragment extends Fragment {
    public Activity mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }

    /**
     * back
     */
    protected void onBackPress() {
        mContext.onBackPressed();
    }

}
