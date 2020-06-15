package com.hyphenate.chatuidemo.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.model.DemoModel;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.common.widget.SwitchItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class CommonSettingsActivity extends BaseInitActivity implements View.OnClickListener, SwitchItemView.OnCheckedChangeListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemNotification;
    private ArrowItemView itemCallOption;
    private SwitchItemView itemTyping;
    private SwitchItemView itemSwitchSpeaker;
    private SwitchItemView itemChatroom;
    private SwitchItemView itemDeleteMsg;
    private SwitchItemView itemAutoFile;
    private SwitchItemView itemAutoDownload;
    private SwitchItemView itemAutoAcceptGroup;

    private DemoModel settingsModel;
    private EMOptions chatOptions;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CommonSettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_common_settings;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemNotification = findViewById(R.id.item_notification);
        itemCallOption = findViewById(R.id.item_call_option);
        itemTyping = findViewById(R.id.item_switch_typing);
        itemSwitchSpeaker = findViewById(R.id.item_switch_speaker);
        itemChatroom = findViewById(R.id.item_switch_chatroom);
        itemDeleteMsg = findViewById(R.id.item_switch_delete_msg);
        itemAutoFile = findViewById(R.id.item_switch_auto_file);
        itemAutoDownload = findViewById(R.id.item_switch_auto_download);
        itemAutoAcceptGroup = findViewById(R.id.item_switch_auto_accept_group);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemNotification.setOnClickListener(this);
        itemCallOption.setOnClickListener(this);
        itemTyping.setOnCheckedChangeListener(this);
        itemSwitchSpeaker.setOnCheckedChangeListener(this);
        itemChatroom.setOnCheckedChangeListener(this);
        itemDeleteMsg.setOnCheckedChangeListener(this);
        itemAutoFile.setOnCheckedChangeListener(this);
        itemAutoDownload.setOnCheckedChangeListener(this);
        itemAutoAcceptGroup.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        settingsModel = DemoHelper.getInstance().getModel();
        chatOptions = EMClient.getInstance().getOptions();

        itemTyping.getSwitch().setChecked(settingsModel.isShowMsgTyping());
        itemSwitchSpeaker.getSwitch().setChecked(settingsModel.getSettingMsgSpeaker());
        itemChatroom.getSwitch().setChecked(settingsModel.isChatroomOwnerLeaveAllowed());
        itemDeleteMsg.getSwitch().setChecked(settingsModel.isDeleteMessagesAsExitGroup());
        itemAutoFile.getSwitch().setChecked(settingsModel.isSetTransferFileByUser());
        itemAutoDownload.getSwitch().setChecked(settingsModel.isSetAutodownloadThumbnail());
        itemAutoAcceptGroup.getSwitch().setChecked(settingsModel.isAutoAcceptGroupInvitation());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_notification :
                OfflinePushSettingsActivity.actionStart(mContext);
                break;
            case R.id.item_call_option :
                CallOptionActivity.actionStart(mContext);
                break;
        }

    }

    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.item_switch_typing :
                settingsModel.showMsgTyping(isChecked);
                break;
            case R.id.item_switch_speaker :
                settingsModel.setSettingMsgSpeaker(isChecked);
                break;
            case R.id.item_switch_chatroom :
                settingsModel.allowChatroomOwnerLeave(isChecked);
                chatOptions.allowChatroomOwnerLeave(isChecked);
                break;
            case R.id.item_switch_delete_msg :
                settingsModel.setDeleteMessagesAsExitGroup(isChecked);
                chatOptions.setDeleteMessagesAsExitGroup(isChecked);
                break;
            case R.id.item_switch_auto_file :
                settingsModel.setTransfeFileByUser(isChecked);
                chatOptions.setAutoTransferMessageAttachments(isChecked);
                break;
            case R.id.item_switch_auto_download :
                settingsModel.setAutodownloadThumbnail(isChecked);
                chatOptions.setAutoDownloadThumbnail(isChecked);
                break;
            case R.id.item_switch_auto_accept_group :
                settingsModel.setAutoAcceptGroupInvitation(isChecked);
                chatOptions.setAutoAcceptGroupInvitation(isChecked);
                break;
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
