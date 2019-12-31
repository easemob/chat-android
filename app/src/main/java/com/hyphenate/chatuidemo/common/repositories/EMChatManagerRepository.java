package com.hyphenate.chatuidemo.common.repositories;

import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.chatuidemo.common.net.ErrorCode;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.easeui.utils.EaseCommonUtils;

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
    public LiveData<Resource<List<EMConversation>>> loadConversationList() {
        return new NetworkOnlyResource<List<EMConversation>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<EMConversation>>> callBack) {
                List<EMConversation> emConversations = loadConversationListFromCache();
                callBack.onSuccess(new MutableLiveData<>(emConversations));
            }

        }.asLiveData();
    }

    /**
     * load conversation list
     *
     * @return
    +    */
    protected List<EMConversation> loadConversationListFromCache(){
        // get all conversations
        Map<String, EMConversation> conversations = getChatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        List<Pair<Long, EMConversation>> topSortList = new ArrayList<Pair<Long, EMConversation>>();
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
                        sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
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
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

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
}
