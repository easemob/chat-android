package com.hyphenate.chatuidemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hyphenate.chatuidemo.section.base.BaseFragment;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.conversation.HomeFragment;
import com.hyphenate.chatuidemo.section.discover.DiscoverFragment;
import com.hyphenate.chatuidemo.section.friends.FriendsFragment;
import com.hyphenate.chatuidemo.section.me.AboutMeFragment;


public class MainActivity extends BaseInitActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navView;
    private BaseFragment mHomeFragment, mFriendsFragment, mDiscoverFragment, mAboutMeFragment;
    private BaseFragment mCurrentFragment;
    private TextView mTvMainHomeMsg, mTvMainFriendsMsg, mTvMainDiscoverMsg, mTvMainAboutMeMsg;
    private int[] badgeIds = {R.layout.em_badge_home, R.layout.em_badge_friends, R.layout.em_badge_discover, R.layout.em_badge_about_me};
    private int[] msgIds = {R.id.tv_main_home_msg, R.id.tv_main_friends_msg, R.id.tv_main_discover_msg, R.id.tv_main_about_me_msg};

    public static void startAction(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        //navView.getMenu().findItem(R.id.em_main_nav_me).setVisible(false);
        switchToHome();
        initTab();
    }

    @Override
    protected void initListener() {
        super.initListener();
        navView.setOnNavigationItemSelectedListener(this);
    }

    private void initTab() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navView.getChildAt(0);
        int childCount = menuView.getChildCount();
        Log.e("TAG", "bottom child count = "+childCount);
        BottomNavigationItemView itemTab;
        for(int i = 0; i < childCount; i++) {
            itemTab = (BottomNavigationItemView) menuView.getChildAt(i);
            View badge = LayoutInflater.from(mContext).inflate(badgeIds[i], menuView, false);
            switch (i) {
                case 0 :
                    mTvMainHomeMsg = badge.findViewById(msgIds[0]);
                    break;
                case 1 :
                    mTvMainFriendsMsg = badge.findViewById(msgIds[1]);
                    break;
                case 2 :
                    mTvMainDiscoverMsg = badge.findViewById(msgIds[2]);
                    break;
                case 3 :
                    mTvMainAboutMeMsg = badge.findViewById(msgIds[3]);
                    break;
            }
            itemTab.addView(badge);
        }
    }

    private void switchToHome() {
        if(mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
        }
        replace(mHomeFragment);
    }

    private void switchToFriends() {
        if(mFriendsFragment == null) {
            mFriendsFragment = new FriendsFragment();
        }
        replace(mFriendsFragment);
    }

    private void switchToDiscover() {
        if(mDiscoverFragment == null) {
            mDiscoverFragment = new DiscoverFragment();
        }
        replace(mDiscoverFragment);
    }

    private void switchToAboutMe() {
        if(mAboutMeFragment == null) {
            mAboutMeFragment = new AboutMeFragment();
        }
        replace(mAboutMeFragment);
    }

    private void replace(BaseFragment fragment) {
        if(mCurrentFragment != fragment) {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            if(mCurrentFragment != null) {
                t.hide(mCurrentFragment);
            }
            mCurrentFragment = fragment;
            if(!fragment.isAdded()) {
                t.add(R.id.fl_main_fragment, fragment).show(fragment).commit();
            }else {
                t.show(fragment).commit();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.em_main_nav_home :
                switchToHome();
                return true;
            case R.id.em_main_nav_friends :
                switchToFriends();
                return true;
            case R.id.em_main_nav_discover :
                switchToDiscover();
                return true;
            case R.id.em_main_nav_me :
                switchToAboutMe();
                return true;
        }
        return false;
    }
}
