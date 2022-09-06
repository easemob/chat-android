package com.hyphenate.chatdemo.section.conversation.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatdemo.DemoHelper;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.db.entity.InviteMessage;
import com.hyphenate.chatdemo.common.db.entity.InviteMessageStatus;
import com.hyphenate.chatdemo.common.db.entity.MsgTypeManageEntity;
import com.hyphenate.chatdemo.common.manager.PushAndMessageHelper;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.manager.EaseAtMessageHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseDateUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.EaseImageView;

import java.util.Date;

public class HomeAdapter extends EaseBaseRecyclerViewAdapter<Object> {

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.demo_item_row_chat_history, parent, false));
    }

    @Override
    public int getEmptyLayoutId() {
        return R.layout.demo_layout_no_data_show_nothing;
    }

    private class MyViewHolder extends ViewHolder<Object> {
        private ConstraintLayout listIteaseLayout;
        private EaseImageView avatar;
        private TextView mUnreadMsgNumber;
        private TextView name;
        private TextView time;
        private ImageView mMsgState;
        private TextView mentioned;
        private TextView message;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            listIteaseLayout = findViewById(R.id.list_itease_layout);
            avatar = findViewById(R.id.avatar);
            mUnreadMsgNumber = findViewById(R.id.unread_msg_number);
            name = findViewById(R.id.name);
            time = findViewById(R.id.time);
            mMsgState = findViewById(R.id.msg_state);
            mentioned = findViewById(R.id.mentioned);
            message = findViewById(R.id.message);
            avatar.setShapeType(DemoHelper.getInstance().getEaseAvatarOptions().getAvatarShape());
        }

        @Override
        public void setData(Object object, int position) {
            if(object instanceof EMConversation) {
                EMConversation item = (EMConversation)object;
                String username = item.conversationId();
                listIteaseLayout.setBackground(!TextUtils.isEmpty(item.getExtField())
                                                ? ContextCompat.getDrawable(mContext, R.drawable.ease_conversation_top_bg)
                                                : null);
                mentioned.setVisibility(View.GONE);
                if(item.getType() == EMConversation.EMConversationType.GroupChat) {
                    if(EaseAtMessageHelper.get().hasAtMeMsg(username)) {
                        mentioned.setText(R.string.were_mentioned);
                        mentioned.setVisibility(View.VISIBLE);
                    }
                    avatar.setImageResource(R.drawable.ease_group_icon);
                    EMGroup group = DemoHelper.getInstance().getGroupManager().getGroup(username);
                    name.setText(group != null ? group.getGroupName() : username);
                }else if(item.getType() == EMConversation.EMConversationType.ChatRoom) {
                    avatar.setImageResource(R.drawable.ease_chat_room_icon);
                    EMChatRoom chatRoom = DemoHelper.getInstance().getChatroomManager().getChatRoom(username);
                    name.setText(chatRoom != null && !TextUtils.isEmpty(chatRoom.getName()) ? chatRoom.getName() : username);
                }else {
                    avatar.setImageResource(R.drawable.ease_default_avatar);
                    name.setText(username);
                }

                if(item.getUnreadMsgCount() > 0) {
                    mUnreadMsgNumber.setText(String.valueOf(item.getUnreadMsgCount()));
                    mUnreadMsgNumber.setVisibility(View.VISIBLE);
                }else {
                    mUnreadMsgNumber.setVisibility(View.GONE);
                }

                if(item.getAllMsgCount() != 0) {
                    EMMessage lastMessage = item.getLastMessage();
                    message.setText(EaseSmileUtils.getSmiledText(mContext, EaseCommonUtils.getMessageDigest(lastMessage, mContext)));
                    time.setText(EaseDateUtils.getTimestampString(mContext, new Date(lastMessage.getMsgTime())));
                    if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                        mMsgState.setVisibility(View.VISIBLE);
                    } else {
                        mMsgState.setVisibility(View.GONE);
                    }
                }

                if(mentioned.getVisibility() != View.VISIBLE) {
                    String unSendMsg = DemoHelper.getInstance().getModel().getUnSendMsg(username);
                    if(!TextUtils.isEmpty(unSendMsg)) {
                        mentioned.setText(R.string.were_not_send_msg);
                        message.setText(unSendMsg);
                        mentioned.setVisibility(View.VISIBLE);
                    }
                }
            }else if(object instanceof MsgTypeManageEntity) {
                String type = ((MsgTypeManageEntity) object).getType();
                Object lastMsg = ((MsgTypeManageEntity) object).getLastMsg();
                if(lastMsg == null || TextUtils.isEmpty(type)) {
                    return;
                }
                listIteaseLayout.setBackground(!TextUtils.isEmpty(((MsgTypeManageEntity) object).getExtField())
                        ? ContextCompat.getDrawable(mContext, R.drawable.ease_conversation_top_bg)
                        : null);
                if(TextUtils.equals(type, MsgTypeManageEntity.msgType.NOTIFICATION.name())) {
                    avatar.setImageResource(R.drawable.em_system_nofinication);
                    name.setText(mContext.getString(R.string.em_conversation_system_notification));
                }
                int unReadCount = ((MsgTypeManageEntity) object).getUnReadCount();
                if(unReadCount > 0) {
                    mUnreadMsgNumber.setText(String.valueOf(unReadCount));
                    mUnreadMsgNumber.setVisibility(View.VISIBLE);
                }else {
                    mUnreadMsgNumber.setVisibility(View.GONE);
                }
                if(lastMsg instanceof InviteMessage) {
                    time.setText(EaseDateUtils.getTimestampString(mContext, new Date(((InviteMessage) lastMsg).getTime())));
                    InviteMessageStatus status = ((InviteMessage) lastMsg).getStatusEnum();
                    if(status == null) {
                        return;
                    }
                    String reason = ((InviteMessage) lastMsg).getReason();
                    if(status == InviteMessageStatus.BEINVITEED ||
                        status == InviteMessageStatus.BEAPPLYED ||
                        status == InviteMessageStatus.GROUPINVITATION) {
                        message.setText(TextUtils.isEmpty(reason) ? PushAndMessageHelper.getSystemMessage((InviteMessage) lastMsg) : reason);
                    }else {
                        message.setText(PushAndMessageHelper.getSystemMessage((InviteMessage) lastMsg));
                    }
                }

            }

        }
    }
}
