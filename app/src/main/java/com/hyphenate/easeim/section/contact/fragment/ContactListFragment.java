package com.hyphenate.easeim.section.contact.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.enums.SearchType;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.model.ContactHeaderBean;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.utils.ToastUtils;
import com.hyphenate.easeim.section.base.BaseActivity;
import com.hyphenate.easeim.section.contact.activity.AddContactActivity;
import com.hyphenate.easeim.section.contact.activity.ChatRoomContactManageActivity;
import com.hyphenate.easeim.section.contact.activity.ContactDetailActivity;
import com.hyphenate.easeim.section.contact.activity.GroupContactManageActivity;
import com.hyphenate.easeim.section.contact.adapter.ContactHeaderAdapter;
import com.hyphenate.easeim.section.contact.viewmodels.ContactsViewModel;
import com.hyphenate.easeim.section.dialog.DemoDialogFragment;
import com.hyphenate.easeim.section.dialog.SimpleDialogFragment;
import com.hyphenate.easeim.section.search.SearchFriendsActivity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.manager.EaseProviderManager;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.contact.EaseContactListFragment;
import com.hyphenate.easeui.modules.contact.model.EaseContactCustomBean;
import com.hyphenate.easeui.modules.menu.EasePopupMenuHelper;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.easeui.widget.EaseSearchTextView;

import java.util.List;

public class ContactListFragment extends EaseContactListFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final int CUSTOM_NEW_CHAT = 0;
    private static final int CUSTOM_GROUP_LIST = 1;
    private static final int CUSTOM_CHAT_ROOM_LIST = 2;
    private EaseSearchTextView tvSearch;
    private ContactsViewModel mViewModel;

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        addSearchView();
        //设置无数据时空白页面
        contactLayout.getContactList().getListAdapter().setEmptyLayoutResource(R.layout.demo_layout_friends_empty_list);
        addHeader();

//        EaseProviderManager.getInstance().setUserProvider(new EaseUserProfileProvider() {
//            @Override
//            public EaseUser getUser(String username) {
//                return null;
//            }
//
//            @Override
//            public EaseUser getUser(EaseUser user) {
//                if(TextUtils.equals(user.getUsername(), "chong")) {
//                    user.setAvatar("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1605085753048&di=d1e68d730cde4b1d399eea7770a50a45&imgtype=0&src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202005%2F11%2F20200511141839_NUsHG.thumb.400_0.jpeg");
//                    user.setNickname("马上详见");
//                }
//                if(TextUtils.equals(user.getUsername(), "ljna")) {
//                    user.setAvatar("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1605085753046&di=de5215509a758bbaed0d7dac0fd756c9&imgtype=0&src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202006%2F07%2F20200607211021_SNzhk.thumb.400_0.jpeg");
//                    user.setNickname("小号的天下");
//                }
//                return user;
//            }
//        });
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
        contactLayout.getContactList().addCustomItem(CUSTOM_NEW_CHAT, R.drawable.em_friends_new_chat, getString(R.string.em_friends_new_chat));
        contactLayout.getContactList().addCustomItem(CUSTOM_GROUP_LIST, R.drawable.em_friends_group_chat, getString(R.string.em_friends_group_chat));
        contactLayout.getContactList().addCustomItem(CUSTOM_CHAT_ROOM_LIST, R.drawable.em_friends_chat_room, getString(R.string.em_friends_chat_room));
    }

    @Override
    public void initListener() {
        super.initListener();
        contactLayout.getSwipeRefreshLayout().setOnRefreshListener(this);
        tvSearch.setOnClickListener(this);
        contactLayout.getContactList().setOnCustomItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                EaseContactCustomBean item = contactLayout.getContactList().getCustomAdapter().getItem(position);
                switch (item.getId()) {
                    case CUSTOM_NEW_CHAT :
                        AddContactActivity.startAction(mContext, SearchType.CHAT);
                        break;
                    case CUSTOM_GROUP_LIST :
                        GroupContactManageActivity.actionStart(mContext);
                        break;
                    case CUSTOM_CHAT_ROOM_LIST :
                        ChatRoomContactManageActivity.actionStart(mContext);
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
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
        mViewModel.loadContactList();
    }
}
