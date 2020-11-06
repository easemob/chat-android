package com.hyphenate.easeui.modules.conversation.interfaces;

import android.view.View;

import com.hyphenate.easeui.modules.conversation.delegate.EaseBaseConversationDelegate;
import com.hyphenate.easeui.modules.interfaces.IRecyclerView;

public interface IConversationListLayout extends IRecyclerView {

    /**
     * 整个布局添加头布局
     * @param top
     */
    void addTopView(View top);

    /**
     * 整个布局添加尾布局
     * @param bottom
     */
    void addBottomView(View bottom);

    /**
     * 添加其他类型的代理类
     * @param delegate
     */
    void addDelegate(EaseBaseConversationDelegate delegate);

}
