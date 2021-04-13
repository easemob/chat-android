package com.hyphenate.easeim.section.group.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.db.entity.EmUserEntity;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.section.group.GroupHelper;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomAdminAuthorityActivity extends ChatRoomMemberAuthorityActivity {

    public static void actionStart(Context context, String roomId) {
        Intent starter = new Intent(context, ChatRoomAdminAuthorityActivity.class);
        starter.putExtra("roomId", roomId);
        context.startActivity(starter);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar.setTitle(getString(R.string.em_authority_menu_admin_list));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void getData() {
        viewModel.chatRoomObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMChatRoom>() {
                @Override
                public void onSuccess(EMChatRoom group) {
                    List<String> adminList = group.getAdminList();
                    if(adminList == null) {
                        adminList = new ArrayList<>();
                    }
                    adminList.add(group.getOwner());
                    //adapter.setData(EmUserEntity.parse(adminList));
                    List<EaseUser> users = new ArrayList<>();
                    if(adminList != null && !adminList.isEmpty()){
                        for(int i = 0; i < adminList.size(); i++){
                            EaseUser user = DemoHelper.getInstance().getUserInfo(adminList.get(i));
                            if(user != null){
                                users.add(user);
                            }else{
                                EaseUser m_user = new EaseUser(adminList.get(i));
                                users.add(m_user);
                            }
                        }
                    }
                    adapter.setData(users);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });
        });
        viewModel.getMessageChangeObservable().with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.type == EaseEvent.TYPE.CHAT_ROOM) {
                refreshData();
            }
            if(event.isChatRoomLeave() && TextUtils.equals(roomId, event.message)) {
                finish();
            }
        });
        refreshData();
    }

    @Override
    protected void refreshData() {
        viewModel.getChatRoom(roomId);
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        String username = adapter.getItem(position).getUsername();
        //不能操作群主
        if(TextUtils.equals(chatRoom.getOwner(), username)) {
            return false;
        }
        //管理员不能操作
        if(GroupHelper.isAdmin(chatRoom)) {
            return false;
        }
        return super.onItemLongClick(view, position);
    }
}
