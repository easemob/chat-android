package com.hyphenate.easeim.section.group.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.db.entity.EmUserEntity;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.section.group.GroupHelper;
import com.hyphenate.easeim.section.group.viewmodels.ChatRoomMemberViewModel;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.ViewModelProvider;

public class ChatRoomMemberAuthorityActivity extends GroupMemberAuthorityActivity {
    protected EMChatRoom chatRoom;
    protected String roomId;
    protected ChatRoomMemberViewModel viewModel;

    public static void actionStart(Context context, String roomId) {
        Intent starter = new Intent(context, ChatRoomMemberAuthorityActivity.class);
        starter.putExtra("roomId", roomId);
        context.startActivity(starter);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        roomId = intent.getStringExtra("roomId");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar.setTitle(getString(R.string.em_chat_room_detail_members));
    }

    @Override
    protected void onSubPrepareOptionsMenu(Menu menu) {
        super.onSubPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_group_add).setVisible(false);
        if(!isOwner() && !isInAdminList(DemoHelper.getInstance().getCurrentUser())) {
            menu.findItem(R.id.action_group_black).setVisible(false);
            menu.findItem(R.id.action_group_mute).setVisible(false);
        }
    }

    @Override
    protected void initData() {
        chatRoom = DemoHelper.getInstance().getChatroomManager().getChatRoom(roomId);
        viewModel = new ViewModelProvider(this).get(ChatRoomMemberViewModel.class);
        getData();
    }

    @Override
    public void getData() {
        viewModel.chatRoomObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMChatRoom>() {
                @Override
                public void onSuccess(EMChatRoom data) {
                    chatRoom = data;
                    refreshData();
                }
            });
        });

        viewModel.membersObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    //List<EaseUser> parse = EmUserEntity.parse(data);
                    List<EaseUser> users = new ArrayList<>();
                    if(data != null && !data.isEmpty()){
                        for(int i = 0; i < data.size(); i++){
                            EaseUser user = DemoHelper.getInstance().getUserInfo(data.get(i));
                            if(user != null){
                                users.add(user);
                            }else{
                                EaseUser m_user = new EaseUser(data.get(i));
                                users.add(m_user);
                            }
                        }
                    }
                    sortUserData(users);
                    adapter.setData(users);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });
        });

        viewModel.blackObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    blackMembers = data;
                    if(flag == TYPE_BLACK) {
                        List<EaseUser> parse = EmUserEntity.parse(data);
                        sortUserData(parse);
                        adapter.setData(parse);
                    }

                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    if(flag == TYPE_BLACK) {
                        finishRefresh();
                    }

                }
            });
        });

        viewModel.muteMapObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Map<String, Long>>() {
                @Override
                public void onSuccess(Map<String, Long> data) {
                    muteMembers = new ArrayList<>(data.keySet());
                    if(flag == TYPE_MUTE) {
                        List<EaseUser> parse = EmUserEntity.parse(muteMembers);
                        sortUserData(parse);
                        adapter.setData(parse);
                    }

                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    if(flag == TYPE_MUTE) {
                        finishRefresh();
                    }

                }
            });
        });

        viewModel.destroyGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {

                }
            });
        });

        viewModel.getMessageChangeObservable().with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isChatRoomLeave() && TextUtils.equals(roomId, event.message)) {
                finish();
                return;
            }
            if(TextUtils.equals(event.event, DemoConstant.CHAT_ROOM_CHANGE)) {
                viewModel.getChatRoom(roomId);
            }
        });


        //监听有关用户属性的事件
        viewModel.getMessageChangeObservable().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event != null) {
                refreshData();
            }
        });
        viewModel.getMessageChangeObservable().with(DemoConstant.CONTACT_UPDATE, EaseEvent.class).observe(this, event -> {
            if(event != null) {
                refreshData();
            }
        });
        viewModel.getMessageChangeObservable().with(DemoConstant.CONTACT_ADD, EaseEvent.class).observe(this, event -> {
            if(event != null) {
                refreshData();
            }
        });

        refreshData();
    }

    @Override
    protected void refreshData() {
        if(flag == TYPE_MEMBER) {
            viewModel.getMembersList(roomId);
        }
        if(!isMember()) {
            viewModel.getGroupBlackList(roomId);
            viewModel.getGroupMuteMap(roomId);
        }
        if(flag == TYPE_MEMBER) {
            titleBar.setTitle(getString(R.string.em_authority_menu_member_list));
        }else if(flag == TYPE_BLACK) {
            titleBar.setTitle(getString(R.string.em_authority_menu_black_list));
        }else {
            titleBar.setTitle(getString(R.string.em_authority_menu_mute_list));
        }
    }

    @Override
    public boolean isMember() {
        String currentUser = DemoHelper.getInstance().getCurrentUser();
        return !TextUtils.equals(currentUser, chatRoom.getOwner())
                && !chatRoom.getAdminList().contains(currentUser);
    }

    @Override
    public boolean isInAdminList(String username) {
        return GroupHelper.isInAdminList(username, chatRoom.getAdminList());
    }

    @Override
    public boolean isOwner() {
        return GroupHelper.isOwner(chatRoom);
    }

    @Override
    protected void addToAdmins(String username) {
        viewModel.addGroupAdmin(roomId, username);
        LiveDataBus.get().with(DemoConstant.CHAT_ROOM_CHANGE).postValue(EaseEvent.create(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.TYPE.CHAT_ROOM));
    }

    @Override
    protected void removeFromAdmins(String username) {
        viewModel.removeGroupAdmin(roomId, username);
        LiveDataBus.get().with(DemoConstant.CHAT_ROOM_CHANGE).postValue(EaseEvent.create(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.TYPE.CHAT_ROOM));
    }

    @Override
    protected void transferOwner(String username) {
        viewModel.changeOwner(roomId, username);
        LiveDataBus.get().with(DemoConstant.CHAT_ROOM_CHANGE).postValue(EaseEvent.create(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.TYPE.CHAT_ROOM));
    }

    @Override
    protected void removeFromGroup(String username) {
        LiveDataBus.get().with(DemoConstant.CHAT_ROOM_CHANGE).postValue(EaseEvent.create(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.TYPE.CHAT_ROOM));
        List<String> usernames = new ArrayList<>();
        usernames.add(username);
        viewModel.removeUserFromGroup(roomId, usernames);
    }

    @Override
    protected void addToBlack(String username) {
        List<String> usernames = new ArrayList<>();
        usernames.add(username);
        viewModel.blockUser(roomId, usernames);
        LiveDataBus.get().with(DemoConstant.CHAT_ROOM_CHANGE).postValue(EaseEvent.create(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.TYPE.CHAT_ROOM));
    }

    @Override
    protected void removeFromBlacks(String username) {
        List<String> usernames = new ArrayList<>();
        usernames.add(username);
        viewModel.unblockUser(roomId, usernames);
        LiveDataBus.get().with(DemoConstant.CHAT_ROOM_CHANGE).postValue(EaseEvent.create(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.TYPE.CHAT_ROOM));
    }

    @Override
    protected void AddToMuteMembers(String username) {
        List<String> mutes = new ArrayList<>();
        mutes.add(username);
        viewModel.muteGroupMembers(roomId, mutes, 20 * 60 * 1000);
    }

    @Override
    protected void removeFromMuteMembers(String username) {
        List<String> unMutes = new ArrayList<>();
        unMutes.add(username);
        viewModel.unMuteGroupMembers(roomId, unMutes);
    }

    protected void sortUserData(List<EaseUser> users) {
        Collections.sort(users, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                    return lhs.getNickname().compareTo(rhs.getNickname());
                }else{
                    if("#".equals(lhs.getInitialLetter())){
                        return 1;
                    }else if("#".equals(rhs.getInitialLetter())){
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });
    }
}
