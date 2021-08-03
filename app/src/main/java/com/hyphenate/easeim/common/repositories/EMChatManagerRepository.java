package com.hyphenate.easeim.common.repositories;

import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeim.common.db.entity.InviteMessage;
import com.hyphenate.easeim.common.db.entity.MsgTypeManageEntity;
import com.hyphenate.easeim.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.easeim.common.net.ErrorCode;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 处理与chat相关的逻辑
 */
public class EMChatManagerRepository extends BaseEMRepository{

    /**
     * 获取会话列表
     * @return
     */
    public LiveData<Resource<List<Object>>> loadConversationList() {
        return new NetworkOnlyResource<List<Object>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<Object>>> callBack) {
                List<Object> emConversations = loadConversationListFromCache();
                callBack.onSuccess(new MutableLiveData<>(emConversations));
            }

        }.asLiveData();
    }

    /**
     * load conversation list
     *
     * @return
    +    */
    protected List<Object> loadConversationListFromCache(){
        // get all conversations
        Map<String, EMConversation> conversations = getChatManager().getAllConversations();
        List<Pair<Long, Object>> sortList = new ArrayList<Pair<Long, Object>>();
        List<Pair<Long, Object>> topSortList = new ArrayList<Pair<Long, Object>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    String extField = conversation.getExtField();
                    if(!TextUtils.isEmpty(extField) && EaseCommonUtils.isTimestamp(extField)) {
                        topSortList.add(new Pair<>(Long.valueOf(extField), conversation));
                    }else {
                        sortList.add(new Pair<Long, Object>(conversation.getLastMessage().getMsgTime(), conversation));
                    }
                }
            }
        }
        List<MsgTypeManageEntity> manageEntities = null;
        if(getMsgTypeManageDao() != null) {
            manageEntities = getMsgTypeManageDao().loadAllMsgTypeManage();
        }
        if(manageEntities != null && !manageEntities.isEmpty()) {
            synchronized (EMChatManagerRepository.class) {
                for (MsgTypeManageEntity manage : manageEntities) {
                    String extField = manage.getExtField();
                    if(!TextUtils.isEmpty(extField) && EaseCommonUtils.isTimestamp(extField)) {
                        topSortList.add(new Pair<>(Long.valueOf(extField), manage));
                    }else {
                        Object lastMsg = manage.getLastMsg();
                        if(lastMsg instanceof InviteMessage) {
                            long time = ((InviteMessage) lastMsg).getTime();
                            sortList.add(new Pair<>(time, manage));
                        }
                    }
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            if(topSortList.size() > 0) {
                sortConversationByLastChatTime(topSortList);
            }
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sortList.addAll(0, topSortList);
        List<Object> list = new ArrayList<Object>();
        for (Pair<Long, Object> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, Object>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, Object>>() {
            @Override
            public int compare(final Pair<Long, Object> con1, final Pair<Long, Object> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    public LiveData<Resource<Boolean>> deleteConversationById(String conversationId) {
        return new NetworkOnlyResource<Boolean>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                boolean isDelete = getChatManager().deleteConversation(conversationId, true);
                if(isDelete) {
                    callBack.onSuccess(new MutableLiveData<>(true));
                }else {
                    callBack.onError(ErrorCode.EM_DELETE_CONVERSATION_ERROR);
                }
            }

        }.asLiveData();
    }

    /**
     * 将会话置为已读
     * @param conversationId
     * @return
     */
    public LiveData<Resource<Boolean>> makeConversationRead(String conversationId) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                EMConversation conversation = getChatManager().getConversation(conversationId);
                if(conversation == null) {
                    callBack.onError(ErrorCode.EM_DELETE_CONVERSATION_ERROR);
                }else {
                    conversation.markAllMessagesAsRead();
                    callBack.onSuccess(createLiveData(true));
                }
            }
        }.asLiveData();
    }

    /**
     * 获取会话列表
     * @return
     */
    public LiveData<Resource<List<EaseConversationInfo>>> fetchConversationsFromServer() {
        return new NetworkOnlyResource<List<EaseConversationInfo>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<EaseConversationInfo>>> callBack) {
                EMClient.getInstance().chatManager().asyncFetchConversationsFromServer(new EMValueCallBack<Map<String, EMConversation>>() {
                    @Override
                    public void onSuccess(Map<String, EMConversation> value) {
                        List<EMConversation> conversations = new ArrayList<EMConversation>(value.values());
                        List<EaseConversationInfo> infoList = new ArrayList<>();
                        if(!conversations.isEmpty()) {
                            EaseConversationInfo info = null;
                            for(EMConversation conversation : conversations) {
                                info = new EaseConversationInfo();
                                info.setInfo(conversation);
                                info.setTimestamp(conversation.getLastMessage().getMsgTime());
                                infoList.add(info);
                            }
                        }
                        callBack.onSuccess(createLiveData(infoList));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }

        }.asLiveData();
    }


    /**
     * 调用api请求将会话置为已读
     * @param conversationId
     * @return
     */
    public LiveData<Resource<Boolean>> makeConversationReadByAck(String conversationId) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                runOnIOThread(()-> {
                    try {
                        getChatManager().ackConversationRead(conversationId);
                        callBack.onSuccess(createLiveData(true));
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getDescription());
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 设置单聊用户聊天免打扰
     *
     * @param userId 用户名
     * @param noPush 是否免打扰
     */
    public LiveData<Resource<Boolean>> setUserNotDisturb(String userId, boolean noPush) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                runOnIOThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> onPushList = new ArrayList<>();
                        onPushList.add(userId);
                        try {
                            getPushManager().updatePushServiceForUsers(onPushList, noPush);
                            callBack.onSuccess(createLiveData(true));
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            callBack.onError(e.getErrorCode(), e.getDescription());
                        }
                    }
                });

            }
        }.asLiveData();
    }

    /**
     * 获取聊天免打扰用户
     */
    public LiveData<Resource<List<String>>> getNoPushUsers() {
        return new NetworkOnlyResource<List<String>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<String>>> callBack) {
                runOnIOThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> noPushUsers = getPushManager().getNoPushUsers();
                        if (noPushUsers != null && noPushUsers.size() != 0) {
                            callBack.onSuccess(createLiveData(noPushUsers));
                        }
                    }
                });

            }
        }.asLiveData();
    }

}
