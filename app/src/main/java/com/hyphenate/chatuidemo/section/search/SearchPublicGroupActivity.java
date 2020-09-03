package com.hyphenate.chatuidemo.section.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.section.chat.ChatActivity;
import com.hyphenate.chatuidemo.section.contact.adapter.PublicGroupContactAdapter;
import com.hyphenate.chatuidemo.section.contact.viewmodels.PublicGroupViewModel;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;

import androidx.lifecycle.ViewModelProvider;

public class SearchPublicGroupActivity extends SearchActivity {

    private PublicGroupViewModel viewModel;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, SearchPublicGroupActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar.setTitle(getString(R.string.em_search_group_public));
        query.setHint("请输入公开群id进行搜索");
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(PublicGroupViewModel.class);
        viewModel.getGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMGroup>() {
                @Override
                public void onSuccess(EMGroup data) {
                    adapter.addData(data);
                }
            });
        });
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new PublicGroupContactAdapter();
    }

    @Override
    public void searchMessages(String search) {
        if(!TextUtils.isEmpty(search)) {
            viewModel.getGroup(search);
        }
    }

    @Override
    protected void onChildItemClick(View view, int position) {
        EMGroup group = (EMGroup) adapter.getItem(position);
        ChatActivity.actionStart(mContext, group.getGroupId(), DemoConstant.CHATTYPE_GROUP);
    }
}
