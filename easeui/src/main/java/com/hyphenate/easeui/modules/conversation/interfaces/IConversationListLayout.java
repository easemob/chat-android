package com.hyphenate.easeui.modules.conversation.interfaces;

import android.view.View;

import com.hyphenate.easeui.modules.conversation.EaseConversationListAdapter;
import com.hyphenate.easeui.modules.conversation.EaseConversationPresenter;
import com.hyphenate.easeui.modules.conversation.delegate.EaseBaseConversationDelegate;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
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

    /**
     * 设置presenter
     * @param presenter
     */
    void setPresenter(EaseConversationPresenter presenter);

    /**
     * 是否展示默认的条目菜单
     * @param showDefault
     */
    void showItemDefaultMenu(boolean showDefault);

    /**
     * 获取数据适配器
     * @return
     */
    EaseConversationListAdapter getListAdapter();

    /**
     * 获取条目数据
     * @param position
     * @return
     */
    EaseConversationInfo getItem(int position);
}
