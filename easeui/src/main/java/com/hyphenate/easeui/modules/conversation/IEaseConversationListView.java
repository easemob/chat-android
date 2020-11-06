package com.hyphenate.easeui.modules.conversation;

import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.modules.ILoadDataView;

import java.util.List;

public interface IEaseConversationListView extends ILoadDataView {
    /**
     * 获取会话列表数据成功
     * @param data
     */
    void loadConversationListSuccess(List<EaseConversationInfo> data);

    /**
     * 没有获取到会话列表数据
     */
    void loadConversationListNoData();

    /**
     * 获取失败
     * @param message
     */
    void loadConversationListFail(String message);
}
