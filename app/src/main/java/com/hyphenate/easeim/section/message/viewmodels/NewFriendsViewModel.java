package com.hyphenate.easeim.section.message.viewmodels;

import android.app.Application;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.db.DemoDbHelper;
import com.hyphenate.easeim.common.db.dao.InviteMessageDao;
import com.hyphenate.easeim.common.db.entity.InviteMessage;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.livedatas.SingleSourceLiveData;
import com.hyphenate.easeim.common.db.entity.InviteMessage.InviteMessageStatus;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NewFriendsViewModel extends AndroidViewModel {
    private InviteMessageDao messageDao;
    private SingleSourceLiveData<List<InviteMessage>> inviteMsgObservable;
    private SingleSourceLiveData<List<InviteMessage>> moreInviteMsgObservable;
    private MutableLiveData<Resource<Boolean>> resultObservable;
    private LiveDataBus messageChangeObservable = LiveDataBus.get();

    public NewFriendsViewModel(@NonNull Application application) {
        super(application);
        messageDao = DemoDbHelper.getInstance(application).getInviteMessageDao();
        inviteMsgObservable = new SingleSourceLiveData<>();
        moreInviteMsgObservable = new SingleSourceLiveData<>();
        resultObservable = new MutableLiveData<>();
    }

    public LiveDataBus messageChangeObservable() {
        return messageChangeObservable;
    }

    public LiveData<List<InviteMessage>> inviteMsgObservable() {
        return inviteMsgObservable;
    }

    public LiveData<List<InviteMessage>> moreInviteMsgObservable() {
        return moreInviteMsgObservable;
    }

    public void loadMessages(int limit) {
        inviteMsgObservable.setSource(messageDao.loadMessages(limit, 0));
    }

    public void loadMoreMessages(int limit, int offset) {
        moreInviteMsgObservable.setSource(messageDao.loadMessages(limit, offset));
    }

    public LiveData<Resource<Boolean>> resultObservable() {
        return resultObservable;
    }

    public void agreeInvite(InviteMessage msg) {
        EaseThreadManager.getInstance().runOnIOThread(() -> {
            try {
                if (msg.getStatusEnum() == InviteMessageStatus.BEINVITEED) {//accept be friends
                    EMClient.getInstance().contactManager().acceptInvitation(msg.getFrom());
                } else if (msg.getStatusEnum() == InviteMessageStatus.BEAPPLYED) { //accept application to join group
                    EMClient.getInstance().groupManager().acceptApplication(msg.getFrom(), msg.getGroupId());
                } else if (msg.getStatusEnum() == InviteMessageStatus.GROUPINVITATION) {
                    EMClient.getInstance().groupManager().acceptInvitation(msg.getGroupId(), msg.getGroupInviter());
                }
                msg.setStatus(InviteMessageStatus.AGREED);
                messageDao.update(msg);
                resultObservable.postValue(Resource.success(true));
                messageChangeObservable.with(DemoConstant.NOTIFY_CHANGE).postValue(EaseEvent.create(DemoConstant.NOTIFY_CHANGE, EaseEvent.TYPE.NOTIFY));
            } catch (HyphenateException e) {
                e.printStackTrace();
                resultObservable.postValue(Resource.error(e.getErrorCode(), e.getMessage(), false));
            }
        });
    }

    public void refuseInvite(InviteMessage msg) {
        EaseThreadManager.getInstance().runOnIOThread(() -> {
            try {
                if (msg.getStatusEnum() == InviteMessageStatus.BEINVITEED) {//decline the invitation
                    EMClient.getInstance().contactManager().declineInvitation(msg.getFrom());
                } else if (msg.getStatusEnum() == InviteMessageStatus.BEAPPLYED) { //decline application to join group
                    EMClient.getInstance().groupManager().declineApplication(msg.getFrom(), msg.getGroupId(), "");
                } else if (msg.getStatusEnum() == InviteMessageStatus.GROUPINVITATION) {
                    EMClient.getInstance().groupManager().declineInvitation(msg.getGroupId(), msg.getGroupInviter(), "");
                }
                msg.setStatus(InviteMessageStatus.REFUSED);
                messageDao.update(msg);
                resultObservable.postValue(Resource.success(true));
                messageChangeObservable.with(DemoConstant.NOTIFY_CHANGE).postValue(EaseEvent.create(DemoConstant.NOTIFY_CHANGE, EaseEvent.TYPE.NOTIFY));
            } catch (HyphenateException e) {
                e.printStackTrace();
                resultObservable.postValue(Resource.error(e.getErrorCode(), e.getMessage(), false));
            }
        });
    }

    public void deleteMsg(InviteMessage message) {
        messageDao.delete(message);
        resultObservable.postValue(Resource.success(true));
    }

    public void makeAllMsgRead() {
        messageDao.makeAllReaded();
        messageChangeObservable.with(DemoConstant.NOTIFY_CHANGE).postValue(EaseEvent.create(DemoConstant.NOTIFY_CHANGE, EaseEvent.TYPE.NOTIFY));
    }
}
