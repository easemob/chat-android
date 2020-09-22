package com.hyphenate.easeim.section.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMConferenceListener;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMultiDeviceListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMConferenceMember;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMStreamStatistics;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.adapter.EMAChatRoomManagerListener;
import com.hyphenate.easeim.DemoApplication;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.MainActivity;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.db.DemoDbHelper;
import com.hyphenate.easeim.common.db.dao.InviteMessageDao;
import com.hyphenate.easeim.common.db.entity.EmUserEntity;
import com.hyphenate.easeim.common.db.entity.InviteMessage;
import com.hyphenate.easeim.common.db.entity.MsgTypeManageEntity;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.manager.PushAndMessageHelper;
import com.hyphenate.easeim.common.repositories.EMContactManagerRepository;
import com.hyphenate.easeim.common.repositories.EMGroupManagerRepository;
import com.hyphenate.easeim.section.group.GroupHelper;
import com.hyphenate.easeui.interfaces.EaseGroupListener;
import com.hyphenate.easeui.manager.EaseAtMessageHelper;
import com.hyphenate.easeui.manager.EaseChatPresenter;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.util.EMLog;
import com.hyphenate.easeim.common.db.entity.InviteMessage.InviteMessageStatus;

import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 主要用于chat过程中的全局监听，并对相应的事件进行处理
 */
public class ChatPresenter extends EaseChatPresenter {
    private static final String TAG = ChatPresenter.class.getSimpleName();
    private static final int HANDLER_SHOW_TOAST = 0;
    private static final int HANDLER_START_CONFERENCE = 1;
    private static ChatPresenter instance;
    private LiveDataBus messageChangeLiveData;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;
    private Context appContext;
    protected Handler handler;

    Queue<String> msgQueue = new ConcurrentLinkedQueue<>();

