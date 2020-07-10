package com.hyphenate.chatuidemo.section.contact.fragment;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.enums.SearchType;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeui.manager.SidebarPresenter;
import com.hyphenate.chatuidemo.common.widget.ContactItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.chatuidemo.section.conference.ConferenceActivity;
import com.hyphenate.chatuidemo.section.dialog.DemoDialogFragment;
import com.hyphenate.chatuidemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.chatuidemo.section.contact.activity.AddContactActivity;
import com.hyphenate.chatuidemo.section.contact.activity.ChatRoomContactManageActivity;
import com.hyphenate.chatuidemo.section.contact.activity.ContactDetailActivity;
import com.hyphenate.chatuidemo.section.contact.activity.GroupContactManageActivity;
import com.hyphenate.chatuidemo.section.contact.adapter.ContactListAdapter;
import com.hyphenate.chatuidemo.section.contact.viewmodels.ContactsViewModel;
import com.hyphenate.chatuidemo.section.search.SearchFriendsActivity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseSearchTextView;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;


public class ContactListFragment extends BaseInitFragment implements View.OnClickListener, OnRefreshListener, OnItemClickListener {
    private EaseSearchTextView tvFriendSearch;
    private SmartRefreshLayout mSrlFriendRefresh;
    private EaseRecyclerView mRvFriendsList;
    private EaseSidebar mSideBarFriend;
    private TextView mFloatingHeader;

    private ContactListAdapter mAdapter;
    private ContactItemView mCivNewChat;
    private ContactItemView mCivGroupChat;
    private ContactItemView mCivLabel;
    private ContactItemView mCivChatRoom;
    private ContactItemView mCivOfficialAccount;
    private ContactItemView mCivAvConference;
    private ContactsViewModel mViewModel;
    private SidebarPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_friends;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tvFriendSearch = findViewById(R.id.tv_friend_search);
        mRvFriendsList = findViewById(R.id.rv_friends_list);
        mSrlFriendRefresh = findViewById(R.id.srl_friend_refresh);
        mSideBarFriend = findViewById(R.id.side_bar_friend);
        mFloatingHeader = findViewById(R.id.floating_header);

        mRvFriendsList.setHasFixedSize(true);
        mRvFriendsList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new ContactListAdapter();
        mRvFriendsList.setAdapter(mAdapter);
        addHeader();

        mPresenter = new SidebarPresenter();
        mPresenter.setupWithRecyclerView(mRvFriendsList, mFloatingHeader);

        registerForContextMenu(mRvFriendsList);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.demo_friends_list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = ((EaseRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo()).position - 1;
        EaseUser user = mAdapter.getItem(position);
        switch (item.getItemId()) {
            case R.id.action_friend_delete ://删除好友
                showDeleteDialog(user);
                break;
            case R.id.action_friend_block ://加入黑名单
                mViewModel.addUserToBlackList(user.getUsername(), false);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void showDeleteDialog(EaseUser user) {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(R.string.ease_friends_delete_contact_hint)
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        mViewModel.deleteContact(user.getUsername());
                    }
                })
                .showCancelButton(true)
                .show();
    }

    @Override
    protected void initListener() {
        super.initListener();
        tvFriendSearch.setOnClickListener(this);
        mCivNewChat.setOnClickListener(this);
        mCivGroupChat.setOnClickListener(this);
        mCivLabel.setOnClickListener(this);
        mCivChatRoom.setOnClickListener(this);
        mCivOfficialAccount.setOnClickListener(this);
        mCivAvConference.setOnClickListener(this);
        mSrlFriendRefresh.setOnRefreshListener(this);
        mSideBarFriend.setOnTouchEventListener(mPresenter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_friend_search :
                SearchFriendsActivity.actionStart(mContext);
                break;
            case R.id.civ_new_chat :
                AddContactActivity.startAction(mContext, SearchType.CHAT);
                break;
            case R.id.civ_group_chat :
                GroupContactManageActivity.actionStart(mContext);
                break;
            case R.id.civ_label :
                showToast("lable");
                break;
            case R.id.civ_chat_room :
                ChatRoomContactManageActivity.actionStart(mContext);
                break;
            case R.id.civ_official_account :
                showToast("official account");
                break;
            case R.id.civ_av_conference:
                ConferenceActivity.startConferenceCall(getActivity(), null);
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);
        mViewModel.getContactObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    // 先进行排序
                    mAdapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });

        });

        mViewModel.resultObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    mViewModel.loadContactList();
                }
            });
        });

        mViewModel.deleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    mViewModel.loadContactList();
                }
            });
        });

        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                mViewModel.loadContactList();
            }
        });

        mViewModel.loadContactList();
    }

    /**
     * 添加头布局
     */
    private void addHeader() {
        // 获取头布局，应该放在RecyclerView的setLayoutManager之后
        View header = getLayoutInflater().inflate(R.layout.demo_header_friends_list, mRvFriendsList, false);
        mRvFriendsList.addHeaderView(header);

        mCivNewChat = header.findViewById(R.id.civ_new_chat);
        mCivGroupChat = header.findViewById(R.id.civ_group_chat);
        mCivLabel = header.findViewById(R.id.civ_label);
        mCivChatRoom = header.findViewById(R.id.civ_chat_room);
        mCivOfficialAccount = header.findViewById(R.id.civ_official_account);
        mCivAvConference = header.findViewById(R.id.civ_av_conference);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mViewModel.loadContactList();
    }

    private void finishRefresh() {
        if(mSrlFriendRefresh != null) {
            mSrlFriendRefresh.finishRefresh();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        EaseUser item = mAdapter.getItem(position);
        ContactDetailActivity.actionStart(mContext, item);
    }
}
