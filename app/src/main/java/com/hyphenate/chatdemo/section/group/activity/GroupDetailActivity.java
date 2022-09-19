package com.hyphenate.chatdemo.section.group.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatdemo.DemoHelper;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.constant.DemoConstant;
import com.hyphenate.chatdemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatdemo.common.livedatas.LiveDataBus;
import com.hyphenate.chatdemo.common.widget.ArrowItemView;
import com.hyphenate.chatdemo.common.widget.SwitchItemView;
import com.hyphenate.chatdemo.section.base.BaseInitActivity;
import com.hyphenate.chatdemo.section.dialog.DemoDialogFragment;
import com.hyphenate.chatdemo.section.dialog.EditTextDialogFragment;
import com.hyphenate.chatdemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.chatdemo.section.group.GroupHelper;
import com.hyphenate.chatdemo.section.group.fragment.GroupEditFragment;
import com.hyphenate.chatdemo.section.group.viewmodels.GroupDetailViewModel;
import com.hyphenate.chatdemo.section.search.SearchGroupChatActivity;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.List;

public class GroupDetailActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener, SwitchItemView.OnCheckedChangeListener {
    private static final int REQUEST_CODE_ADD_USER = 0;
    private EaseTitleBar titleBar;
    private EaseImageView ivGroupAvatar;
    private TextView tvGroupName;
    private TextView tvGroupIntroduction;
    private TextView tvGroupMemberTitle;
    private TextView tvGroupMemberNum;
    private TextView tvGroupInvite;
    private ArrowItemView itemGroupName;
    private ArrowItemView itemGroupShareFile;
    private ArrowItemView itemGroupNotice;
    private ArrowItemView itemGroupIntroduction;
    private ArrowItemView itemGroupMemberManage;
    private ArrowItemView itemGroupHistory;
    private ArrowItemView itemGroupClearHistory;
    private SwitchItemView itemGroupNotDisturb;
    private SwitchItemView itemGroupOffPush;
    private SwitchItemView itemGroupTop;
    private TextView tvGroupRefund;
    private String groupId;
    private EMGroup group;
    private GroupDetailViewModel viewModel;
    private EMConversation conversation;

    public static void actionStart(Context context, String groupId) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_chat_group_detail;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        ivGroupAvatar = findViewById(R.id.iv_group_avatar);
        tvGroupName = findViewById(R.id.tv_group_name);
        tvGroupIntroduction = findViewById(R.id.tv_group_introduction);
        tvGroupMemberTitle = findViewById(R.id.tv_group_member_title);
        tvGroupMemberNum = findViewById(R.id.tv_group_member_num);
        tvGroupInvite = findViewById(R.id.tv_group_invite);
        itemGroupName = findViewById(R.id.item_group_name);
        itemGroupShareFile = findViewById(R.id.item_group_share_file);
        itemGroupNotice = findViewById(R.id.item_group_notice);
        itemGroupIntroduction = findViewById(R.id.item_group_introduction);
        itemGroupHistory = findViewById(R.id.item_group_history);
        itemGroupClearHistory = findViewById(R.id.item_group_clear_history);
        itemGroupNotDisturb = findViewById(R.id.item_group_not_disturb);
        itemGroupOffPush = findViewById(R.id.item_group_off_push);
        itemGroupTop = findViewById(R.id.item_group_top);
        tvGroupRefund = findViewById(R.id.tv_group_refund);
        itemGroupMemberManage = findViewById(R.id.item_group_member_manage);

