package com.hyphenate.easeui.modules.interfaces;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easeui.modules.conversation.EaseConversationListLayout;

public interface IRecyclerView {

    /**
     * 添加头部adapter
     * @param adapter
     */
    void addHeaderAdapter(RecyclerView.Adapter adapter);

    /**
     * 添加尾部adapter
     * @param adapter
     */
    void addFooterAdapter(RecyclerView.Adapter adapter);

    /**
     * 移除adapter
     * @param adapter
     */
    void removeAdapter(RecyclerView.Adapter adapter);

    /**
     * 添加装饰类
     * @param decor
     */
    void addItemDecoration(@NonNull RecyclerView.ItemDecoration decor);

    /**
     * 移除装饰类
     * @param decor
     */
    void removeItemDecoration(@NonNull RecyclerView.ItemDecoration decor);

    /**
     * 设置条目点击事件
     */
    void setOnItemClickListener(EaseConversationListLayout.OnItemClickListener listener);

    /**
     * 设置条目长按事件
     */
    void setOnItemLongClickListener(EaseConversationListLayout.OnItemLongClickListener listener);
}

