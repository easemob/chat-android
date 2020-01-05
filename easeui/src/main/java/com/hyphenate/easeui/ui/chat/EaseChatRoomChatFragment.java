package com.hyphenate.easeui.ui.chat;

import android.text.TextUtils;
import android.view.View;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.adapter.EMAChatRoomManagerListener;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.interfaces.EaseChatRoomListener;

public class EaseChatRoomChatFragment extends EaseChatFragment {
    private EaseChatRoomListener chatRoomListener;

    @Override
    protected void initChildArguments() {
        super.initChildArguments();
        chatType = EaseConstant.CHATTYPE_CHATROOM;
        emMsgChatType = EMMessage.ChatType.ChatRoom;
    }

    @Override
    protected void initChildListener() {
        super.initChildListener();
        chatRoomListener = new ChatRoomListener();
        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomListener);
    }

    @Override
    protected void initChildData() {
        super.initChildData();
        onChatRoomViewCreation();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.tv_error_msg) {
            onChatRoomViewCreation();
        }
    }

    /**
     * join chat room
     */
    private void onChatRoomViewCreation() {
        EMClient.getInstance().chatroomManager().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {
            @Override
            public void onSuccess(EMChatRoom value) {
                if(isActivityDisable()) {
                    return;
                }
                if(!TextUtils.equals(toChatUsername, value.getId())) {
                    return;
                }
                context.runOnUiThread(()-> {
                    EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(toChatUsername);
                    String title = room != null ? room.getName() : toChatUsername;
                    setTitle(title);

                });
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    /**
     * 设置标题
     * @param room
     */
    private void setTitle(String room) {

    }

    private class ChatRoomListener extends EaseChatRoomListener {

        @Override
        public void onChatRoomDestroyed(String roomId, String roomName) {
            if(isActivityDisable()) {
                return;
            }
            mContext.runOnUiThread(() -> mContext.finish());
        }

        @Override
        public void onRemovedFromChatRoom(int reason, String roomId, String roomName, String participant) {
            if(isActivityDisable()) {
                return;
            }
            mContext.runOnUiThread(() -> {
                if(!TextUtils.equals(roomId, toChatUsername)) {
                    return;
                }
                if(reason == EMAChatRoomManagerListener.BE_KICKED) {
                    mContext.finish();
                }else {
                    tvErrorMsg.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onMemberJoined(String roomId, String participant) {
            if(isActivityDisable()) {
                return;
            }
        }

        @Override
        public void onMemberExited(String roomId, String roomName, String participant) {
            if(isActivityDisable()) {
                return;
            }
        }
    }
}