        group = DemoHelper.getInstance().getGroupManager().getGroup(groupId);
        initGroupView();
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        tvGroupMemberTitle.setOnClickListener(this);
        tvGroupMemberNum.setOnClickListener(this);
        tvGroupInvite.setOnClickListener(this);
        itemGroupName.setOnClickListener(this);
        itemGroupShareFile.setOnClickListener(this);
        itemGroupNotice.setOnClickListener(this);
        itemGroupIntroduction.setOnClickListener(this);
        itemGroupHistory.setOnClickListener(this);
        itemGroupClearHistory.setOnClickListener(this);
        itemGroupNotDisturb.setOnCheckedChangeListener(this);
        itemGroupOffPush.setOnCheckedChangeListener(this);
        itemGroupTop.setOnCheckedChangeListener(this);
        tvGroupRefund.setOnClickListener(this);
        itemGroupMemberManage.setOnClickListener(this);
    }

    private void initGroupView() {
        if(group == null) {
            finish();
            return;
        }
        tvGroupName.setText(group.getGroupName());
        itemGroupName.getTvContent().setText(group.getGroupName());
        tvGroupMemberNum.setText(getString(R.string.em_chat_group_detail_member_num, group.getMemberCount()));
        tvGroupRefund.setText(getResources().getString(isOwner() ? R.string.em_chat_group_detail_dissolve : R.string.em_chat_group_detail_refund));
        tvGroupIntroduction.setText(group.getDescription());
        //itemGroupNotDisturb.getSwitch().setChecked(group.isMsgBlocked());
        conversation = DemoHelper.getInstance().getConversation(groupId, EMConversation.EMConversationType.GroupChat, true);
        String extField = conversation.getExtField();
        itemGroupTop.getSwitch().setChecked(!TextUtils.isEmpty(extField) && EaseCommonUtils.isTimestamp(extField));
        tvGroupInvite.setVisibility(group.getMemberCount() <= 0 ? View.VISIBLE : View.GONE);
        tvGroupInvite.setVisibility(isCanInvite() ? View.VISIBLE : View.GONE);
        //itemGroupNotDisturb.getSwitch().setChecked(group.isMsgBlocked());
        itemGroupMemberManage.setVisibility((isOwner() || isAdmin()) ? View.VISIBLE : View.GONE);

        itemGroupIntroduction.getTvContent().setText(group.getDescription());

        makeTextSingleLine(itemGroupNotice.getTvContent());
        makeTextSingleLine(itemGroupIntroduction.getTvContent());

        List<String> disabledIds = DemoHelper.getInstance().getPushManager().getNoPushGroups();
        itemGroupNotDisturb.getSwitch().setChecked(disabledIds != null && disabledIds.contains(groupId));
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(GroupDetailViewModel.class);
        viewModel.getGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMGroup>() {
                @Override
                public void onSuccess(EMGroup data) {
                    group = data;
                    initGroupView();
                }
            });
        });
        viewModel.getAnnouncementObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    itemGroupNotice.getTvContent().setText(data);
                }
            });
        });
        viewModel.getRefreshObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    loadGroup();
                }
            });
        });
        viewModel.getMessageChangeObservable().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event.isGroupLeave() && TextUtils.equals(groupId, event.message)) {
                finish();
                return;
            }
            if(event.isGroupChange()) {
                loadGroup();
            }
        });
        viewModel.getLeaveGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    finish();
                    LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_LEAVE, EaseEvent.TYPE.GROUP, groupId));
                }
            });
        });
        viewModel.blockGroupMessageObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    //itemGroupNotDisturb.getSwitch().setChecked(true);
                }
            });
        });
        viewModel.unblockGroupMessage().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    //itemGroupNotDisturb.getSwitch().setChecked(false);
                }
            });
        });
        viewModel.offPushObservable().observe(this, response -> {
            if(response) {
                loadGroup();
            }
        });
        viewModel.getClearHistoryObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(DemoConstant.CONVERSATION_DELETE).postValue(new EaseEvent(DemoConstant.CONTACT_DECLINE, EaseEvent.TYPE.MESSAGE));
                }
            });
        });
        loadGroup();
    }

    private void loadGroup() {
        viewModel.getGroup(groupId);
        viewModel.getGroupAnnouncement(groupId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_group_member_title :// 群成员
                GroupMemberTypeActivity.actionStart(mContext, groupId, isOwner());
                break;
            case R.id.tv_group_invite ://邀请群成员
                GroupPickContactsActivity.actionStartForResult(mContext, groupId, isOwner(), REQUEST_CODE_ADD_USER);
                break;
            case R.id.item_group_name ://群名称
                showGroupNameDialog();
                break;
            case R.id.item_group_share_file ://共享文件
                GroupSharedFilesActivity.actionStart(mContext, groupId);
                break;
            case R.id.item_group_notice ://群公告
                showAnnouncementDialog();
                break;
            case R.id.item_group_introduction ://群介绍
                showIntroductionDialog();
                break;
            case R.id.item_group_history ://查找聊天记录
                SearchGroupChatActivity.actionStart(mContext, groupId);
                break;
            case R.id.item_group_clear_history://清空聊天记录
                showClearConfirmDialog();
                break;
            case R.id.tv_group_refund ://退出群组
                showConfirmDialog();
                break;
            case R.id.item_group_member_manage://群组管理
                GroupManageIndexActivity.actionStart(mContext, groupId);
                break;
        }
    }

    private void showClearConfirmDialog() {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(R.string.em_chat_group_detail_clear_history_warning)
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        viewModel.clearHistory(groupId);
                    }
                })
                .showCancelButton(true)
                .show();
    }

    private void showConfirmDialog() {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(isOwner() ? R.string.em_chat_group_detail_dissolve : R.string.em_chat_group_detail_refund)
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        if(isOwner()) {
                            viewModel.destroyGroup(groupId);
                        }else {
                            viewModel.leaveGroup(groupId);
                        }
                    }
                })
                .showCancelButton(true)
                .show();
    }

    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.item_group_not_disturb ://消息免打扰
                viewModel.updatePushServiceForGroup(groupId, isChecked);
                /*if(isChecked) {
                    viewModel.blockGroupMessage(groupId);
                }else {
                    viewModel.unblockGroupMessage(groupId);
                }*/
                break;
            case R.id.item_group_off_push://屏蔽离线消息推送
                viewModel.updatePushServiceForGroup(groupId, isChecked);
                break;
            case R.id.item_group_top ://消息置顶
                if(isChecked) {
                    conversation.setExtField(System.currentTimeMillis()+"");
                }else {
                    conversation.setExtField("");
                }
                LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
                break;
        }
    }

    private void showGroupNameDialog() {
        new EditTextDialogFragment.Builder(mContext)
                .setContent(group.getGroupName())
                .setConfirmClickListener(new EditTextDialogFragment.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, String content) {
                        if(!TextUtils.isEmpty(content)) {
                            viewModel.setGroupName(groupId, content);
                        }
                    }
                })
                .setTitle(R.string.em_chat_group_detail_name)
                .show();
    }

    private void showAnnouncementDialog() {
        GroupEditFragment.showDialog(mContext,
                getString(R.string.em_chat_group_detail_announcement),
                group.getAnnouncement(),
                getString(R.string.em_chat_group_detail_announcement_hint),
                GroupHelper.isAdmin(group) || GroupHelper.isOwner(group),
                new GroupEditFragment.OnSaveClickListener() {
                    @Override
                    public void onSaveClick(View view, String content) {
                        //修改群公告
                        viewModel.setGroupAnnouncement(groupId, content);
                    }
                });
    }

    private void showIntroductionDialog() {
        GroupEditFragment.showDialog(mContext,
                getString(R.string.em_chat_group_detail_introduction),
                group.getDescription(),
                getString(R.string.em_chat_group_detail_introduction_hint),
                GroupHelper.isAdmin(group) || GroupHelper.isOwner(group),
                new GroupEditFragment.OnSaveClickListener() {
                    @Override
                    public void onSaveClick(View view, String content) {
                        //修改群介绍
                        viewModel.setGroupDescription(groupId, content);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER :
                    loadGroup();
                    break;
            }
        }
    }

    private void makeTextSingleLine(TextView tv) {
        tv.setMaxLines(1);
        tv.setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    /**
     * 是否有邀请权限
     * @return
     */
    private boolean isCanInvite() {
        return GroupHelper.isCanInvite(group);
    }

    /**
     * 是否是管理员
     * @return
     */
    private boolean isAdmin() {
        return GroupHelper.isAdmin(group);
    }

    /**
     * 是否是群主
     * @return
     */
    private boolean isOwner() {
        return GroupHelper.isOwner(group);
    }
}
