package com.hyphenate.easeui.modules.conversation;

import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.modules.EaseBasePresenter;
import com.hyphenate.easeui.modules.ILoadDataView;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class EaseConversationPresenterImpl extends EaseBasePresenter {
    private IEaseConversationListView mView;

    @Override
    public void attachView(ILoadDataView view) {
        mView = (IEaseConversationListView) view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detachView();
    }

    /**
     * 注意：默认conversation中设置extField值为时间戳后，是将该会话置顶
     * 如果有不同的逻辑，请自己实现，并调用{@link #loadData(List)}方法即可
     */
    public void loadDefaultData() {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        if(conversations.isEmpty()) {
            EaseThreadManager.getInstance().runOnMainThread(() -> {
                if(!isDestroy()) {
                    mView.loadConversationListNoData();
                }
            });
            return;
        }
        List<EaseConversationInfo> infos = new ArrayList<>();
        synchronized (this) {
            EaseConversationInfo info = null;
            for (EMConversation conversation : conversations.values()) {
                if(conversation.getAllMessages().size() != 0) {
                    info = new EaseConversationInfo();
                    info.setInfo(conversation);
                    String extField = conversation.getExtField();
                    if(!TextUtils.isEmpty(extField) && EaseCommonUtils.isTimestamp(extField)) {
                        info.setTimestamp(Long.parseLong(extField));
                        info.setTop(true);
                    }else {
                        info.setTimestamp(conversation.getLastMessage().getMsgTime());
                    }
                    infos.add(info);
                }
            }
        }
        loadData(infos);
    }

    /**
     * 排序数据
     * @param data
     */
    public void loadData(List<EaseConversationInfo> data) {
        if(data == null || data.isEmpty()) {
            EaseThreadManager.getInstance().runOnMainThread(() -> {
                if(!isDestroy()) {
                    mView.loadConversationListNoData();
                }

            });
            return;
        }
        List<EaseConversationInfo> sortList = new ArrayList<>();
        List<EaseConversationInfo> topSortList = new ArrayList<>();
        synchronized (this) {
            for(EaseConversationInfo info : data) {
                if(info.isTop()) {
                    topSortList.add(info);
                }else {
                    sortList.add(info);
                }
            }
            sortByTimestamp(topSortList);
            sortByTimestamp(sortList);
            sortList.addAll(0, topSortList);
        }
        EaseThreadManager.getInstance().runOnMainThread(() -> {
            if(!isDestroy()) {
                mView.loadConversationListSuccess(sortList);
            }
        });
    }

    /**
     * 排序
     * @param list
     */
    private void sortByTimestamp(List<EaseConversationInfo> list) {
        if(list == null || list.isEmpty()) {
            return;
        }
        Collections.sort(list, new Comparator<EaseConversationInfo>() {
            @Override
            public int compare(EaseConversationInfo o1, EaseConversationInfo o2) {
                if(o2.getTimestamp() > o1.getTimestamp()) {
                    return 1;
                }else if(o2.getTimestamp() == o1.getTimestamp()) {
                    return 0;
                }else {
                    return -1;
                }
            }
        });
    }

}

