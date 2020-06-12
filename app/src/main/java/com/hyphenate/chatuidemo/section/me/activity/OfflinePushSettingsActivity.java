package com.hyphenate.chatuidemo.section.me.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.model.DemoModel;
import com.hyphenate.chatuidemo.common.widget.SwitchItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by wei on 2016/12/6.
 */

public class OfflinePushSettingsActivity extends BaseInitActivity implements CompoundButton.OnCheckedChangeListener, EaseTitleBar.OnRightClickListener, EaseTitleBar.OnBackPressListener, SwitchItemView.OnCheckedChangeListener {
    private CheckBox noDisturbOn, noDisturbOff, noDisturbInNight;
    private Status status = Status.OFF;

    EMPushConfigs mPushConfigs;
    DemoModel settingsModel;
    private EaseTitleBar titleBar;
    private SwitchItemView rlCustomServer;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, OfflinePushSettingsActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_offline_push_settings;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        noDisturbOn = (CheckBox) findViewById(R.id.cb_no_disturb_on);
        noDisturbOff = (CheckBox) findViewById(R.id.cb_no_disturb_off);
        noDisturbInNight = (CheckBox) findViewById(R.id.cb_no_disturb_only_night);
        rlCustomServer = findViewById(R.id.rl_custom_server);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
        noDisturbOn.setOnCheckedChangeListener(this);
        noDisturbOff.setOnCheckedChangeListener(this);
        noDisturbInNight.setOnCheckedChangeListener(this);
        rlCustomServer.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        settingsModel = DemoHelper.getInstance().getModel();
        rlCustomServer.getSwitch().setChecked(settingsModel.isUseFCM());

        loadSettings();
    }

    private void loadSettings() {
        mPushConfigs = EMClient.getInstance().pushManager().getPushConfigs();
        if(mPushConfigs == null){
            final ProgressDialog loadingPd = new ProgressDialog(this);
            loadingPd.setMessage("loading");
            loadingPd.setCanceledOnTouchOutside(false);
            loadingPd.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mPushConfigs = EMClient.getInstance().pushManager().getPushConfigsFromServer();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingPd.dismiss();
                                processPushConfigs();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingPd.dismiss();
                                Toast.makeText(OfflinePushSettingsActivity.this, "loading failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }else{
            processPushConfigs();
        }
    }

    private void saveSettings() {
        final ProgressDialog savingPd = new ProgressDialog(OfflinePushSettingsActivity.this);
        savingPd.setMessage(getString(R.string.push_saving_settings));
        savingPd.setCanceledOnTouchOutside(false);
        savingPd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(status == Status.ON){
                        EMClient.getInstance().pushManager().disableOfflinePush(0, 24);
                    }else if(status == Status.OFF){
                        EMClient.getInstance().pushManager().enableOfflinePush();
                    }else{
                        EMClient.getInstance().pushManager().disableOfflinePush(22, 7);
                    }
                    finish();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            savingPd.dismiss();
                            Toast.makeText(OfflinePushSettingsActivity.this, R.string.push_save_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_no_disturb_on:
                if(isChecked){
                    noDisturbOff.setChecked(false);
                    noDisturbInNight.setChecked(false);
                    status = Status.ON;
                }
                break;
            case R.id.cb_no_disturb_off:
                if(isChecked){
                    noDisturbOn.setChecked(false);
                    noDisturbInNight.setChecked(false);
                    status = Status.OFF;
                }
                break;
            case R.id.cb_no_disturb_only_night:
                if(isChecked){
                    noDisturbOn.setChecked(false);
                    noDisturbOff.setChecked(false);
                    status = Status.ON_IN_NIGHT;
                }
                break;
        }
    }

    private void processPushConfigs(){
        if(mPushConfigs == null)
            return;
        if(mPushConfigs.isNoDisturbOn()){
            status = status.ON;
            noDisturbOn.setChecked(true);
            if(mPushConfigs.getNoDisturbStartHour() > 0){
                status = Status.ON_IN_NIGHT;
                noDisturbInNight.setChecked(true);
            }
        }else{
            status = Status.OFF;
            noDisturbOff.setChecked(true);
        }
    }

    @Override
    public void onRightClick(View view) {
        saveSettings();
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        if (isChecked) {
            settingsModel.setUseFCM(false);
            EMClient.getInstance().getOptions().setUseFCM(false);
        } else {
            settingsModel.setUseFCM(true);
            EMClient.getInstance().getOptions().setUseFCM(true);
        }
    }

    private enum Status {
        ON,
        OFF,
        ON_IN_NIGHT
    }

}
