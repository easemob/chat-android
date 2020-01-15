package com.hyphenate.chatuidemo.section.chat;

import android.os.SystemClock;
import android.util.Log;

import com.hyphenate.EMConferenceListener;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMConferenceMember;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMStreamStatistics;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.db.entity.InviteMessageEntity;
import com.hyphenate.chatuidemo.common.livedatas.MessageChangeLiveData;
import com.hyphenate.chatuidemo.common.livedatas.NetworkChangeLiveData;
import com.hyphenate.chatuidemo.common.manager.PushAndMessageHelper;
import com.hyphenate.chatuidemo.common.repositories.EMContactManagerRepository;
import com.hyphenate.chatuidemo.common.repositories.EMGroupManagerRepository;
import com.hyphenate.chatuidemo.common.repositories.NetworkOnlyResource;
import com.hyphenate.easeui.EaseChatPresenter;
import com.hyphenate.easeui.interfaces.EaseGroupListener;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.util.EMLog;

import java.util.List;
import java.util.UUID;

public class ChatPresenter extends EaseChatPresenter {
    private static final String TAG = ChatPresenter.class.getSimpleName();
    private static ChatPresenter instance;
    private MessageChangeLiveData messageChangeLiveData;
    private NetworkChangeLiveData networkChangeLiveData;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;

    private ChatPresenter() {
        messageChangeLiveData = MessageChangeLiveData.getInstance();
        networkChangeLiveData = NetworkChangeLiveData.getInstance();
    }

    public static ChatPresenter getInstance() {
        if(instance == null) {
            synchronized (ChatPresenter.class) {
                if(instance == null) {
                    instance = new ChatPresenter();
                }
            }
        }
        return instance;
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        super.onMessageReceived(messages);
        messageChangeLiveData.postValue(DemoConstant.MESSAGE_CHANGE_RECEIVE);
        for (EMMessage message : messages) {
            EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
            // 判断一下是否是会议邀请
            String confId = message.getStringAttribute(DemoConstant.MSG_ATTR_CONF_ID, "");
            if(!"".equals(confId)){
                String password = message.getStringAttribute(DemoConstant.MSG_ATTR_CONF_PASS, "");
                String extension = message.getStringAttribute(DemoConstant.MSG_ATTR_EXTENSION, "");
                PushAndMessageHelper.goConference(context, confId, password, extension);
            }
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        super.onCmdMessageReceived(messages);
        messageChangeLiveData.postValue(DemoConstant.MESSAGE_CHANGE_CMD_RECEIVE);
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        messageChangeLiveData.postValue(DemoConstant.MESSAGE_CHANGE_RECALL);
        for (EMMessage msg : messages) {
            if(msg.getChatType() == EMMessage.ChatType.GroupChat && EaseAtMessageHelper.get().isAtMeMsg(msg)){
                EaseAtMessageHelper.get().removeAtMeGroup(msg.getTo());
            }
            EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            EMTextMessageBody txtBody = new EMTextMessageBody(String.format(context.getString(R.string.msg_recall_by_user), msg.getFrom()));
            msgNotification.addBody(txtBody);
            msgNotification.setFrom(msg.getFrom());
            msgNotification.setTo(msg.getTo());
            msgNotification.setUnread(false);
            msgNotification.setMsgTime(msg.getMsgTime());
            msgNotification.setLocalTime(msg.getMsgTime());
            msgNotification.setChatType(msg.getChatType());
            msgNotification.setAttribute(DemoConstant.MESSAGE_TYPE_RECALL, true);
            msgNotification.setStatus(EMMessage.Status.SUCCESS);
            EMClient.getInstance().chatManager().saveMessage(msgNotification);
        }
    }

    private class ChatConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            if(!DemoHelper.getInstance().isLoggedIn()) {
                return;
            }
            if(!isGroupsSyncedWithServer) {
                Log.i(TAG, "isGroupsSyncedWithServer");
                new EMGroupManagerRepository().getAllGroups();
                isGroupsSyncedWithServer = true;
            }
            if(!isContactsSyncedWithServer) {
                Log.i(TAG, "isContactsSyncedWithServer");
                new EMContactManagerRepository().getContactList();
                isContactsSyncedWithServer = true;
            }
            if(!isBlackListSyncedWithServer) {
                Log.i(TAG, "isBlackListSyncedWithServer");
                new EMContactManagerRepository().getBlackContactList();
                isBlackListSyncedWithServer = true;
            }
        }

