package com.hyphenate.easeim.section.message.viewmodels;

import android.app.Application;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.db.entity.InviteMessageStatus;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.livedatas.SingleSourceLiveData;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.manager.EaseSystemMsgManager;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.exceptions.HyphenateException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NewFriendsViewModel extends AndroidViewModel {
    private SingleSourceLiveData<List<EMMessage>> inviteMsgObservable;
    private SingleSourceLiveData<List<EMMessage>> moreInviteMsgObservable;
    private MutableLiveData<Resource<Boolean>> resultObservable;
    private MutableLiveData<Resource<String>> agreeObservable;
    private MutableLiveData<Resource<String>> refuseObservable;
    private LiveDataBus messageChangeObservable = LiveDataBus.get();

    public NewFriendsViewModel(@NonNull Application application) {
        super(application);
        inviteMsgObservable = new SingleSourceLiveData<>();
        moreInviteMsgObservable = new SingleSourceLiveData<>();
        resultObservable = new MutableLiveData<>();
        agreeObservable = new MutableLiveData<>();
        refuseObservable = new MutableLiveData<>();
    }

    public LiveDataBus messageChangeObservable() {
        return messageChangeObservable;
    }

    public LiveData<List<EMMessage>> inviteMsgObservable() {
        return inviteMsgObservable;
    }

    public LiveData<List<EMMessage>> moreInviteMsgObservable() {
        return moreInviteMsgObservable;
    }

    public void loadMessages(int limit) {
        List<EMMessage> emMessages = EMClient.getInstance().chatManager().searchMsgFromDB(EMMessage.Type.TXT
                , System.currentTimeMillis(), limit, EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID, EMConversation.EMSearchDirection.UP);
        sortData(emMessages);
        inviteMsgObservable.setSource(new MutableLiveData<>(emMessages));
    }

    public void loadMoreMessages(String targetId, int limit) {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID, EMConversation.EMConversationType.Chat, true);
        List<EMMessage> messages = conversation.loadMoreMsgFromDB(targetId, limit);
        sortData(messages);
        moreInviteMsgObservable.setSource(new MutableLiveData<>(messages));
    }
    
    private void sortData(List<EMMessage> messages) {
        Collections.sort(messages, new Comparator<EMMessage>() {
            @Override
            public int compare(EMMessage o1, EMMessage o2) {
                long o1MsgTime = o1.getMsgTime();
                long o2MsgTime = o2.getMsgTime();
                return (int) (o2MsgTime - o1MsgTime);
            }
        });
    }

    public LiveData<Resource<Boolean>> resultObservable() {
        return resultObservable;
    }

    public LiveData<Resource<String>> agreeObservable() {
        return agreeObservable;
    }
    public LiveData<Resource<String>> refuseObservable() {
        return refuseObservable;
    }

    public void agreeInvite(EMMessage msg) {
        EaseThreadManager.getInstance().runOnIOThread(() -> {
            try {
                String statusParams = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS);
                InviteMessageStatus status = InviteMessageStatus.valueOf(statusParams);
                String message = "";
                if (status == InviteMessageStatus.BEINVITEED) {//accept be friends
                    message = getApplication().getString(R.string.demo_system_agree_invite, msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM));
                    EMClient.getInstance().contactManager().acceptInvitation(msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM));
                } else if (status == InviteMessageStatus.BEAPPLYED) { //accept application to join group
                    message = getApplication().getString(R.string.demo_system_agree_remote_user_apply_to_join_group, msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM));
                    EMClient.getInstance().groupManager().acceptApplication(msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM), msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_GROUP_ID));
                } else if (status == InviteMessageStatus.GROUPINVITATION) {
                    message = getApplication().getString(R.string.demo_system_agree_received_remote_user_invitation, msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_INVITER));
                    EMClient.getInstance().groupManager().acceptInvitation(msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_GROUP_ID)
                            , msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_INVITER));
                }
                msg.setAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.AGREED.name());
                msg.setAttribute(DemoConstant.SYSTEM_MESSAGE_REASON, message);
                EMTextMessageBody body = new EMTextMessageBody(message);
                msg.setBody(body);
                EaseSystemMsgManager.getInstance().updateMessage(msg);
                agreeObservable.postValue(Resource.success(message));
                messageChangeObservable.with(DemoConstant.NOTIFY_CHANGE).postValue(EaseEvent.create(DemoConstant.NOTIFY_CHANGE, EaseEvent.TYPE.NOTIFY));
            } catch (HyphenateException e) {
                e.printStackTrace();
                agreeObservable.postValue(Resource.error(e.getErrorCode(), e.getMessage(), ""));
            }
        });
    }

    public void refuseInvite(EMMessage msg) {
        EaseThreadManager.getInstance().runOnIOThread(() -> {
            try {
                String statusParams = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS);
                InviteMessageStatus status = InviteMessageStatus.valueOf(statusParams);
                String message = "";
                if (status == InviteMessageStatus.BEINVITEED) {//decline the invitation
                    message = getApplication().getString(R.string.demo_system_decline_invite, msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM));
                    EMClient.getInstance().contactManager().declineInvitation(msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM));
                } else if (status == InviteMessageStatus.BEAPPLYED) { //decline application to join group
                    message = getApplication().getString(R.string.demo_system_decline_remote_user_apply_to_join_group, msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM));
                    EMClient.getInstance().groupManager().declineApplication(msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM)
                            , msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_GROUP_ID), "");
                } else if (status == InviteMessageStatus.GROUPINVITATION) {
                    message = getApplication().getString(R.string.demo_system_decline_received_remote_user_invitation, msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_INVITER));
                    EMClient.getInstance().groupManager().declineInvitation(msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_GROUP_ID)
                            , msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_INVITER), "");
                }
                msg.setAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.REFUSED.name());
                msg.setAttribute(DemoConstant.SYSTEM_MESSAGE_REASON, message);
                EMTextMessageBody body = new EMTextMessageBody(message);
                msg.setBody(body);
                EaseSystemMsgManager.getInstance().updateMessage(msg);
                refuseObservable.postValue(Resource.success(message));
                messageChangeObservable.with(DemoConstant.NOTIFY_CHANGE).postValue(EaseEvent.create(DemoConstant.NOTIFY_CHANGE, EaseEvent.TYPE.NOTIFY));
            } catch (HyphenateException e) {
                e.printStackTrace();
                refuseObservable.postValue(Resource.error(e.getErrorCode(), e.getMessage(), ""));
            }
        });
    }

    public void deleteMsg(EMMessage message) {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(DemoConstant.DEFAULT_SYSTEM_MESSAGE_ID, EMConversation.EMConversationType.Chat, true);
        conversation.removeMessage(message.getMsgId());
        resultObservable.postValue(Resource.success(true));
    }

    public void makeAllMsgRead() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(DemoConstant.DEFAULT_SYSTEM_MESSAGE_ID, EMConversation.EMConversationType.Chat, true);
        conversation.markAllMessagesAsRead();
        messageChangeObservable.with(DemoConstant.NOTIFY_CHANGE).postValue(EaseEvent.create(DemoConstant.NOTIFY_CHANGE, EaseEvent.TYPE.NOTIFY));
    }
}
