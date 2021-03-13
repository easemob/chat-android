package com.hyphenate.easeim.section.base;

import android.content.Intent;
import android.os.Bundle;

import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallFloatWindow;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.ui.EaseMultipleVideoActivity;
import com.hyphenate.easecallkit.ui.EaseVideoCallActivity;
import com.hyphenate.easecallkit.utils.EaseCallState;

import androidx.annotation.Nullable;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public abstract class BaseInitActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initSystemFit();
        initIntent(getIntent());
        initView(savedInstanceState);
        initListener();
        initData();
    }

    protected void initSystemFit() {
        setFitSystemForTheme(true);
    }

    /**
     * get layout id
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * init intent
     * @param intent
     */
    protected void initIntent(Intent intent) { }

    /**
     * init view
     * @param savedInstanceState
     */
    protected void initView(Bundle savedInstanceState) {

    }

    /**
     * init listener
     */
    protected void initListener() { }

    /**
     * init data
     */
    protected void initData() { }


    @Override
    protected void onRestart(){
        super.onRestart();
        if(EaseCallKit.getInstance().getCallState() != EaseCallState.CALL_IDLE&& !EaseCallFloatWindow.getInstance(getApplicationContext()).isShowing()){
            if(EaseCallKit.getInstance().getCallType() == EaseCallType.CONFERENCE_CALL){
                Intent intent = new Intent(getApplicationContext(), EaseMultipleVideoActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }else{
                Intent intent = new Intent(getApplicationContext(), EaseVideoCallActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        }
    }
}
