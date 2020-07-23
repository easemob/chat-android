package com.hyphenate.chatuidemo.section.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.livedatas.LiveDataBus;
import com.hyphenate.easeui.manager.SidebarPresenter;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.dialog.DemoDialogFragment;
import com.hyphenate.chatuidemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.chatuidemo.section.contact.activity.ContactDetailActivity;
import com.hyphenate.chatuidemo.section.group.adapter.GroupMemberAuthorityAdapter;
import com.hyphenate.chatuidemo.section.group.viewmodels.GroupMemberAuthorityViewModel;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.interfaces.OnItemLongClickListener;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class GroupMemberAuthorityActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, OnItemClickListener, OnRefreshListener, OnItemLongClickListener {
    private static final int REQUEST_CODE_ADD_USER = 0;
    protected static final int TYPE_MEMBER = 0;
    protected static final int TYPE_BLACK = 1;
    protected static final int TYPE_MUTE = 2;
    protected static final int TYPE_TRANSFER = 3;
    protected EaseTitleBar titleBar;
    private SmartRefreshLayout srlRefresh;
    private EaseRecyclerView rvList;
    private EaseSidebar sidebar;
    private TextView floatingHeader;

    private SidebarPresenter presenter;
    protected GroupMemberAuthorityAdapter adapter;
    protected GroupMemberAuthorityViewModel viewModel;
    protected String groupId;
    protected List<String> muteMembers = new ArrayList<>();
    protected List<String> blackMembers = new ArrayList<>();
    protected int flag;//作为切换的flag
    public EMGroup group;

    public static void actionStart(Context context, String groupId) {
        Intent starter = new Intent(context, GroupMemberAuthorityActivity.class);
        starter.putExtra("groupId", groupId);
        context.startActivity(starter);
    }

    public static void actionStart(Context context, String groupId, int type) {
        Intent starter = new Intent(context, GroupMemberAuthorityActivity.class);
        starter.putExtra("groupId", groupId);
        starter.putExtra("type", type);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_group_member_authority;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
        flag = intent.getIntExtra("type", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        srlRefresh = findViewById(R.id.srl_refresh);
        rvList = findViewById(R.id.rv_list);
        sidebar = findViewById(R.id.sidebar);
        floatingHeader = findViewById(R.id.floating_header);

        titleBar.setTitle(getString(R.string.em_group_member_type_member));

        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new GroupMemberAuthorityAdapter();
        rvList.setAdapter(adapter);

        presenter = new SidebarPresenter();
        presenter.setupWithRecyclerView(rvList, floatingHeader);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(flag == TYPE_MEMBER) {
            menu.findItem(R.id.action_group_black).setVisible(true);
            menu.findItem(R.id.action_group_mute).setVisible(true);
            menu.findItem(R.id.action_group_add).setVisible(true);
        }else if(flag == TYPE_BLACK) {
            menu.findItem(R.id.action_group_member).setVisible(true);
            menu.findItem(R.id.action_group_mute).setVisible(true);
        }else if(flag == TYPE_MUTE) {
            menu.findItem(R.id.action_group_member).setVisible(true);
            menu.findItem(R.id.action_group_black).setVisible(true);
        }
        onSubPrepareOptionsMenu(menu);
        return false/*super.onPrepareOptionsMenu(menu)*/;
    }

    protected void onSubPrepareOptionsMenu(Menu menu) {
        //对角色进行判断
        if(!isOwner() && !isInAdminList(DemoHelper.getInstance().getCurrentUser())) {
            menu.findItem(R.id.action_group_black).setVisible(false);
            menu.findItem(R.id.action_group_mute).setVisible(false);
        }
        if(!GroupHelper.isCanInvite(group)) {
            menu.findItem(R.id.action_group_add).setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mContext instanceof GroupMemberAuthorityActivity) {
            getMenuInflater().inflate(R.menu.demo_group_member_authority_menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_group_member :
                flag = TYPE_MEMBER;
                refreshData();
                invalidateOptionsMenu();
                break;
            case R.id.action_group_black :
                flag = TYPE_BLACK;
                refreshData();
                invalidateOptionsMenu();
                break;
            case R.id.action_group_mute :
                flag = TYPE_MUTE;
                refreshData();
                invalidateOptionsMenu();
                break;
            case R.id.action_group_add :
                GroupPickContactsActivity.actionStartForResult(mContext,
                        groupId, false, REQUEST_CODE_ADD_USER);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        sidebar.setOnTouchEventListener(presenter);
        srlRefresh.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        getGroup();
        viewModel = new ViewModelProvider(this).get(GroupMemberAuthorityViewModel.class);
        getData();
    }

    private void getGroup() {
        group = DemoHelper.getInstance().getGroupManager().getGroup(groupId);
    }

    protected void refreshData() {
        if(flag == TYPE_MEMBER) {
            viewModel.getMembers(groupId);
        }
        if(isOwner() || isInAdminList(DemoHelper.getInstance().getCurrentUser())) {
            viewModel.getBlackMembers(groupId);
            viewModel.getMuteMembers(groupId);
        }
        if(flag == TYPE_MEMBER) {
            titleBar.setTitle(getString(R.string.em_authority_menu_member_list));
        }else if(flag == TYPE_BLACK) {
            titleBar.setTitle(getString(R.string.em_authority_menu_black_list));
        }else {
            titleBar.setTitle(getString(R.string.em_authority_menu_mute_list));
        }
    }

    public void getData() {
        viewModel.getMemberObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    getGroup();
                    adapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });
        });
        viewModel.getMuteMembersObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Map<String, Long>>() {
                @Override
                public void onSuccess(Map<String, Long> data) {
                    muteMembers.clear();
                    muteMembers.addAll(data.keySet());
                    if(flag == TYPE_MUTE) {
                        List<EaseUser> muteUsers = EmUserEntity.parse(muteMembers);
                        adapter.setData(muteUsers);
                    }
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    if(flag == TYPE_MUTE) {
                        finishRefresh();
                    }
                }
            });
        });
        viewModel.getBlackObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    blackMembers.clear();
                    blackMembers.addAll(data);
                    if(flag == TYPE_BLACK) {
                        List<EaseUser> blackUsers = EmUserEntity.parse(blackMembers);
                        adapter.setData(blackUsers);
                    }
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    if(flag == TYPE_BLACK) {
                        finishRefresh();
                    }
                }
            });
        });
        viewModel.getRefreshObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    refreshData();
                    LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
                }
            });
        });

        viewModel.getTransferOwnerObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_OWNER_TRANSFER, EaseEvent.TYPE.GROUP));
                    finish();
                }
            });
        });
        viewModel.getMessageChangeObservable().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event.isGroupChange()) {
                refreshData();
            }else if(event.isGroupLeave() && TextUtils.equals(groupId, event.message)) {
                finish();
            }
        });
        refreshData();
    }

    public void finishRefresh() {
        if(srlRefresh != null) {
            runOnUiThread(()-> srlRefresh.finishRefresh());
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        refreshData();
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onItemClick(View view, int position) {
        EaseUser user = adapter.getItem(position);
        ContactDetailActivity.actionStart(mContext, user, DemoHelper.getInstance().getModel().isContact(user.getUsername()));
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        if(isMember()) {
            return false;
        }
        PopupMenu menu = new PopupMenu(mContext, view);
        menu.setGravity(Gravity.CENTER_HORIZONTAL);
        menu.getMenuInflater().inflate(R.menu.demo_group_member_authority_item_menu, menu.getMenu());
        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(mContext, (MenuBuilder) menu.getMenu(), view);
        menuPopupHelper.setForceShowIcon(true);
        menuPopupHelper.setGravity(Gravity.CENTER_HORIZONTAL);
        EaseUser item = adapter.getItem(position);
        if(item == null) {
            return false;
        }
        String username = item.getUsername();
        setMenuInfo(menu.getMenu());
        if(isInBlackList(username)) {
            setMenuItemVisible(menu.getMenu(), R.id.action_group_remove_black);
        }else if(isInMuteList(username)) {
            if(flag != TYPE_MUTE) {
                menu.getMenu().findItem(R.id.action_group_add_admin).setVisible(isOwner());
                setMenuItemVisible(menu.getMenu(), R.id.action_group_remove_member);
                setMenuItemVisible(menu.getMenu(), R.id.action_group_add_black);
            }
            setMenuItemVisible(menu.getMenu(), R.id.action_group_unmute);
        }else if(isInAdminList(username)) {
            setMenuItemVisible(menu.getMenu(), R.id.action_group_remove_admin);
            setMenuItemVisible(menu.getMenu(), R.id.action_group_transfer_owner);
        }else {
            menu.getMenu().findItem(R.id.action_group_add_admin).setVisible(isOwner());
            setMenuItemVisible(menu.getMenu(), R.id.action_group_transfer_owner);
            setMenuItemVisible(menu.getMenu(), R.id.action_group_remove_member);
            setMenuItemVisible(menu.getMenu(), R.id.action_group_add_black);
            setMenuItemVisible(menu.getMenu(), R.id.action_group_mute);
        }
        menuPopupHelper.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_group_add_admin ://设为管理员
                        addToAdmins(username);
                        break;
                    case R.id.action_group_remove_admin ://移除管理员
                        removeFromAdmins(username);
                        break;
                    case R.id.action_group_transfer_owner ://移交群主
                        transferOwner(username);
                        break;
                    case R.id.action_group_remove_member ://踢出群
                        new SimpleDialogFragment.Builder(mContext)
                                .setTitle(R.string.em_authority_remove_group)
                                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                                    @Override
                                    public void onConfirmClick(View view) {
                                        removeFromGroup(username);
                                    }
                                })
                                .showCancelButton(true)
                                .show();

                        break;
                    case R.id.action_group_add_black ://加入黑名单
                        addToBlack(username);
                        break;
                    case R.id.action_group_remove_black ://移出黑名单
                        removeFromBlacks(username);
                        break;
                    case R.id.action_group_mute ://禁言
                        AddToMuteMembers(username);
                        break;
                    case R.id.action_group_unmute ://解除禁言
                        removeFromMuteMembers(username);
                        break;
                }
                return false;
            }
        });
        return true;
    }

    protected void addToAdmins(String username) {
        viewModel.addGroupAdmin(groupId, username);
    }

    protected void removeFromAdmins(String username) {
        viewModel.removeGroupAdmin(groupId, username);
    }

    protected void transferOwner(String username) {
        viewModel.changeOwner(groupId, username);
    }

    protected void removeFromGroup(String username) {
        viewModel.removeUserFromGroup(groupId, username);
    }

    protected void addToBlack(String username) {
        viewModel.blockUser(groupId, username);
    }

    protected void removeFromBlacks(String username) {
        viewModel.unblockUser(groupId, username);
    }

    protected void AddToMuteMembers(String username) {
        List<String> mutes = new ArrayList<>();
        mutes.add(username);
        viewModel.muteGroupMembers(groupId, mutes, 20 * 60 * 1000);
    }

    protected void removeFromMuteMembers(String username) {
        List<String> unMutes = new ArrayList<>();
        unMutes.add(username);
        viewModel.unMuteGroupMembers(groupId, unMutes);
    }

    /**
     * 修改菜单项
     * @param menu
     */
    protected void setMenuInfo(Menu menu) {

    }

    public boolean isOwner() {
        return GroupHelper.isOwner(group);
    }

    public boolean isInAdminList(String username) {
        return GroupHelper.isInAdminList(username, group.getAdminList());
    }

    public boolean isInMuteList(String username) {
        return GroupHelper.isInMuteList(username, muteMembers);
    }

    public boolean isInBlackList(String username) {
        return GroupHelper.isInBlackList(username, blackMembers);
    }

    public boolean isMember() {
        return !GroupHelper.isAdmin(group) && !isOwner();
    }

    /**
     * 设置菜单条目可见
     * @param menu
     * @param actionId
     */
    protected void setMenuItemVisible(Menu menu, @IdRes int actionId) {
        menu.findItem(actionId).setVisible(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER :
                    refreshData();
                    break;
            }
        }
    }

}
