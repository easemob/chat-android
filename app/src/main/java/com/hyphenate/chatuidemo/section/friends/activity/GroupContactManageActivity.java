package com.hyphenate.chatuidemo.section.friends.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.friends.adapter.GroupContactFragmentAdapter;
import com.hyphenate.chatuidemo.section.friends.fragment.GroupPublicContactManageFragment;
import com.hyphenate.chatuidemo.section.friends.viewmodels.GroupContactViewModel;
import com.hyphenate.chatuidemo.section.search.SearchGroupActivity;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseSearchTextView;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class GroupContactManageActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, EaseTitleBar.OnRightClickListener, View.OnClickListener {
    private EaseTitleBar mTitleBarGroupContact;
    private EaseSearchTextView mSearchGroup;
    private TabLayout mTlGroupContact;
    private ViewPager mVpGroupContact;
    private FrameLayout flFragment;
    private Group groupJoin;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, GroupContactManageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_friends_group_contact_manage;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBarGroupContact = findViewById(R.id.title_bar_group_contact);
        mSearchGroup = findViewById(R.id.search_group);
        mTlGroupContact = findViewById(R.id.tl_group_contact);
        mVpGroupContact = findViewById(R.id.vp_group_contact);
        flFragment = findViewById(R.id.fl_fragment);
        groupJoin = findViewById(R.id.group_join);

        mTlGroupContact.setupWithViewPager(mVpGroupContact);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("public-contact");
        if(fragment != null && fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(fragment);
        }else {
            fragment = new GroupPublicContactManageFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "public-contact").commit();
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTitleBarGroupContact.setOnBackPressListener(this);
        mTitleBarGroupContact.setOnRightClickListener(this);
        mSearchGroup.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        GroupContactFragmentAdapter adapter = new GroupContactFragmentAdapter(mContext, getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mVpGroupContact.setAdapter(adapter);

        GroupContactViewModel viewModel = new ViewModelProvider(mContext).get(GroupContactViewModel.class);
        viewModel.getMessageObservable().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isGroupChange() || event.isGroupLeave()) {
                viewModel.loadAllGroups();
            }
        });
        viewModel.loadAllGroups();
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onRightClick(View view) {
        String rightContent = mTitleBarGroupContact.getRightText().getText().toString().trim();
        if(TextUtils.equals(rightContent, getString(R.string.em_friends_group_public))) {
            //公开群
            mTitleBarGroupContact.getRightText().setText(R.string.em_friends_group_join);
            groupJoin.setVisibility(View.GONE);
            flFragment.setVisibility(View.VISIBLE);
        }else {
            //已加入的群
            mTitleBarGroupContact.getRightText().setText(R.string.em_friends_group_public);
            groupJoin.setVisibility(View.VISIBLE);
            flFragment.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_group :
                SearchGroupActivity.actionStart(mContext);
                break;
        }
    }
}
