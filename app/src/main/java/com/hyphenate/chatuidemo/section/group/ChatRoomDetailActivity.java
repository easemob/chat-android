package com.hyphenate.chatuidemo.section.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.livedatas.LiveDataBus;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.dialog.DemoDialogFragment;
import com.hyphenate.chatuidemo.section.dialog.EditTextDialogFragment;
import com.hyphenate.chatuidemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.chatuidemo.section.group.viewmodels.ChatRoomDetailViewModel;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseTitleBar;

import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class ChatRoomDetailActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener {
    private String roomId;
    private EaseTitleBar titleBar;
    private ArrowItemView itemChatRoomId;
    private ArrowItemView itemChatRoomName;
    private ArrowItemView itemChatRoomDescription;
    private ArrowItemView itemChatRoomOwner;
    private ArrowItemView itemChatRoomMembers;
    private ArrowItemView itemChatRoomAdmins;
    private TextView tvChatRoomRefund;
    private ChatRoomDetailViewModel viewModel;
    private EMChatRoom chatRoom;

    public static void actionStart(Context context, String roomId) {
        Intent intent = new Intent(context, ChatRoomDetailActivity.class);
        intent.putExtra("roomId", roomId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_chat_chat_room_detail;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        roomId = intent.getStringExtra("roomId");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemChatRoomId = findViewById(R.id.item_chat_room_id);
        itemChatRoomName = findViewById(R.id.item_chat_room_name);
        itemChatRoomDescription = findViewById(R.id.item_chat_room_description);
        itemChatRoomOwner = findViewById(R.id.item_chat_room_owner);
        itemChatRoomMembers = findViewById(R.id.item_chat_room_members);
        itemChatRoomAdmins = findViewById(R.id.item_chat_room_admins);
        tvChatRoomRefund = findViewById(R.id.tv_chat_room_refund);

        chatRoom = DemoHelper.getInstance().getChatroomManager().getChatRoom(roomId);

        updateContent(chatRoom);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemChatRoomName.setOnClickListener(this);
        itemChatRoomDescription.setOnClickListener(this);
        itemChatRoomMembers.setOnClickListener(this);
        itemChatRoomAdmins.setOnClickListener(this);
        tvChatRoomRefund.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(ChatRoomDetailViewModel.class);
        viewModel.chatRoomObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMChatRoom>() {
                @Override
                public void onSuccess(EMChatRoom data) {
                    chatRoom = data;
                    updateContent(data);
                }
            });
        });

        viewModel.updateAnnouncementObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(String data) {

                }
            });
        });

        viewModel.destroyGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    EaseEvent easeEvent = new EaseEvent(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.TYPE.CHAT_ROOM_LEAVE);
                    easeEvent.message = roomId;
                    LiveDataBus.get().with(DemoConstant.CHAT_ROOM_CHANGE).postValue(easeEvent);
                    finish();
                }
            });
        });

        viewModel.getMessageChangeObservable().with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.type == EaseEvent.TYPE.CHAT_ROOM) {
                getChatRoom();
            }
            if(event.isChatRoomLeave() && TextUtils.equals(roomId, event.message)) {
                finish();
            }
        });

        viewModel.memberObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    itemChatRoomMembers.getTvContent().setText(data.size() + "人");
                }
            });
        });

        getChatRoom();
    }

    private void updateContent(EMChatRoom chatRoom) {
        itemChatRoomId.getTvContent().setText(chatRoom.getId());
        itemChatRoomDescription.getTvContent().setText(chatRoom.getDescription());
        itemChatRoomName.getTvContent().setText(chatRoom.getName());
        itemChatRoomOwner.getTvContent().setText(chatRoom.getOwner());
        itemChatRoomAdmins.getTvContent().setText((chatRoom.getAdminList().size() + 1 ) + "人");
        itemChatRoomMembers.getTvContent().setText(chatRoom.getMemberList().size() + "人");

        tvChatRoomRefund.setVisibility(isOwner() ? View.VISIBLE : View.GONE);

        if(viewModel != null) {
            viewModel.getChatRoomMembers(chatRoom.getId());
        }
    }

    private void getChatRoom() {
        viewModel.getChatRoomFromServer(roomId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_chat_room_name :
                showChatRoomNameDialog();
                break;
            case R.id.item_chat_room_description :
                showDescriptionDialog();
                break;
            case R.id.item_chat_room_members :
                ChatRoomMemberAuthorityActivity.actionStart(mContext, roomId);
                break;
            case R.id.item_chat_room_admins :
                ChatRoomAdminAuthorityActivity.actionStart(mContext, roomId);
                break;
            case R.id.tv_chat_room_refund :
                new SimpleDialogFragment.Builder(mContext)
                        .setTitle(R.string.em_chat_room_detail_destroy_info)
                        .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                            @Override
                            public void onConfirmClick(View view) {
                                viewModel.destroyGroup(roomId);
                            }
                        })
                        .showCancelButton(true)
                        .show();
                break;
        }
    }

    private void showChatRoomNameDialog() {
        if(!isOwner()) {
            return;
        }
        new EditTextDialogFragment.Builder(mContext)
                .setContent(chatRoom.getName())
                .setConfirmClickListener(new EditTextDialogFragment.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, String content) {
                        if(!TextUtils.isEmpty(content)) {
                            viewModel.changeChatRoomSubject(roomId, content);
                        }
                    }
                })
                .setTitle(R.string.em_chat_room_detail_room_name)
                .show();

    }

    private void showDescriptionDialog() {
        if(!isOwner()) {
            return;
        }
        new EditTextDialogFragment.Builder(mContext)
                .setContent(chatRoom.getDescription())
                .setConfirmClickListener(new EditTextDialogFragment.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, String content) {
                        if(!TextUtils.isEmpty(content)) {
                            viewModel.changeChatroomDescription(roomId, content);
                        }
                    }
                })
                .setTitle(R.string.em_chat_room_detail_description)
                .show();
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    private boolean isOwner() {
        return chatRoom != null && TextUtils.equals(DemoHelper.getInstance().getCurrentUser(), chatRoom.getOwner());
    }
}
