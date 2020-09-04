package com.hyphenate.chatuidemo.section.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.db.DemoDbHelper;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.section.contact.activity.ContactDetailActivity;
import com.hyphenate.chatuidemo.section.contact.adapter.ContactListAdapter;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendsActivity extends SearchActivity {
    public List<EaseUser> mData;
    public List<EaseUser> result;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SearchFriendsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar.setTitle(getString(R.string.em_search_contact));
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new SearchFriendAdapter();
    }

    @Override
    protected void initData() {
        super.initData();
        result = new ArrayList<>();
        mData = DemoDbHelper.getInstance(mContext).getUserDao().loadAllEaseUsers();
    }

    @Override
    public void searchMessages(String search) {
        searchResult(search);
    }

    private void searchResult(String search) {
        if(mData == null || mData.isEmpty() || TextUtils.isEmpty(search)) {
            return;
        }

        ThreadManager.getInstance().runOnIOThread(()-> {
            result.clear();
            for (EaseUser user : mData) {
                if(user.getUsername().contains(search) || user.getNickname().contains(search)) {
                    result.add(user);
                }
            }
            runOnUiThread(()-> adapter.setData(result));
        });
    }

    @Override
    protected void onChildItemClick(View view, int position) {
        EaseUser item = ((SearchFriendAdapter)adapter).getItem(position);
        ContactDetailActivity.actionStart(mContext, item);
    }

    public class SearchFriendAdapter extends ContactListAdapter {
        @Override
        public int getEmptyLayoutId() {
            return R.layout.demo_layout_no_data_show_nothing;
        }
    }
}