    private ChatPresenter() {
        appContext = DemoApplication.getInstance();
        initHandler(appContext.getMainLooper());
        messageChangeLiveData = LiveDataBus.get();
        //添加网络连接状态监听
        DemoHelper.getInstance().getEMClient().addConnectionListener(new ChatConnectionListener());
        //添加多端登录监听
        DemoHelper.getInstance().getEMClient().addMultiDeviceListener(new ChatMultiDeviceListener());
        //添加群组监听
        DemoHelper.getInstance().getGroupManager().addGroupChangeListener(new ChatGroupListener());
        //添加联系人监听
        DemoHelper.getInstance().getContactManager().setContactListener(new ChatContactListener());
        //添加聊天室监听
        DemoHelper.getInstance().getChatroomManager().addChatRoomChangeListener(new ChatRoomListener());
        //添加会议监听
        DemoHelper.getInstance().getConferenceManager().addConferenceListener(new ChatConferenceListener());
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

    /**
     * 将需要登录成功进入MainActivity中初始化的逻辑，放到此处进行处理
     */
    public void init() {

    }

    public void initHandler(Looper looper) {
        handler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                Object obj = msg.obj;
                switch (msg.what) {
                    case HANDLER_SHOW_TOAST :
                        if(obj instanceof String) {
                            String str = (String) obj;
                            Toast.makeText(appContext, str, Toast.LENGTH_LONG).show();
                        }
                        break;
                    case HANDLER_START_CONFERENCE:
                        if(!isAppLaunchMain()) {
                            Message message = Message.obtain(handler, HANDLER_START_CONFERENCE, obj);
                            handler.sendMessageDelayed(message, 500);
                            return;
                        }
                        if(obj instanceof EMMessage) {
                            startConference((EMMessage) obj);
                            // in background, do not refresh UI, notify it in notification bar
                            if(!DemoApplication.getInstance().getLifecycleCallbacks().isFront()){
                                getNotifier().notify((EMMessage) obj);
                            }
                            //notify new message
                            getNotifier().vibrateAndPlayTone((EMMessage) obj);
                        }
                        break;
                }


            }
        };
        while (!msgQueue.isEmpty()) {
            showToast(msgQueue.remove());
        }
    }

    void showToast(@StringRes int mesId) {
        showToast(context.getString(mesId));
    }

    void showToast(final String message) {
        Log.d(TAG, "receive invitation to join the group：" + message);
        if (handler != null) {
            Message msg = Message.obtain(handler, HANDLER_SHOW_TOAST, message);
            handler.sendMessage(msg);
        } else {
            msgQueue.add(message);
        }
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        super.onMessageReceived(messages);
        EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_CHANGE_RECEIVE, EaseEvent.TYPE.MESSAGE);
        messageChangeLiveData.with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(event);
        for (EMMessage message : messages) {
            EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
            EMLog.d(TAG, "onMessageReceived: " + message.getType());
            // 判断一下是否是会议邀请
            String confId = message.getStringAttribute(DemoConstant.MSG_ATTR_CONF_ID, "");
            if(!TextUtils.isEmpty(confId)){
                if(!isAppLaunchMain()) {
                    Message obtain = Message.obtain(handler, HANDLER_START_CONFERENCE, message);
                    handler.sendMessageDelayed(obtain, 500);
                    return;
                }
                startConference(message);
            }
            // in background, do not refresh UI, notify it in notification bar
            if(!DemoApplication.getInstance().getLifecycleCallbacks().isFront()){
                getNotifier().notify(message);
            }
            //notify new message
            getNotifier().vibrateAndPlayTone(message);
        }
    }

    private void startConference(EMMessage message) {
        String confId = message.getStringAttribute(DemoConstant.MSG_ATTR_CONF_ID, "");
        String password = message.getStringAttribute(DemoConstant.MSG_ATTR_CONF_PASS, "");
        String extension = message.getStringAttribute(DemoConstant.MSG_ATTR_EXTENSION, "");
        PushAndMessageHelper.goConference(context, confId, password, extension);
    }

    /**
     * 判断是否已经启动了MainActivity
     * @return
     */
    private synchronized boolean isAppLaunchMain() {
        List<Activity> activities = DemoApplication.getInstance().getLifecycleCallbacks().getActivityList();
        if(activities != null && !activities.isEmpty()) {
            for(int i = activities.size() - 1; i >= 0 ; i--) {
                if(activities.get(i) instanceof MainActivity) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        super.onCmdMessageReceived(messages);
        EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_CHANGE_CMD_RECEIVE, EaseEvent.TYPE.MESSAGE);
        messageChangeLiveData.with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(event);
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_CHANGE_RECALL, EaseEvent.TYPE.MESSAGE);
        messageChangeLiveData.with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(event);
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
            EMLog.i(TAG, "onConnected");
            if(!DemoHelper.getInstance().isLoggedIn()) {
                return;
            }
            if(!isGroupsSyncedWithServer) {
                EMLog.i(TAG, "isGroupsSyncedWithServer");
                new EMGroupManagerRepository().getAllGroups();
                isGroupsSyncedWithServer = true;
            }
            if(!isContactsSyncedWithServer) {
                EMLog.i(TAG, "isContactsSyncedWithServer");
                new EMContactManagerRepository().getContactList();
                isContactsSyncedWithServer = true;
            }
            if(!isBlackListSyncedWithServer) {
                EMLog.i(TAG, "isBlackListSyncedWithServer");
                new EMContactManagerRepository().getBlackContactList();
                isBlackListSyncedWithServer = true;
            }
        }

        /**
         * 用来监听账号异常
         * @param error
         */
        @Override
        public void onDisconnected(int error) {
            EMLog.i(TAG, "onDisconnected ="+error);
            String event = null;
            if (error == EMError.USER_REMOVED) {
                event = DemoConstant.ACCOUNT_REMOVED;
            } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                event = DemoConstant.ACCOUNT_CONFLICT;
            } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                event = DemoConstant.ACCOUNT_FORBIDDEN;
            } else if (error == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
                event = DemoConstant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD;
            } else if (error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                event = DemoConstant.ACCOUNT_KICKED_BY_OTHER_DEVICE;
            }
            if(!TextUtils.isEmpty(event)) {
                LiveDataBus.get().with(DemoConstant.ACCOUNT_CHANGE).postValue(new EaseEvent(event, EaseEvent.TYPE.ACCOUNT));
                showToast(event);
            }
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
            messageChangeLiveData.with(DemoConstant.NOTIFY_CHANGE).postValue(event);

            showToast(context.getString(R.string.demo_group_listener_onInvitationReceived, inviter, groupName));
        }

        @Override
        public void onInvitationAccepted(String groupId, String invitee, String reason) {
            super.onInvitationAccepted(groupId, invitee, reason);
            //user accept your invitation
            String groupName = GroupHelper.getGroupName(groupId);

            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            msg.setGroupInviter(invitee);
            msg.setStatus(InviteMessage.InviteMessageStatus.GROUPINVITATION_ACCEPTED);
            msg.setType(MsgTypeManageEntity.msgType.NOTIFICATION);
            notifyNewInviteMessage(msg);
            EaseEvent event = EaseEvent.create(DemoConstant.NOTIFY_GROUP_INVITE_ACCEPTED, EaseEvent.TYPE.NOTIFY);
            messageChangeLiveData.with(DemoConstant.NOTIFY_CHANGE).postValue(event);

            showToast(context.getString(R.string.demo_group_listener_onInvitationAccepted, invitee));
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            super.onInvitationDeclined(groupId, invitee, reason);
            //user declined your invitation
            String groupName = GroupHelper.getGroupName(groupId);
            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            msg.setGroupInviter(invitee);
            msg.setStatus(InviteMessage.InviteMessageStatus.GROUPINVITATION_DECLINED);
            msg.setType(MsgTypeManageEntity.msgType.NOTIFICATION);
            notifyNewInviteMessage(msg);
            EaseEvent event = EaseEvent.create(DemoConstant.NOTIFY_GROUP_INVITE_DECLINED, EaseEvent.TYPE.NOTIFY);
            messageChangeLiveData.with(DemoConstant.NOTIFY_CHANGE).postValue(event);

            showToast(context.getString(R.string.demo_system_other_decline_received_remote_user_invitation, invitee));
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            EaseEvent easeEvent = new EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP_LEAVE);
            easeEvent.message = groupId;
            messageChangeLiveData.with(DemoConstant.GROUP_CHANGE).postValue(easeEvent);

            showToast(context.getString(R.string.demo_group_listener_onUserRemoved, groupName));
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            EaseEvent easeEvent = new EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP_LEAVE);
            easeEvent.message = groupId;
            messageChangeLiveData.with(DemoConstant.GROUP_CHANGE).postValue(easeEvent);

            showToast(context.getString(R.string.demo_group_listener_onGroupDestroyed, groupName));
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
            messageChangeLiveData.with(DemoConstant.NOTIFY_CHANGE).postValue(event);

            showToast(context.getString(R.string.demo_group_listener_onRequestToJoinReceived, applicant, groupName));
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
            // notify the accept message
            getNotifier().vibrateAndPlayTone(msg);

            EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_GROUP_JOIN_ACCEPTED, EaseEvent.TYPE.MESSAGE);
            messageChangeLiveData.with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(event);

            showToast(context.getString(R.string.demo_group_listener_onRequestToJoinAccepted, accepter, groupName));
        }

        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            super.onRequestToJoinDeclined(groupId, groupName, decliner, reason);
            showToast(context.getString(R.string.demo_group_listener_onRequestToJoinDeclined, decliner, groupName));
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
            // notify invitation message
            getNotifier().vibrateAndPlayTone(msg);
            EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_GROUP_AUTO_ACCEPT, EaseEvent.TYPE.MESSAGE);
            messageChangeLiveData.with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(event);

            String groupName = GroupHelper.getGroupName(groupId);
            showToast(context.getString(R.string.demo_group_listener_onAutoAcceptInvitationFromGroup, groupName));
        }

        @Override
        public void onMuteListAdded(String groupId, List<String> mutes, long muteExpire) {
            super.onMuteListAdded(groupId, mutes, muteExpire);
            StringBuilder sb = new StringBuilder();
            for (String member : mutes) {
                sb.append(member).append(",");
            }
            showToast(context.getString(R.string.demo_group_listener_onMuteListAdded, sb.toString()));
        }

        @Override
        public void onMuteListRemoved(String groupId, List<String> mutes) {
            super.onMuteListRemoved(groupId, mutes);
            StringBuilder sb = new StringBuilder();
            for (String member : mutes) {
                sb.append(member).append(",");
            }
            showToast(context.getString(R.string.demo_group_listener_onMuteListRemoved, sb.toString()));
        }

        @Override
        public void onWhiteListAdded(String groupId, List<String> whitelist) {
            EaseEvent easeEvent = new EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP);
            easeEvent.message = groupId;
            messageChangeLiveData.with(DemoConstant.GROUP_CHANGE).postValue(easeEvent);

            StringBuilder sb = new StringBuilder();
            for (String member : whitelist) {
                sb.append(member).append(",");
            }
            showToast(context.getString(R.string.demo_group_listener_onWhiteListAdded, sb.toString()));
        }

        @Override
        public void onWhiteListRemoved(String groupId, List<String> whitelist) {
            EaseEvent easeEvent = new EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP);
            easeEvent.message = groupId;
            messageChangeLiveData.with(DemoConstant.GROUP_CHANGE).postValue(easeEvent);

            StringBuilder sb = new StringBuilder();
            for (String member : whitelist) {
                sb.append(member).append(",");
            }
            showToast(context.getString(R.string.demo_group_listener_onWhiteListRemoved, sb.toString()));
        }

        @Override
        public void onAllMemberMuteStateChanged(String groupId, boolean isMuted) {
            EaseEvent easeEvent = new EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP);
            easeEvent.message = groupId;
            messageChangeLiveData.with(DemoConstant.GROUP_CHANGE).postValue(easeEvent);

            showToast(context.getString(isMuted ? R.string.demo_group_listener_onAllMemberMuteStateChanged_mute
                    : R.string.demo_group_listener_onAllMemberMuteStateChanged_not_mute));
        }

        @Override
        public void onAdminAdded(String groupId, String administrator) {
            super.onAdminAdded(groupId, administrator);
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onAdminAdded, administrator));
        }

        @Override
        public void onAdminRemoved(String groupId, String administrator) {
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onAdminRemoved, administrator));
        }

        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_OWNER_TRANSFER, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onOwnerChanged, oldOwner, newOwner));
        }

        @Override
        public void onMemberJoined(String groupId, String member) {
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onMemberJoined, member));
        }

        @Override
        public void onMemberExited(String groupId, String member) {
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onMemberExited, member));
        }

        @Override
        public void onAnnouncementChanged(String groupId, String announcement) {
            showToast(context.getString(R.string.demo_group_listener_onAnnouncementChanged));
        }

        @Override
        public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {
            LiveDataBus.get().with(DemoConstant.GROUP_SHARE_FILE_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_SHARE_FILE_CHANGE, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onSharedFileAdded, sharedFile.getFileName()));
        }

        @Override
        public void onSharedFileDeleted(String groupId, String fileId) {
            LiveDataBus.get().with(DemoConstant.GROUP_SHARE_FILE_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_SHARE_FILE_CHANGE, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onSharedFileDeleted, fileId));
        }
    }

    private class ChatContactListener implements EMContactListener {

        @Override
        public void onContactAdded(String username) {
            EMLog.i("ChatContactListener", "onContactAdded");
            EmUserEntity entity = new EmUserEntity();
            entity.setUsername(username);
            DemoDbHelper.getInstance(DemoApplication.getInstance()).getUserDao().insert(entity);
            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
            messageChangeLiveData.with(DemoConstant.CONTACT_CHANGE).postValue(event);

            showToast(context.getString(R.string.demo_contact_listener_onContactAdded, username));
        }

        @Override
        public void onContactDeleted(String username) {
            EMLog.i("ChatContactListener", "onContactDeleted");
            DemoDbHelper helper = DemoDbHelper.getInstance(DemoApplication.getInstance());
            helper.getUserDao().deleteUser(username);
            helper.getInviteMessageDao().deleteByFrom(username);
            EMClient.getInstance().chatManager().deleteConversation(username, false);
            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
            messageChangeLiveData.with(DemoConstant.CONTACT_CHANGE).postValue(event);

            showToast(context.getString(R.string.demo_contact_listener_onContactDeleted, username));
        }

        @Override
        public void onContactInvited(String username, String reason) {
            EMLog.i("ChatContactListener", "onContactInvited");
            InviteMessageDao dao = DemoDbHelper.getInstance(DemoApplication.getInstance()).getInviteMessageDao();
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
            messageChangeLiveData.with(DemoConstant.CONTACT_CHANGE).postValue(event);

            showToast(context.getString(R.string.demo_contact_listener_onContactInvited, username));
        }

        @Override
        public void onFriendRequestAccepted(String username) {
            EMLog.i("ChatContactListener", "onFriendRequestAccepted");
            InviteMessageDao dao = DemoDbHelper.getInstance(DemoApplication.getInstance()).getInviteMessageDao();
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
            messageChangeLiveData.with(DemoConstant.CONTACT_CHANGE).postValue(event);

            showToast(context.getString(R.string.demo_contact_listener_onFriendRequestAccepted));
        }

        @Override
        public void onFriendRequestDeclined(String username) {
            EMLog.i("ChatContactListener", "onFriendRequestDeclined");
            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
            messageChangeLiveData.with(DemoConstant.CONTACT_CHANGE).postValue(event);
            showToast(context.getString(R.string.demo_contact_listener_onFriendRequestDeclined, username));
        }
    }

    private class ChatMultiDeviceListener implements EMMultiDeviceListener {

        @Override
        public void onContactEvent(int event, String target, String ext) {
            EMLog.i("ChatMultiDeviceListener", "onContactEvent event"+event);
            DemoDbHelper dbHelper = DemoDbHelper.getInstance(DemoApplication.getInstance());
            String message = null;
            switch (event) {
                case CONTACT_REMOVE: //好友已经在其他机子上被移除
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_REMOVE");
                    message = DemoConstant.CONTACT_REMOVE;
                    dbHelper.getUserDao().deleteUser(target);
                    dbHelper.getInviteMessageDao().deleteByFrom(target);
                    // TODO: 2020/1/16 0016 确认此处逻辑，是否是删除当前的target
                    DemoHelper.getInstance().getChatManager().deleteConversation(target, false);

                    showToast("CONTACT_REMOVE");
                    break;
                case CONTACT_ACCEPT: //好友请求已经在其他机子上被同意
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_ACCEPT");
                    message = DemoConstant.CONTACT_ACCEPT;
                    EmUserEntity  entity = new EmUserEntity();
                    entity.setUsername(target);
                    dbHelper.getUserDao().insert(entity);
                    updateContactNotificationStatus(target, "", InviteMessage.InviteMessageStatus.MULTI_DEVICE_CONTACT_ACCEPT);

                    showToast("CONTACT_ACCEPT");
                    break;
                case CONTACT_DECLINE: //好友请求已经在其他机子上被拒绝
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_DECLINE");
                    message = DemoConstant.CONTACT_DECLINE;
                    updateContactNotificationStatus(target, "", InviteMessage.InviteMessageStatus.MULTI_DEVICE_CONTACT_DECLINE);

                    showToast("CONTACT_DECLINE");
                    break;
                case CONTACT_BAN: //当前用户在其他设备加某人进入黑名单
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_BAN");
                    message = DemoConstant.CONTACT_BAN;
                    dbHelper.getUserDao().deleteUser(target);
                    dbHelper.getInviteMessageDao().deleteByFrom(target);
                    DemoHelper.getInstance().getChatManager().deleteConversation(target, false);
                    updateContactNotificationStatus(target, "", InviteMessage.InviteMessageStatus.MULTI_DEVICE_CONTACT_BAN);

                    showToast("CONTACT_BAN");
                    break;
                case CONTACT_ALLOW: // 好友在其他设备被移出黑名单
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_ALLOW");
                    message = DemoConstant.CONTACT_ALLOW;
                    updateContactNotificationStatus(target, "", InviteMessage.InviteMessageStatus.MULTI_DEVICE_CONTACT_ALLOW);

                    showToast("CONTACT_ALLOW");
                    break;
            }
            if(!TextUtils.isEmpty(message)) {
                EaseEvent easeEvent = EaseEvent.create(message, EaseEvent.TYPE.CONTACT);
                messageChangeLiveData.with(message).postValue(easeEvent);
            }
        }

        @Override
        public void onGroupEvent(int event, String groupId, List<String> usernames) {
            EMLog.i("ChatMultiDeviceListener", "onGroupEvent event"+event);
            InviteMessageDao messageDao = DemoDbHelper.getInstance(DemoApplication.getInstance()).getInviteMessageDao();
            String message = null;
            switch (event) {
                case GROUP_CREATE:
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_CREATE);

                    showToast("GROUP_CREATE");
                    break;
                case GROUP_DESTROY:
                    messageDao.deleteByGroupId(groupId);
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_DESTROY);
                    message = DemoConstant.GROUP_CHANGE;

                    showToast("GROUP_DESTROY");
                    break;
                case GROUP_JOIN:
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_JOIN);
                    message = DemoConstant.GROUP_CHANGE;

                    showToast("GROUP_JOIN");
                    break;
                case GROUP_LEAVE:
                    messageDao.deleteByGroupId(groupId);
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_LEAVE);
                    message = DemoConstant.GROUP_CHANGE;

                    showToast("GROUP_LEAVE");
                    break;
                case GROUP_APPLY:
                    messageDao.deleteByGroupId(groupId);
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY);

                    showToast("GROUP_APPLY");
                    break;
                case GROUP_APPLY_ACCEPT:
                    messageDao.deleteByGroupId(groupId, usernames.get(0));
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY_ACCEPT);

                    showToast("GROUP_APPLY_ACCEPT");
                    break;
                case GROUP_APPLY_DECLINE:
                    messageDao.deleteByGroupId(groupId, usernames.get(0));
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY_DECLINE);

                    showToast("GROUP_APPLY_DECLINE");
                    break;
                case GROUP_INVITE:
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE);

                    showToast("GROUP_INVITE");
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

                    showToast("GROUP_INVITE_ACCEPT");
                    break;
                case GROUP_INVITE_DECLINE:
                    messageDao.deleteByGroupId(groupId);
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_DECLINE);

                    showToast("GROUP_INVITE_DECLINE");
                    break;
                case GROUP_KICK:
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_DECLINE);

                    showToast("GROUP_KICK");
                    break;
                case GROUP_BAN:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_BAN);

                    showToast("GROUP_BAN");
                    break;
                case GROUP_ALLOW:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ALLOW);

                    showToast("GROUP_ALLOW");
                    break;
                case GROUP_BLOCK:
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_BLOCK);

                    showToast("GROUP_BLOCK");
                    break;
                case GROUP_UNBLOCK:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_UNBLOCK);

                    showToast("GROUP_UNBLOCK");
                    break;
                case GROUP_ASSIGN_OWNER:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ASSIGN_OWNER);

                    showToast("GROUP_ASSIGN_OWNER");
                    break;
                case GROUP_ADD_ADMIN:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ADD_ADMIN);

                    showToast("GROUP_ADD_ADMIN");
                    break;
                case GROUP_REMOVE_ADMIN:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_REMOVE_ADMIN);

                    showToast("GROUP_REMOVE_ADMIN");
                    break;
                case GROUP_ADD_MUTE:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ADD_MUTE);

                    showToast("GROUP_ADD_MUTE");
                    break;
                case GROUP_REMOVE_MUTE:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_REMOVE_MUTE);

                    showToast("GROUP_REMOVE_MUTE");
                    break;
                default:
                    break;
            }
            if(!TextUtils.isEmpty(message)) {
                EaseEvent easeEvent = EaseEvent.create(message, EaseEvent.TYPE.GROUP);
                messageChangeLiveData.with(message).postValue(easeEvent);
            }
        }
    }

    private void notifyNewInviteMessage(InviteMessage msg) {
        msg.setUnread(true);
        DemoHelper.getInstance().insert(msg);
        // notify there is new message
        getNotifier().vibrateAndPlayTone(null);
    }

    private void updateContactNotificationStatus(String from, String reason, InviteMessage.InviteMessageStatus status) {
        InviteMessage msg = null;
        InviteMessageDao dao = DemoDbHelper.getInstance(DemoApplication.getInstance()).getInviteMessageDao();
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
            showToast(context.getString(R.string.demo_chat_room_listener_onChatRoomDestroyed, roomName));
        }

        @Override
        public void onMemberJoined(String roomId, String participant) {
            setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM);
            showToast(context.getString(R.string.demo_chat_room_listener_onMemberJoined, participant));
        }

        @Override
        public void onMemberExited(String roomId, String roomName, String participant) {
            setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM);
            showToast(context.getString(R.string.demo_chat_room_listener_onMemberExited, participant));
        }

        @Override
        public void onRemovedFromChatRoom(int reason, String roomId, String roomName, String participant) {
            if(TextUtils.equals(DemoHelper.getInstance().getCurrentUser(), participant)) {
                setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM);
                if(reason == EMAChatRoomManagerListener.BE_KICKED) {
                    showToast(R.string.quiting_the_chat_room);
                }else {
                    showToast(context.getString(R.string.demo_chat_room_listener_onRemovedFromChatRoom, participant));
                }

            }
        }

        @Override
        public void onMuteListAdded(String chatRoomId, List<String> mutes, long expireTime) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);

            StringBuilder sb = new StringBuilder();
            for (String member : mutes) {
                sb.append(member).append(",");
            }
            showToast(context.getString(R.string.demo_chat_room_listener_onMuteListAdded, sb.toString()));
        }

        @Override
        public void onMuteListRemoved(String chatRoomId, List<String> mutes) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);
            StringBuilder sb = new StringBuilder();
            for (String member : mutes) {
                sb.append(member).append(",");
            }
            showToast(context.getString(R.string.demo_chat_room_listener_onMuteListRemoved, sb.toString()));
        }

        @Override
        public void onWhiteListAdded(String chatRoomId, List<String> whitelist) {
            StringBuilder sb = new StringBuilder();
            for (String member : whitelist) {
                sb.append(member).append(",");
            }
            showToast(context.getString(R.string.demo_chat_room_listener_onWhiteListAdded, sb.toString()));
        }

        @Override
        public void onWhiteListRemoved(String chatRoomId, List<String> whitelist) {
            StringBuilder sb = new StringBuilder();
            for (String member : whitelist) {
                sb.append(member).append(",");
            }
            showToast(context.getString(R.string.demo_chat_room_listener_onWhiteListRemoved, sb.toString()));
        }

        @Override
        public void onAllMemberMuteStateChanged(String chatRoomId, boolean isMuted) {
            showToast(context.getString(isMuted ? R.string.demo_chat_room_listener_onAllMemberMuteStateChanged_mute
                    : R.string.demo_chat_room_listener_onAllMemberMuteStateChanged_note_mute));
        }

        @Override
        public void onAdminAdded(String chatRoomId, String admin) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);

            showToast(context.getString(R.string.demo_chat_room_listener_onAdminAdded, admin));
        }

        @Override
        public void onAdminRemoved(String chatRoomId, String admin) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);

            showToast(context.getString(R.string.demo_chat_room_listener_onAdminRemoved, admin));
        }

        @Override
        public void onOwnerChanged(String chatRoomId, String newOwner, String oldOwner) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);

            showToast(context.getString(R.string.demo_chat_room_listener_onOwnerChanged, oldOwner, newOwner));
        }

        @Override
        public void onAnnouncementChanged(String chatRoomId, String announcement) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);
            showToast(context.getString(R.string.demo_chat_room_listener_onAnnouncementChanged));
        }
    }

    private void setChatRoomEvent(String roomId, EaseEvent.TYPE type) {
        EaseEvent easeEvent = new EaseEvent(DemoConstant.CHAT_ROOM_CHANGE, type);
        easeEvent.message = roomId;
        messageChangeLiveData.with(DemoConstant.CHAT_ROOM_CHANGE).postValue(easeEvent);
    }
}
