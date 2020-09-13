package com.hyphenate.chatuidemo.section.contact.activity;

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
import com.hyphenate.chatuidemo.common.constant.DemoConstant;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.contact.adapter.GroupContactFragmentAdapter;
import com.hyphenate.chatuidemo.section.contact.fragment.GroupContactManageFragment;
import com.hyphenate.chatuidemo.section.contact.fragment.GroupPublicContactManageFragment;
import com.hyphenate.chatuidemo.section.contact.viewmodels.GroupContactViewModel;
import com.hyphenate.chatuidemo.section.search.SearchGroupActivity;
import com.hyphenate.chatuidemo.section.search.SearchPublicGroupActivity;
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
    private Fragment joinGroupFragment;
    private Fragment publicGroupFragment;

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
        showJoinGroup();
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onRightClick(View view) {
        switchTab();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_group :
                if(isShowPublic()) {
                    //搜索公开群
                    SearchGroupActivity.actionStart(mContext);
                }else {
                    //搜索已加入的群
                    SearchPublicGroupActivity.actionStart(mContext);
                }
                break;
        }
    }

    private void switchTab() {
        if(isShowPublic()) {
            //公开群
            mTitleBarGroupContact.getRightText().setText(R.string.em_friends_group_join);
            mTitleBarGroupContact.setTitle(getString(R.string.em_friends_group_public));
            showPublicGroup();
        }else {
            //已加入的群
            mTitleBarGroupContact.getRightText().setText(R.string.em_friends_group_public);
            mTitleBarGroupContact.setTitle(getString(R.string.em_friends_group_join));
            showJoinGroup();
        }
    }

    /**
     * 是否是公开群
     * @return
     */
    private boolean isShowPublic() {
        String rightContent = mTitleBarGroupContact.getRightText().getText().toString().trim();
        return TextUtils.equals(rightContent, getString(R.string.em_friends_group_public));
    }

    private void showJoinGroup() {
        joinGroupFragment = getSupportFragmentManager().findFragmentByTag("join-group");
        if(joinGroupFragment != null && joinGroupFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(joinGroupFragment);
        }else {
            joinGroupFragment = new GroupContactManageFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, joinGroupFragment, "join-group").commit();
        }
    }

    private void showPublicGroup() {
        //设置公开群fragment
        publicGroupFragment = getSupportFragmentManager().findFragmentByTag("public-group");
        if(publicGroupFragment != null && publicGroupFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(publicGroupFragment);
        }else {
            publicGroupFragment = new GroupPublicContactManageFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, publicGroupFragment, "public-group").commit();
        }
    }
}
