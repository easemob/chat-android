package com.hyphenate.chatuidemo.section.chat;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMConferenceListener;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMultiDeviceListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMConferenceMember;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMStreamStatistics;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chatuidemo.DemoApp;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.db.DemoDbHelper;
import com.hyphenate.chatuidemo.common.db.dao.InviteMessageDao;
import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;
import com.hyphenate.chatuidemo.common.db.entity.InviteMessage;
import com.hyphenate.chatuidemo.common.db.entity.MsgTypeManageEntity;
import com.hyphenate.chatuidemo.common.livedatas.MessageChangeLiveData;
import com.hyphenate.chatuidemo.common.livedatas.NetworkChangeLiveData;
import com.hyphenate.chatuidemo.common.manager.PushAndMessageHelper;
import com.hyphenate.chatuidemo.common.repositories.EMContactManagerRepository;
import com.hyphenate.chatuidemo.common.repositories.EMGroupManagerRepository;
import com.hyphenate.easeui.EaseChatPresenter;
import com.hyphenate.easeui.interfaces.EaseGroupListener;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.util.EMLog;
import com.hyphenate.chatuidemo.common.db.entity.InviteMessage.InviteMessageStatus;

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
        //添加网络连接状态监听
        DemoHelper.getInstance().getEMClient().addConnectionListener(new ChatConnectionListener());
        //添加多端登录监听
        DemoHelper.getInstance().getEMClient().addMultiDeviceListener(new ChatMultiDeviceListener());
        //添加群组监听
        DemoHelper.getInstance().getGroupManager().addGroupChangeListener(new ChatGroupListener());
        //添加联系人监听
        DemoHelper.getInstance().getContactManager().setContactListener(new ChatContactListener());
        //添加会议监听
        DemoHelper.getInstance().getConferenceManager().addConferenceListener(new ChatConferenceListener());
        //添加聊天室监听
        DemoHelper.getInstance().getChatroomManager().addChatRoomChangeListener(new ChatRoomListener());

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
        EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_CHANGE_RECEIVE, EaseEvent.TYPE.MESSAGE);
        messageChangeLiveData.postValue(event);
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
        EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_CHANGE_CMD_RECEIVE, EaseEvent.TYPE.MESSAGE);
        messageChangeLiveData.postValue(event);
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_CHANGE_RECALL, EaseEvent.TYPE.MESSAGE);
        messageChangeLiveData.postValue(event);
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
            EMLog.i("TAG", "onConnected");
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
            EMLog.i("TAG", "onDisconnected ="+errorCode);
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
            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setReason(reason);
            msg.setGroupInviter(inviter);
            msg.setStatus(InviteMessage.InviteMessageStatus.GROUPINVITATION);
            msg.setType(MsgTypeManageEntity.msgType.NOTIFICATION);
            notifyNewInviteMessage(msg);
            EaseEvent event = EaseEvent.create(DemoConstant.NOTIFY_GROUP_INVITE_RECEIVE, EaseEvent.TYPE.NOTIFY);
            messageChangeLiveData.postValue(event);
        }

        @Override
        public void onInvitationAccepted(String groupId, String invitee, String reason) {
            super.onInvitationAccepted(groupId, invitee, reason);
            //user accept your invitation
            boolean hasGroup = false;
            EMGroup _group = null;
            for (EMGroup group : DemoHelper.getInstance().getGroupManager().getAllGroups()) {
                if (group.getGroupId().equals(groupId)) {
                    hasGroup = true;
                    _group = group;
                    break;
                }
            }
            if (!hasGroup)
                return;

            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(_group == null ? groupId : _group.getGroupName());
            msg.setReason(reason);
            msg.setGroupInviter(invitee);
            msg.setStatus(InviteMessage.InviteMessageStatus.GROUPINVITATION_ACCEPTED);
            msg.setType(MsgTypeManageEntity.msgType.NOTIFICATION);
            notifyNewInviteMessage(msg);
            EaseEvent event = EaseEvent.create(DemoConstant.NOTIFY_GROUP_INVITE_ACCEPTED, EaseEvent.TYPE.NOTIFY);
            messageChangeLiveData.postValue(event);
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            super.onInvitationDeclined(groupId, invitee, reason);
            //user declined your invitation
            EMGroup group = null;
            for (EMGroup _group : EMClient.getInstance().groupManager().getAllGroups()) {
                if (_group.getGroupId().equals(groupId)) {
                    group = _group;
                    break;
                }
            }
            if (group == null)
                return;

            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(group.getGroupName());
            msg.setReason(reason);
            msg.setGroupInviter(invitee);
            msg.setStatus(InviteMessage.InviteMessageStatus.GROUPINVITATION_DECLINED);
            msg.setType(MsgTypeManageEntity.msgType.NOTIFICATION);
            notifyNewInviteMessage(msg);
            EaseEvent event = EaseEvent.create(DemoConstant.NOTIFY_GROUP_INVITE_DECLINED, EaseEvent.TYPE.NOTIFY);
            messageChangeLiveData.postValue(event);
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            EaseEvent easeEvent = new EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP_LEAVE);
            easeEvent.message = groupId;
            messageChangeLiveData.postValue(easeEvent);
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            EaseEvent easeEvent = new EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP_LEAVE);
            easeEvent.message = groupId;
            messageChangeLiveData.postValue(easeEvent);
        }

        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {
            super.onRequestToJoinReceived(groupId, groupName, applicant, reason);
            // user apply to join group
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applicant);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            msg.setStatus(InviteMessage.InviteMessageStatus.BEAPPLYED);
            msg.setType(MsgTypeManageEntity.msgType.NOTIFICATION);
            notifyNewInviteMessage(msg);
            EaseEvent event = EaseEvent.create(DemoConstant.NOTIFY_GROUP_JOIN_RECEIVE, EaseEvent.TYPE.NOTIFY);
            messageChangeLiveData.postValue(event);
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
            EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_GROUP_JOIN_ACCEPTED, EaseEvent.TYPE.MESSAGE);
            messageChangeLiveData.postValue(event);
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
            EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_GROUP_AUTO_ACCEPT, EaseEvent.TYPE.MESSAGE);
            messageChangeLiveData.postValue(event);
        }
    }

    private class ChatContactListener implements EMContactListener {

        @Override
        public void onContactAdded(String username) {
            EMLog.i("ChatContactListener", "onContactAdded");
            EmUserEntity entity = new EmUserEntity();
            entity.setUsername(username);
            DemoDbHelper.getInstance(DemoApp.getInstance()).getUserDao().insert(entity);
            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
            messageChangeLiveData.postValue(event);
        }

        @Override
        public void onContactDeleted(String username) {
            EMLog.i("ChatContactListener", "onContactDeleted");
            DemoDbHelper helper = DemoDbHelper.getInstance(DemoApp.getInstance());
            helper.getUserDao().deleteUser(username);
            helper.getInviteMessageDao().deleteByFrom(username);
            EMClient.getInstance().chatManager().deleteConversation(username, false);
            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
            messageChangeLiveData.postValue(event);
        }

        @Override
        public void onContactInvited(String username, String reason) {
            EMLog.i("ChatContactListener", "onContactInvited");
            InviteMessageDao dao = DemoDbHelper.getInstance(DemoApp.getInstance()).getInviteMessageDao();
            List<InviteMessage> messages = dao.loadAll();
            if(messages != null && !messages.isEmpty()) {
                for (InviteMessage message : messages) {
                    if(message.getGroupId() == null && message.getFrom().equals(username)) {
                        dao.deleteByFrom(username);
                    }
                }
            }
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            msg.setStatus(InviteMessageStatus.BEINVITEED);
            msg.setType(MsgTypeManageEntity.msgType.NOTIFICATION);
            notifyNewInviteMessage(msg);
            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
            messageChangeLiveData.postValue(event);
        }

        @Override
        public void onFriendRequestAccepted(String username) {
            EMLog.i("ChatContactListener", "onFriendRequestAccepted");
            InviteMessageDao dao = DemoDbHelper.getInstance(DemoApp.getInstance()).getInviteMessageDao();
            List<String> messages = dao.loadAllNames();
            if(messages.contains(username)) {
                return;
            }
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setStatus(InviteMessageStatus.BEAGREED);
            msg.setType(MsgTypeManageEntity.msgType.NOTIFICATION);
            notifyNewInviteMessage(msg);
            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
            messageChangeLiveData.postValue(event);
        }

        @Override
        public void onFriendRequestDeclined(String username) {
            EMLog.i("ChatContactListener", "onFriendRequestDeclined");
        }
    }

    private class ChatMultiDeviceListener implements EMMultiDeviceListener {

        @Override
        public void onContactEvent(int event, String target, String ext) {
            EMLog.i("ChatMultiDeviceListener", "onContactEvent event"+event);
            DemoDbHelper dbHelper = DemoDbHelper.getInstance(DemoApp.getInstance());
            String message = null;
            switch (event) {
                case CONTACT_REMOVE: //好友已经在其他机子上被移除
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_REMOVE");
                    message = DemoConstant.CONTACT_REMOVE;
                    dbHelper.getUserDao().deleteUser(target);
                    dbHelper.getInviteMessageDao().deleteByFrom(target);
                    // TODO: 2020/1/16 0016 确认此处逻辑，是否是删除当前的target
                    DemoHelper.getInstance().getChatManager().deleteConversation(target, false);

                    break;
                case CONTACT_ACCEPT: //好友请求已经在其他机子上被同意
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_ACCEPT");
                    message = DemoConstant.CONTACT_ACCEPT;
                    EmUserEntity  entity = new EmUserEntity();
                    entity.setUsername(target);
                    dbHelper.getUserDao().insert(entity);
                    updateContactNotificationStatus(target, "", InviteMessage.InviteMessageStatus.MULTI_DEVICE_CONTACT_ACCEPT);
                    break;
                case CONTACT_DECLINE: //好友请求已经在其他机子上被拒绝
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_DECLINE");
                    message = DemoConstant.CONTACT_DECLINE;
                    updateContactNotificationStatus(target, "", InviteMessage.InviteMessageStatus.MULTI_DEVICE_CONTACT_DECLINE);
                    break;
                case CONTACT_BAN: //当前用户在其他设备加某人进入黑名单
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_BAN");
                    message = DemoConstant.CONTACT_BAN;
                    dbHelper.getUserDao().deleteUser(target);
                    dbHelper.getInviteMessageDao().deleteByFrom(target);
                    DemoHelper.getInstance().getChatManager().deleteConversation(target, false);
                    updateContactNotificationStatus(target, "", InviteMessage.InviteMessageStatus.MULTI_DEVICE_CONTACT_BAN);
                    break;
                case CONTACT_ALLOW: // 好友在其他设备被移出黑名单
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_ALLOW");
                    message = DemoConstant.CONTACT_ALLOW;
                    updateContactNotificationStatus(target, "", InviteMessage.InviteMessageStatus.MULTI_DEVICE_CONTACT_ALLOW);
                    break;
            }
            if(!TextUtils.isEmpty(message)) {
                EaseEvent easeEvent = EaseEvent.create(message, EaseEvent.TYPE.CONTACT);
                messageChangeLiveData.postValue(easeEvent);
            }
        }

        @Override
        public void onGroupEvent(int event, String groupId, List<String> usernames) {
            EMLog.i("ChatMultiDeviceListener", "onGroupEvent event"+event);
            InviteMessageDao messageDao = DemoDbHelper.getInstance(DemoApp.getInstance()).getInviteMessageDao();
            String message = null;
            switch (event) {
                case GROUP_CREATE:
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_CREATE);
                    break;
                case GROUP_DESTROY:
                    messageDao.deleteByGroupId(groupId);
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_DESTROY);
                    message = DemoConstant.GROUP_CHANGE;
                    break;
                case GROUP_JOIN:
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_JOIN);
                    message = DemoConstant.GROUP_CHANGE;
                    break;
                case GROUP_LEAVE:
                    messageDao.deleteByGroupId(groupId);
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_LEAVE);
                    message = DemoConstant.GROUP_CHANGE;
                    break;
                case GROUP_APPLY:
                    messageDao.deleteByGroupId(groupId);
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY);
                    break;
                case GROUP_APPLY_ACCEPT:
                    messageDao.deleteByGroupId(groupId, usernames.get(0));
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY_ACCEPT);
                    break;
                case GROUP_APPLY_DECLINE:
                    messageDao.deleteByGroupId(groupId, usernames.get(0));
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY_DECLINE);
                    break;
                case GROUP_INVITE:
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE);
                    break;
                case GROUP_INVITE_ACCEPT:
                    String st3 = context.getString(R.string.Invite_you_to_join_a_group_chat);
                    EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    msg.setChatType(EMMessage.ChatType.GroupChat);
                    // TODO: person, reason from ext
                    String from = "";
                    if (usernames != null && usernames.size() > 0) {
                        msg.setFrom(usernames.get(0));
                    }
                    msg.setTo(groupId);
                    msg.setMsgId(UUID.randomUUID().toString());
                    msg.addBody(new EMTextMessageBody(msg.getFrom() + " " +st3));
                    msg.setStatus(EMMessage.Status.SUCCESS);
                    // save invitation as messages
                    EMClient.getInstance().chatManager().saveMessage(msg);

                    messageDao.deleteByGroupId(groupId);
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_ACCEPT);
                    message = DemoConstant.GROUP_CHANGE;
                    break;
                case GROUP_INVITE_DECLINE:
                    messageDao.deleteByGroupId(groupId);
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_DECLINE);
                    break;
                case GROUP_KICK:
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_DECLINE);
                    break;
                case GROUP_BAN:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_BAN);
                    break;
                case GROUP_ALLOW:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ALLOW);
                    break;
                case GROUP_BLOCK:
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_BLOCK);
                    break;
                case GROUP_UNBLOCK:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_UNBLOCK);
                    break;
                case GROUP_ASSIGN_OWNER:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ASSIGN_OWNER);
                    break;
                case GROUP_ADD_ADMIN:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ADD_ADMIN);
                    break;
                case GROUP_REMOVE_ADMIN:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_REMOVE_ADMIN);
                    break;
                case GROUP_ADD_MUTE:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ADD_MUTE);
                    break;
                case GROUP_REMOVE_MUTE:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_REMOVE_MUTE);
                    break;
                default:
                    break;
            }
            if(!TextUtils.isEmpty(message)) {
                EaseEvent easeEvent = EaseEvent.create(message, EaseEvent.TYPE.GROUP);
                messageChangeLiveData.postValue(easeEvent);
            }
        }
    }

    private void notifyNewInviteMessage(InviteMessage msg) {
        msg.setUnread(true);
        DemoHelper.getInstance().insert(msg);
    }

    private void updateContactNotificationStatus(String from, String reason, InviteMessage.InviteMessageStatus status) {
        InviteMessage msg = null;
        InviteMessageDao dao = DemoDbHelper.getInstance(DemoApp.getInstance()).getInviteMessageDao();
        List<InviteMessage> messages = dao.loadAll();
        if(messages != null && !messages.isEmpty()) {
            for (InviteMessage _msg : messages) {
                if (_msg.getFrom().equals(from)) {
                    msg = _msg;
                    break;
                }
            }
        }

        if (msg != null) {
            msg.setStatus(status);
            dao.insert(msg);
        } else {
            // save invitation as message
            msg = new InviteMessage();
            msg.setFrom(from);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            msg.setStatus(status);
            msg.setType(MsgTypeManageEntity.msgType.NOTIFICATION);
            notifyNewInviteMessage(msg);
        }
    }

    private void saveGroupNotification(String groupId, String groupName, String inviter, String reason, InviteMessageStatus status) {
        InviteMessage msg = new InviteMessage();
        msg.setFrom(groupId);
        msg.setTime(System.currentTimeMillis());
        msg.setGroupId(groupId);
        msg.setGroupName(groupName);
        msg.setReason(reason);
        msg.setGroupInviter(inviter);
        msg.setStatus(status);
        msg.setType(MsgTypeManageEntity.msgType.NOTIFICATION);
        notifyNewInviteMessage(msg);
    }

    private class ChatRoomListener implements EMChatRoomChangeListener {

        @Override
        public void onChatRoomDestroyed(String roomId, String roomName) {
            setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM_LEAVE);
        }

        @Override
        public void onMemberJoined(String roomId, String participant) {
            setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM);
        }

        @Override
        public void onMemberExited(String roomId, String roomName, String participant) {
            setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM);
        }

        @Override
        public void onRemovedFromChatRoom(int reason, String roomId, String roomName, String participant) {
            if(TextUtils.equals(DemoHelper.getInstance().getCurrentUser(), participant)) {
                setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM);
            }
        }

        @Override
        public void onMuteListAdded(String chatRoomId, List<String> mutes, long expireTime) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);
        }

        @Override
        public void onMuteListRemoved(String chatRoomId, List<String> mutes) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);
        }

        @Override
        public void onAdminAdded(String chatRoomId, String admin) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);
        }

        @Override
        public void onAdminRemoved(String chatRoomId, String admin) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);
        }

        @Override
        public void onOwnerChanged(String chatRoomId, String newOwner, String oldOwner) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);
        }

        @Override
        public void onAnnouncementChanged(String chatRoomId, String announcement) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);
        }
    }

    private void setChatRoomEvent(String roomId, EaseEvent.TYPE type) {
        EaseEvent easeEvent = new EaseEvent(DemoConstant.CHAT_ROOM_CHANGE, type);
        easeEvent.message = roomId;
        messageChangeLiveData.postValue(easeEvent);
    }
}