        @Override
        public void onDisconnected(int errorCode) {
            networkChangeLiveData.postValue(errorCode);
        }
    }

    private class ChatConferenceListener implements EMConferenceListener {

        @Override
        public void onMemberJoined(EMConferenceMember member) {
            EMLog.i(TAG, String.format("member joined username: %s, member: %d", member.memberName,
                    EMClient.getInstance().conferenceManager().getConferenceMemberList().size()));
        }

        @Override
        public void onMemberExited(EMConferenceMember member) {
            EMLog.i(TAG, String.format("member exited username: %s, member size: %d", member.memberName,
                    EMClient.getInstance().conferenceManager().getConferenceMemberList().size()));
        }

        @Override
        public void onStreamAdded(EMConferenceStream stream) {
            EMLog.i(TAG, String.format("Stream added streamId: %s, streamName: %s, memberName: %s, username: %s, extension: %s, videoOff: %b, mute: %b",
                    stream.getStreamId(), stream.getStreamName(), stream.getMemberName(), stream.getUsername(),
                    stream.getExtension(), stream.isVideoOff(), stream.isAudioOff()));
            EMLog.i(TAG, String.format("Conference stream subscribable: %d, subscribed: %d",
                    EMClient.getInstance().conferenceManager().getAvailableStreamMap().size(),
                    EMClient.getInstance().conferenceManager().getSubscribedStreamMap().size()));
        }

        @Override
        public void onStreamRemoved(EMConferenceStream stream) {
            EMLog.i(TAG, String.format("Stream removed streamId: %s, streamName: %s, memberName: %s, username: %s, extension: %s, videoOff: %b, mute: %b",
                    stream.getStreamId(), stream.getStreamName(), stream.getMemberName(), stream.getUsername(),
                    stream.getExtension(), stream.isVideoOff(), stream.isAudioOff()));
            EMLog.i(TAG, String.format("Conference stream subscribable: %d, subscribed: %d",
                    EMClient.getInstance().conferenceManager().getAvailableStreamMap().size(),
                    EMClient.getInstance().conferenceManager().getSubscribedStreamMap().size()));
        }

        @Override
        public void onStreamUpdate(EMConferenceStream stream) {
            EMLog.i(TAG, String.format("Stream added streamId: %s, streamName: %s, memberName: %s, username: %s, extension: %s, videoOff: %b, mute: %b",
                    stream.getStreamId(), stream.getStreamName(), stream.getMemberName(), stream.getUsername(),
                    stream.getExtension(), stream.isVideoOff(), stream.isAudioOff()));
            EMLog.i(TAG, String.format("Conference stream subscribable: %d, subscribed: %d",
                    EMClient.getInstance().conferenceManager().getAvailableStreamMap().size(),
                    EMClient.getInstance().conferenceManager().getSubscribedStreamMap().size()));
        }

        @Override
        public void onPassiveLeave(int error, String message) {
            EMLog.i(TAG, String.format("passive leave code: %d, message: %s", error, message));
        }

        @Override
        public void onConferenceState(ConferenceState state) {
            EMLog.i(TAG, String.format("State code=%d", state.ordinal()));
        }

        @Override
        public void onStreamStatistics(EMStreamStatistics statistics) {
            EMLog.d(TAG, statistics.toString());
        }

        @Override
        public void onStreamSetup(String streamId) {
            EMLog.i(TAG, String.format("Stream id - %s", streamId));
        }

        @Override
        public void onSpeakers(List<String> speakers) {

        }

        @Override
        public void onReceiveInvite(String confId, String password, String extension) {
            EMLog.i(TAG, String.format("Receive conference invite confId: %s, password: %s, extension: %s", confId, password, extension));
            PushAndMessageHelper.goConference(context, confId, password, extension);
        }

        @Override
        public void onRoleChanged(EMConferenceManager.EMConferenceRole role) {

        }
    }

    private class ChatGroupListener extends EaseGroupListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            super.onInvitationReceived(groupId, groupName, inviter, reason);

            InviteMessageEntity msg = new InviteMessageEntity();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setReason(reason);
            msg.setGroupInviter(inviter);
        }

        @Override
        public void onInvitationAccepted(String groupId, String invitee, String reason) {
            super.onInvitationAccepted(groupId, invitee, reason);

        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            super.onInvitationDeclined(groupId, invitee, reason);

        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {

        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {
            super.onRequestToJoinReceived(groupId, groupName, applicant, reason);

        }

        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {
            super.onRequestToJoinAccepted(groupId, groupName, accepter);
            String st4 = context.getString(R.string.Agreed_to_your_group_chat_application);
            // your application was accepted
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new EMTextMessageBody(accepter + " " +st4));
            msg.setStatus(EMMessage.Status.SUCCESS);
            // save accept message
            EMClient.getInstance().chatManager().saveMessage(msg);
        }

        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            super.onRequestToJoinDeclined(groupId, groupName, decliner, reason);

        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            super.onAutoAcceptInvitationFromGroup(groupId, inviter, inviteMessage);
            String st3 = context.getString(R.string.Invite_you_to_join_a_group_chat);
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(inviter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new EMTextMessageBody(inviter + " " +st3));
            msg.setStatus(EMMessage.Status.SUCCESS);
            // save invitation as messages
            EMClient.getInstance().chatManager().saveMessage(msg);
        }
    }
}
