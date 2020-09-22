package com.hyphenate.easeui.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseContactListAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnEaseCallBack;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.manager.SidebarPresenter;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 联系人列表，提供最基本的联系人展示
 */
public class EaseContactListFragment extends EaseBaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener {
    public ViewStub viewStub;
    public SwipeRefreshLayout srlContactRefresh;
    public EaseRecyclerView rvContactList;
    public EaseSidebar sideBarFriend;
    public TextView floatingHeader;
    public EaseContactListAdapter adapter;
    public SidebarPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), null);
    }

    public int getLayoutId() {
        return R.layout.ease_fragment_contact_list;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initArgument();
        initView(savedInstanceState);
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(1, R.id.action_friend_delete, 1, getString(R.string.ease_friends_delete_the_contact));
        onChildCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = ((EaseRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo()).position - rvContactList.getHeadersCount();
        EaseUser user = adapter.getItem(position);
        if(item.getItemId() == R.id.action_friend_delete) {
            showDeleteDialog(user);
        }
        onChildContextItemSelected(item, user);
        return super.onContextItemSelected(item);
    }

    public void initArgument() {}

    public void initView(Bundle savedInstanceState) {
        viewStub = findViewById(R.id.view_stub);
        srlContactRefresh = findViewById(R.id.srl_contact_refresh);
        rvContactList = findViewById(R.id.rv_contact_list);
        sideBarFriend = findViewById(R.id.side_bar_friend);
        floatingHeader = findViewById(R.id.floating_header);

        rvContactList.setHasFixedSize(true);
        rvContactList.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new EaseContactListAdapter();
        //通过官方提供的ConcatAdapter可以很便捷的添加头尾布局
        ConcatAdapter concatAdapter = new ConcatAdapter();
        addHeader(concatAdapter);
        concatAdapter.addAdapter(adapter);
        addFooter(concatAdapter);
        rvContactList.setAdapter(concatAdapter);

        presenter = new SidebarPresenter();
        presenter.setupWithRecyclerView(rvContactList, adapter, floatingHeader);

        //注册快捷菜单
        registerForContextMenu(rvContactList);
    }

    /**
     * 提供添加Header的方法
     * 利用google提供的ConcatAdapter提供的concatAdapter.addAdapter(headerAdapter)
     * @param concatAdapter
     */
    public void addHeader(ConcatAdapter concatAdapter) {}

    /**
     * 提供添加Footer的方法
     * 利用google提供的ConcatAdapter提供的concatAdapter.addAdapter(footerAdapter)
     * @param concatAdapter
     */
    public void addFooter(ConcatAdapter concatAdapter) {}

    public void initListener() {
        srlContactRefresh.setOnRefreshListener(this);
        sideBarFriend.setOnTouchEventListener(presenter);
        adapter.setOnItemClickListener(this);
    }

    public void initData() {
        if(srlContactRefresh != null && !srlContactRefresh.isRefreshing()) {
            srlContactRefresh.setRefreshing(true);
        }
        refreshContactList();
    }

    @Override
    public void onRefresh() {
        refreshContactList();
    }

    public void refreshContactList() {
        getContactList(new OnEaseCallBack<List<EaseUser>>() {
            @Override
            public void onSuccess(List<EaseUser> models) {
                adapter.setData(models);
                finishRefresh();
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                runOnUiThread(()-> finishRefresh());
            }
        });
    }

    private void getContactList(@NonNull OnEaseCallBack<List<EaseUser>> callBack) {
        new Thread(()-> {
            try {
                List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                List<String> ids = EMClient.getInstance().contactManager().getSelfIdsOnOtherPlatform();
                if(usernames == null) {
                    usernames = new ArrayList<>();
                }
                if(ids != null && !ids.isEmpty()) {
                    usernames.addAll(ids);
                }
                List<EaseUser> easeUsers = EaseUser.parse(usernames);
                if(easeUsers != null && !easeUsers.isEmpty()) {
                    List<String> blackListFromServer = EMClient.getInstance().contactManager().getBlackListFromServer();
                    for (EaseUser user : easeUsers) {
                        if(blackListFromServer != null && !blackListFromServer.isEmpty()) {
                            if(blackListFromServer.contains(user.getUsername())) {
                                user.setContact(1);
                            }
                        }
                    }
                }
                sortData(easeUsers);
                runOnUiThread(()-> {
                    callBack.onSuccess(easeUsers);
                });


            } catch (HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(()-> {
                    callBack.onError(e.getErrorCode(), e.getDescription());
                });

            }
        }).start();
    }

    private void sortData(List<EaseUser> data) {
        if(data == null || data.isEmpty()) {
            return;
        }
        Collections.sort(data, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                    return lhs.getNickname().compareTo(rhs.getNickname());
                }else{
                    if("#".equals(lhs.getInitialLetter())){
                        return 1;
                    }else if("#".equals(rhs.getInitialLetter())){
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });
    }

    public void finishRefresh() {
        if(srlContactRefresh != null) {
            srlContactRefresh.setRefreshing(false);
        }
    }

    public void showDeleteDialog(EaseUser user) {
        new AlertDialog.Builder(mContext)
                    .setTitle(R.string.ease_friends_delete_contact_hint)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteContact(user);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
    }

    private void deleteContact(EaseUser user) {
        EMClient.getInstance().contactManager().asyncDeclineInvitation(user.getUsername(), new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(()->refreshContactList());
            }

            @Override
            public void onError(int code, String error) {
                runOnUiThread(()-> Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /**
     * 方便子类扩展
     * @param menu
     * @param v
     * @param menuInfo
     */
    public void onChildCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    /**
     *  方便子类扩展
     * @param item
     * @param user
     */
    public void onChildContextItemSelected(MenuItem item, EaseUser user) {

    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
