package com.hyphenate.chatuidemo.section.contact.fragment;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.enums.SearchType;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.utils.ToastUtils;
import com.hyphenate.chatuidemo.common.widget.ContactItemView;
import com.hyphenate.chatuidemo.section.base.BaseActivity;
import com.hyphenate.chatuidemo.section.conference.ConferenceActivity;
import com.hyphenate.chatuidemo.section.contact.activity.AddContactActivity;
import com.hyphenate.chatuidemo.section.contact.activity.ChatRoomContactManageActivity;
import com.hyphenate.chatuidemo.section.contact.activity.ContactDetailActivity;
import com.hyphenate.chatuidemo.section.contact.activity.GroupContactManageActivity;
import com.hyphenate.chatuidemo.section.contact.viewmodels.ContactsViewModel;
import com.hyphenate.chatuidemo.section.dialog.DemoDialogFragment;
import com.hyphenate.chatuidemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.chatuidemo.section.search.SearchFriendsActivity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.widget.EaseSearchTextView;

import java.util.List;

public class ContactListFragment extends EaseContactListFragment implements View.OnClickListener {
    private ContactItemView mCivNewChat;
    private ContactItemView mCivGroupChat;
    private ContactItemView mCivLabel;
    private ContactItemView mCivChatRoom;
    private ContactItemView mCivOfficialAccount;
    private ContactItemView mCivAvConference;
    private EaseSearchTextView tvSearch;

    private ContactsViewModel mViewModel;

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        addSearchView();
        addHeader();
        //设置无数据时空白页面
        adapter.setEmptyLayoutResource(R.layout.demo_layout_friends_empty_list);
    }

    @Override
    public void onChildCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onChildCreateContextMenu(menu, v, menuInfo);
        menu.add(1, R.id.action_friend_block, 2, getString(R.string.em_friends_move_into_the_blacklist_new));
    }

    @Override
    public void onChildContextItemSelected(MenuItem item, EaseUser user) {
        super.onChildContextItemSelected(item, user);
        switch (item.getItemId()) {
            case R.id.action_friend_block :
                mViewModel.addUserToBlackList(user.getUsername(), false);
                break;
        }
    }

    private void addSearchView() {
        //添加搜索会话布局
        viewStub.setLayoutResource(R.layout.demo_layout_search);
        View view = viewStub.inflate();
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params instanceof ConstraintLayout.LayoutParams) {
            ConstraintSet set = new ConstraintSet();
            set.clone(mContext, R.layout.ease_fragment_contact_list);
            set.connect(view.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
            set.connect(view.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
            set.connect(view.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            set.connect(view.getId(), ConstraintSet.BOTTOM, R.id.srl_contact_refresh, ConstraintSet.TOP);
            set.constrainWidth(view.getId(), ViewGroup.LayoutParams.MATCH_PARENT);
            set.constrainHeight(view.getId(), ViewGroup.LayoutParams.WRAP_CONTENT);
            set.connect(R.id.srl_contact_refresh, ConstraintSet.TOP, view.getId(), ConstraintSet.BOTTOM);
            set.connect(R.id.side_bar_friend, ConstraintSet.TOP, view.getId(), ConstraintSet.BOTTOM);
            ConstraintLayout clRoot = findViewById(R.id.cl_root);
            set.applyTo(clRoot);
        }
        tvSearch = view.findViewById(R.id.tv_search);
        tvSearch.setHint(R.string.em_friend_list_search_hint);
    }

    private void addHeader() {
        // 获取头布局，应该放在RecyclerView的setLayoutManager之后
        View header = getLayoutInflater().inflate(R.layout.demo_header_friends_list, rvContactList, false);
        rvContactList.addHeaderView(header);

        mCivNewChat = header.findViewById(R.id.civ_new_chat);
        mCivGroupChat = header.findViewById(R.id.civ_group_chat);
        mCivLabel = header.findViewById(R.id.civ_label);
        mCivChatRoom = header.findViewById(R.id.civ_chat_room);
        mCivOfficialAccount = header.findViewById(R.id.civ_official_account);
        mCivAvConference = header.findViewById(R.id.civ_av_conference);
    }

    @Override
    public void initListener() {
        super.initListener();
        tvSearch.setOnClickListener(this);
        mCivNewChat.setOnClickListener(this);
        mCivGroupChat.setOnClickListener(this);
        mCivLabel.setOnClickListener(this);
        mCivChatRoom.setOnClickListener(this);
        mCivOfficialAccount.setOnClickListener(this);
        mCivAvConference.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);
        mViewModel.getContactObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    // 先进行排序
                    adapter.setData(data);
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

    @Override
    public void refreshContactList() {
        mViewModel.loadContactList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search :
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
    public void showDeleteDialog(EaseUser user) {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
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
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position);
        EaseUser item = adapter.getItem(position);
        ContactDetailActivity.actionStart(mContext, item);
    }

    /**
     * 解析Resource<T>
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if(mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).parseResource(response, callback);
        }
    }

    /**
     * toast by string
     * @param message
     */
    private void showToast(String message) {
        ToastUtils.showToast(message);
    }
}
