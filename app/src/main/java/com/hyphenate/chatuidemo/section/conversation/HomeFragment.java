package com.hyphenate.chatuidemo.section.conversation;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class HomeFragment extends BaseInitFragment {
    private EaseTitleBar mTitleBarHome;
    private RecyclerView mRvHomeList;

    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBarHome = findViewById(R.id.title_bar_home);
        mRvHomeList = findViewById(R.id.rv_home_list);

        mRvHomeList.setLayoutManager(new LinearLayoutManager(mContext));

    }

    @Override
    protected void initData() {
        super.initData();
    }

}
