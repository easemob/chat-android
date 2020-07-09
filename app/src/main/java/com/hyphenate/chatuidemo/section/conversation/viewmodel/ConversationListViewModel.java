package com.hyphenate.chatuidemo.section.conversation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chatuidemo.DemoApplication;
import com.hyphenate.chatuidemo.common.db.DemoDbHelper;
import com.hyphenate.chatuidemo.common.db.entity.MsgTypeManageEntity;
import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.ErrorCode;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMChatManagerRepository;

import java.util.List;

public class ConversationListViewModel extends AndroidViewModel {
    private EMChatManagerRepository mRepository;

    private SingleSourceLiveData<Resource<List<Object>>> conversationObservable;
    private SingleSourceLiveData<Resource<Boolean>> deleteConversationObservable;
    private SingleSourceLiveData<Resource<Boolean>> readConversationObservable;

    public ConversationListViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMChatManagerRepository();
        conversationObservable = new SingleSourceLiveData<>();
        deleteConversationObservable = new SingleSourceLiveData<>();
        readConversationObservable = new SingleSourceLiveData<>();
    }

    /**
     * 获取聊天列表
     */
    public void loadConversationList() {
        conversationObservable.setSource(mRepository.loadConversationList());
    }

    public LiveData<Resource<List<Object>>> getConversationObservable() {
        return conversationObservable;
    }

    /**
     * 删除对话
     * @param conversationId
     */
    public void deleteConversationById(String conversationId) {
        deleteConversationObservable.setSource(mRepository.deleteConversationById(conversationId));
    }

    public LiveData<Resource<Boolean>> getDeleteObservable() {
        return deleteConversationObservable;
    }

    /**
     * 将会话置为已读
     * @param conversationId
     */
    public void makeConversationRead(String conversationId) {
        readConversationObservable.setSource(mRepository.makeConversationRead(conversationId));
    }

    public LiveData<Resource<Boolean>> getReadObservable() {
        return readConversationObservable;
    }

    /**
     * 删除系统消息
     * @param msg
     */
    public void deleteSystemMsg(MsgTypeManageEntity msg) {
        try {
            DemoDbHelper dbHelper = DemoDbHelper.getInstance(DemoApplication.getInstance());
            dbHelper.getInviteMessageDao().delete("type", msg.getType());
            dbHelper.getMsgTypeManageDao().delete(msg);
            deleteConversationObservable.postValue(Resource.success(true));
        } catch (Exception e) {
            e.printStackTrace();
            deleteConversationObservable.postValue(Resource.error(ErrorCode.EM_DELETE_SYS_MSG_ERROR, e.getMessage(), null));
        }
    }
}
