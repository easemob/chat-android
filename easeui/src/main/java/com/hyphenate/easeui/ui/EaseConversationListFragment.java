package com.hyphenate.easeui.ui;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseAdapterDelegate;
import com.hyphenate.easeui.adapter.EaseConversationListAdapter;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.hyphenate.easeui.delegate.ConversationDelegate;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 会话列表，展示了基本的会话列表逻辑。如果需要添加系统消息等类型，可重写{@link #addDelegate()}，
 * 调用{@link #listAdapter}的{@link EaseConversationListAdapter#addDelegate(EaseAdapterDelegate)}
 * 方法添加相应的类型即可。
 */
public class EaseConversationListFragment extends EaseBaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener {
    protected ViewStub viewStub;
    protected SwipeRefreshLayout srlRefresh;
    protected EaseRecyclerView rvConversationList;
    protected EaseConversationListAdapter listAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public int getLayoutId() {
        return R.layout.ease_fragment_conversation_list;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.ease_conversation_list_menu, menu);
        if(menuInfo instanceof EaseRecyclerView.RecyclerViewContextMenuInfo) {
            int position = ((EaseRecyclerView.RecyclerViewContextMenuInfo) menuInfo).position;
            Object item = listAdapter.getItem(position);
            if(item instanceof EMConversation) {
                String extField = ((EMConversation)item).getExtField();
                if(!TextUtils.isEmpty(extField) && EaseCommonUtils.isTimestamp(extField)) {
                    // 含有时间戳
                    menu.findItem(R.id.action_cancel_top).setVisible(true);
                    menu.findItem(R.id.action_make_top).setVisible(false);
                }
                //如果有未读消息则显示“置为已读”
                menu.findItem(R.id.action_make_read).setVisible(((EMConversation) item).getUnreadMsgCount() > 0);
            }
            onChildCreateContextMenu(menu, v, menuInfo, item);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        EaseRecyclerView.RecyclerViewContextMenuInfo info = (EaseRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Object object = null;
        if(position >= 0) {
            object = listAdapter.getItem(position);
        }
        if(object != null) {
            if(object instanceof EMConversation) {
                EMConversation conversation = (EMConversation) object;
                int itemId = item.getItemId();
                if(itemId == R.id.action_make_top) {
                    conversation.setExtField(System.currentTimeMillis()+"");
                    refreshList();
                }else if(itemId == R.id.action_cancel_top) {
                    conversation.setExtField("");
                    refreshList();
                }else if(itemId == R.id.action_delete) {
                    deleteConversation(conversation.conversationId());
                }else if(itemId == R.id.action_make_read) {
                    makeConversationRead(conversation);
                }
            }
            onChildContextItemSelected(item, object);
        }
        return super.onContextItemSelected(item);
    }

    public void initView(Bundle savedInstanceState) {
        viewStub = findViewById(R.id.view_stub);
        srlRefresh = findViewById(R.id.srl_refresh);
        rvConversationList = findViewById(R.id.rv_conversation_list);

        // 注册快捷菜单
        registerForContextMenu(rvConversationList);

        listAdapter = new EaseConversationListAdapter();
        addDelegate();
        rvConversationList.setLayoutManager(new LinearLayoutManager(mContext));
        rvConversationList.setAdapter(listAdapter);
    }

    /**
     * 添加代理类
     */
    public void addDelegate() {
        listAdapter.addDelegate(new ConversationDelegate());//添加会话消息
    }

    public void initListener() {
        srlRefresh.setOnRefreshListener(this);
        listAdapter.setOnItemClickListener(this);
    }

    public void initData() {
        refreshList();
    }

    /**
     * 刷新列表
     */
    public void refreshList() {
        loadConversationList();
    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    public void finishRefresh() {
        if(srlRefresh != null) {
            srlRefresh.setRefreshing(false);
        }
    }

    /**
     * 将会话置为已读
     * @param conversation
     */
    public void makeConversationRead(EMConversation conversation) {
        conversation.markAllMessagesAsRead();
        refreshList();
    }

    public void deleteConversation(String conversationId) {
        boolean isDelete = EMClient.getInstance().chatManager().deleteConversation(conversationId, true);
        if(isDelete) {
            refreshList();
        }else {
            Toast.makeText(mContext, getString(R.string.ease_delete_conversation_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadConversationList() {
        List<Object> list = loadConversationListFromCache();
        finishRefresh();
        listAdapter.setData(list);
    }

    /**
     * load conversation list
     *
     * @return
    +    */
    private List<Object> loadConversationListFromCache(){
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
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

    /**
     * 方便子类使用
     * @param menu
     * @param v
     * @param menuInfo
     * @param item
     */
    public void onChildCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, Object item) {

    }

    /**
     * 方便子类使用
     * @param menuItem
     * @param item
     */
    public void onChildContextItemSelected(MenuItem menuItem, Object item) {

    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
