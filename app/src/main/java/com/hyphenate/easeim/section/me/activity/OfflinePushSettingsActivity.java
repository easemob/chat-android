package com.hyphenate.easeim.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.model.DemoModel;
import com.hyphenate.easeim.common.widget.ArrowItemView;
import com.hyphenate.easeim.common.widget.SwitchItemView;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.dialog.TimePickerDialogFragment;
import com.hyphenate.easeim.section.me.viewmodels.OfflinePushSetViewModel;
import com.hyphenate.easeui.widget.EaseTitleBar;

/**
 * Created by wei on 2016/12/6.
 */

public class OfflinePushSettingsActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener {
    EMPushConfigs mPushConfigs;
    DemoModel settingsModel;
    private EaseTitleBar titleBar;
    private SwitchItemView switchPushNoDisturb;
    private ArrowItemView itemPushTimeRange;
    private OfflinePushSetViewModel viewModel;
    private int startTime;
    private int endTime;
    private boolean shouldUpdateToServer;

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
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        switchPushNoDisturb.setOnClickListener(this);
        itemPushTimeRange.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        settingsModel = DemoHelper.getInstance().getModel();

        viewModel = new ViewModelProvider(this).get(OfflinePushSetViewModel.class);
        viewModel.getConfigsObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMPushConfigs>() {
                @Override
                public void onSuccess(EMPushConfigs data) {
                    mPushConfigs = data;
                    processPushConfigs();
                }
            });
        });

        viewModel.getDisableObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    itemPushTimeRange.getTvContent().setText(getTimeRange(startTime, endTime));
                    shouldUpdateToServer = false;
                }
            });
        });

        viewModel.getEnableObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {

                }
            });
        });

        viewModel.getPushConfigs();
    }

    private void processPushConfigs(){
        if(mPushConfigs == null)
            return;
        startTime = mPushConfigs.getNoDisturbStartHour();
        endTime = mPushConfigs.getNoDisturbEndHour();
        if(startTime < 0) {
            startTime = 0;
        }
        if(endTime < 0) {
            endTime = 0;
        }
        itemPushTimeRange.getTvContent().setText(getTimeRange(startTime, endTime));
        if(mPushConfigs.isNoDisturbOn()) {
            switchPushNoDisturb.getSwitch().setChecked(mPushConfigs.isNoDisturbOn());
            setOptionsVisible(true);
            if(shouldUpdateToServer) {
                viewModel.disableOfflinePush(startTime, endTime);
            }
        }
    }

    private String getTimeRange(int start, int end) {
        return getTimeToString(start) + "~" + getTimeToString(end);
    }

    private String getTimeToString(int hour) {
        return getDoubleDigit(hour) + ":00";
    }

    private String getDoubleDigit(int num) {
        return num > 10 ? String.valueOf(num) : "0" + num;
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_push_no_disturb :
                boolean checked = switchPushNoDisturb.getSwitch().isChecked();
                switchPushNoDisturb.getSwitch().setChecked(!checked);
                if(switchPushNoDisturb.getSwitch().isChecked()) {
                    viewModel.getPushConfigs();
                    shouldUpdateToServer = true;
                    setOptionsVisible(true);
                }else {
                    setOptionsVisible(false);
                    viewModel.enableOfflinePush();
                }
                break;
            case R.id.item_push_time_range :
                showTimePicker();
                break;
        }

    }

    private void setOptionsVisible(boolean visible) {
        itemPushTimeRange.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void showTimePicker() {
        new TimePickerDialogFragment.Builder(mContext)
                .setTitle(R.string.demo_no_disturb_time)
                .setConfirmColor(R.color.em_color_brand)
                .showCancelButton(true)
                .showMinute(false)
                .setStartTime(getTimeToString(startTime))
                .setEndTime(getTimeToString(endTime))
                .setOnTimePickCancelListener(R.string.cancel, new TimePickerDialogFragment.OnTimePickCancelListener() {
                        @Override
                        public void onClickCancel(View view) {

                        }
                    })
                .setOnTimePickSubmitListener(R.string.confirm, new TimePickerDialogFragment.OnTimePickSubmitListener() {
                        @Override
                        public void onClickSubmit(View view, String start, String end) {
                            try {
                                int startHour = Integer.parseInt(getHour(start));
                                int endHour = Integer.parseInt(getHour(end));
                                if(startHour != endHour) {
                                    startTime = startHour;
                                    endTime = endHour;
                                    viewModel.disableOfflinePush(startTime, endTime);
                                }else {
                                    showToast(R.string.offline_time_rang_error);
                                }

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
