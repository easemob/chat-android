package com.hyphenate.easeim.section.base;

import android.content.Intent;
import android.os.Bundle;

import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallFloatWindow;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.utils.EaseCallState;
import com.hyphenate.easeim.section.av.MultipleVideoActivity;
import com.hyphenate.easeim.section.av.VideoCallActivity;
import com.hyphenate.util.EMLog;

import androidx.annotation.Nullable;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public abstract class BaseInitActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                EMLog.d("getExtras", "获取附加字段start=======");
                for(String key : bundle.keySet()){
                    EMLog.d("getExtras", key + ":" + bundle.getString(key));
                }
                EMLog.d("getExtras", "获取附加字段end=======");
            }
        }

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
        if(EaseCallKit.getInstance().getCallState() != EaseCallState.CALL_IDLE && !EaseCallFloatWindow.getInstance(mContext).isShowing()){
            if(EaseCallKit.getInstance().getCallType() == EaseCallType.CONFERENCE_CALL){
                Intent intent = new Intent(mContext, MultipleVideoActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }else{
                Intent intent = new Intent(mContext, VideoCallActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }
    }
}
