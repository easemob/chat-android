package com.hyphenate.chatuidemo.section.me.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.model.DemoModel;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.common.widget.SwitchItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.dialog.TimePickerDialogFragment;
import com.hyphenate.chatuidemo.section.me.viewmodels.OfflinePushSetViewModel;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by wei on 2016/12/6.
 */

public class OfflinePushSettingsActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, SwitchItemView.OnCheckedChangeListener, View.OnClickListener {
    EMPushConfigs mPushConfigs;
    DemoModel settingsModel;
    private EaseTitleBar titleBar;
    private SwitchItemView rlCustomServer;
    private SwitchItemView switchPushNoDisturb;
    private ArrowItemView itemPushTimeRange;
    private OfflinePushSetViewModel viewModel;
    private int startTime;
    private int endTime;

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
        switchPushNoDisturb = findViewById(R.id.switch_push_no_disturb);
        itemPushTimeRange = findViewById(R.id.item_push_time_range);
        rlCustomServer = findViewById(R.id.rl_custom_server);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        switchPushNoDisturb.setOnCheckedChangeListener(this);
        rlCustomServer.setOnCheckedChangeListener(this);
        itemPushTimeRange.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        settingsModel = DemoHelper.getInstance().getModel();
        rlCustomServer.getSwitch().setChecked(settingsModel.isUseFCM());

        viewModel = new ViewModelProvider(this).get(OfflinePushSetViewModel.class);
        viewModel.getConfigsObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMPushConfigs>() {
                @Override
                public void onSuccess(EMPushConfigs data) {
                    mPushConfigs = data;
                    processPushConfigs();
                }

                @Override
                public void onLoading() {
                    super.onLoading();
                    showLoading();
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    dismissLoading();
                }
            });
        });

        viewModel.getDisableObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    itemPushTimeRange.getTvContent().setText(getTimeRange(startTime, endTime));
                }

                @Override
                public void onLoading() {
                    super.onLoading();
                    showLoading();
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    dismissLoading();
                }
            });
        });

        viewModel.getEnableObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {

                }

                @Override
                public void onLoading() {
                    super.onLoading();
                    showLoading();
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    dismissLoading();
                }
            });
        });

        viewModel.getPushConfigs();
    }

    private void processPushConfigs(){
        if(mPushConfigs == null)
            return;
        switchPushNoDisturb.getSwitch().setChecked(mPushConfigs.isNoDisturbOn());
        if(mPushConfigs.isNoDisturbOn()) {
            startTime = mPushConfigs.getNoDisturbStartHour();
            endTime = mPushConfigs.getNoDisturbEndHour();
            itemPushTimeRange.getTvContent().setText(getTimeRange(startTime, endTime));
        }
    }

    private String getTimeRange(int start, int end) {
        return getDoubleDigit(start) + ":00" + "~" + getDoubleDigit(end) + ":00";
    }

    private String getDoubleDigit(int num) {
        return num > 10 ? String.valueOf(num) : "0" + num;
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_push_no_disturb :
                if(isChecked) {
                    viewModel.disableOfflinePush(startTime, endTime);
                    itemPushTimeRange.setVisibility(View.VISIBLE);
                    rlCustomServer.setVisibility(View.VISIBLE);
                }else {
                    itemPushTimeRange.setVisibility(View.GONE);
                    rlCustomServer.setVisibility(View.GONE);
                    viewModel.enableOfflinePush();
                }
                break;
            case R.id.rl_custom_server :
                settingsModel.setUseFCM(isChecked);
                EMClient.getInstance().getOptions().setUseFCM(isChecked);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        showTimePicker();
    }

    private void showTimePicker() {
        new TimePickerDialogFragment.Builder(mContext)
                .setTitle(R.string.demo_no_disturb_time)
                .setConfirmColor(R.color.em_color_brand)
                .showCancelButton(true)
                .showMinute(false)
                .setOnTimePickCancelListener(R.string.cancel, new TimePickerDialogFragment.OnTimePickCancelListener() {
                        @Override
                        public void onClickCancel(View view) {

                        }
                    })
                .setOnTimePickSubmitListener(R.string.confirm, new TimePickerDialogFragment.OnTimePickSubmitListener() {
                        @Override
                        public void onClickSubmit(View view, String start, String end) {
                            try {
                                startTime = Integer.parseInt(getHour(start));
                                endTime = Integer.parseInt(getHour(end));
                                viewModel.disableOfflinePush(startTime, endTime);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                .show();
    }

    private String getHour(String time) {
        return time.contains(":") ? time.substring(0, time.indexOf(":")) : time;
    }
}
