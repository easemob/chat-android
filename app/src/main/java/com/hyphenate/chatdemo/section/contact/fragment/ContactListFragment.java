package com.hyphenate.chatdemo.section.contact.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hyphenate.chatdemo.DemoHelper;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.constant.DemoConstant;
import com.hyphenate.chatdemo.common.enums.SearchType;
import com.hyphenate.chatdemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatdemo.common.net.Resource;
import com.hyphenate.chatdemo.common.utils.ToastUtils;
import com.hyphenate.chatdemo.section.base.BaseActivity;
import com.hyphenate.chatdemo.section.contact.activity.AddContactActivity;
import com.hyphenate.chatdemo.section.contact.activity.ChatRoomContactManageActivity;
import com.hyphenate.chatdemo.section.contact.activity.ContactDetailActivity;
import com.hyphenate.chatdemo.section.contact.activity.GroupContactManageActivity;
import com.hyphenate.chatdemo.section.contact.viewmodels.ContactsViewModel;
import com.hyphenate.chatdemo.section.dialog.DemoDialogFragment;
import com.hyphenate.chatdemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.chatdemo.section.search.SearchFriendsActivity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.contact.EaseContactListFragment;
import com.hyphenate.easeui.modules.contact.model.EaseContactCustomBean;
import com.hyphenate.easeui.modules.menu.EasePopupMenuHelper;
import com.hyphenate.easeui.widget.EaseSearchTextView;

import java.util.List;

public class ContactListFragment extends EaseContactListFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private EaseSearchTextView tvSearch;
    private ContactsViewModel mViewModel;

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        addSearchView();
        //设置无数据时空白页面
        contactLayout.getContactList().getListAdapter().setEmptyLayoutResource(R.layout.demo_layout_friends_empty_list);
        addHeader();

        //设置为简洁模式
        //contactLayout.showSimple();
        //获取列表控件
        //EaseContactListLayout contactList = contactLayout.getContactList();
        //设置条目高度
        //contactList.setItemHeight((int) EaseCommonUtils.dip2px(mContext, 80));
        //设置条目背景
        //contactList.setItemBackGround(ContextCompat.getDrawable(mContext, R.color.gray));
        //设置头像样式
        //contactList.setAvatarShapeType(2);
        //设置头像圆角
        //contactList.setAvatarRadius((int) EaseCommonUtils.dip2px(mContext, 5));
        //设置header背景
        //contactList.setHeaderBackGround(ContextCompat.getDrawable(mContext, R.color.white));
    }

    @Override
    public void onMenuPreShow(EasePopupMenuHelper menuHelper, int position) {
        super.onMenuPreShow(menuHelper, position);
        menuHelper.addItemMenu(1, R.id.action_friend_block, 2, getString(R.string.em_friends_move_into_the_blacklist_new));
        menuHelper.addItemMenu(1, R.id.action_friend_delete, 1, getString(R.string.ease_friends_delete_the_contact));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        EaseUser user = contactLayout.getContactList().getItem(position);
        switch (item.getItemId()) {
            case R.id.action_friend_block :
                mViewModel.addUserToBlackList(user.getUsername(), false);
                return true;
            case R.id.action_friend_delete:
                showDeleteDialog(user);
                return true;
        }
        return super.onMenuItemClick(item, position);
    }

    private void addSearchView() {
        //添加搜索会话布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.demo_layout_search, null);
        llRoot.addView(view, 0);
        tvSearch = view.findViewById(R.id.tv_search);
        tvSearch.setHint(R.string.em_friend_list_search_hint);
    }

    /**
     * 添加头布局
     */
    public void addHeader() {
        contactLayout.getContactList().addCustomItem(R.id.contact_header_item_new_chat, R.drawable.em_friends_new_chat, getString(R.string.em_friends_new_chat));
        contactLayout.getContactList().addCustomItem(R.id.contact_header_item_group_list, R.drawable.em_friends_group_chat, getString(R.string.em_friends_group_chat));
        contactLayout.getContactList().addCustomItem(R.id.contact_header_item_chat_room_list, R.drawable.em_friends_chat_room, getString(R.string.em_friends_chat_room));
    }

    @Override
    public void initListener() {
        super.initListener();
        contactLayout.getSwipeRefreshLayout().setOnRefreshListener(this);
        tvSearch.setOnClickListener(this);
        contactLayout.getContactList().setOnCustomItemClickListener(new OnItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(View view, int position) {
                EaseContactCustomBean item = contactLayout.getContactList().getCustomAdapter().getItem(position);
                switch (item.getId()) {
                    case R.id.contact_header_item_new_chat :
                        AddContactActivity.startAction(mContext, SearchType.CHAT);
                        break;
                    case R.id.contact_header_item_group_list :
                        GroupContactManageActivity.actionStart(mContext);
                        break;
                    case R.id.contact_header_item_chat_room_list :
                        ChatRoomContactManageActivity.actionStart(mContext);
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
        //更新过期用户属性列表
        DemoHelper.getInstance().updateTimeoutUsers();
        mViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);
        mViewModel.getContactObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    contactLayout.getContactList().setData(data);
                }

                @Override
                public void onLoading(@Nullable List<EaseUser> data) {
                    super.onLoading(data);
                    contactLayout.getContactList().setData(data);
                }
            });
        });

        mViewModel.resultObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    showToast(R.string.em_friends_move_into_blacklist_success);
                    mViewModel.loadContactList(false);
                }
            });
        });

        mViewModel.deleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    mViewModel.loadContactList(false);
                }
            });
        });

        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });

        mViewModel.messageChangeObservable().with(DemoConstant.REMOVE_BLACK, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                mViewModel.loadContactList(true);
            }
        });


        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_ADD, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });


        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_DELETE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });

        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_UPDATE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });


        mViewModel.loadContactList(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search :
                SearchFriendsActivity.actionStart(mContext);
                break;
        }
    }

    private void showDeleteDialog(EaseUser user) {
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
        EaseUser item = contactLayout.getContactList().getItem(position);
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

    /**
     * toast by string res
     * @param messageId
     */
    public void showToast(@StringRes int messageId) {
        ToastUtils.showToast(messageId);
    }

    @Override
    public void onRefresh() {
        mViewModel.loadContactList(true);
    }
}
