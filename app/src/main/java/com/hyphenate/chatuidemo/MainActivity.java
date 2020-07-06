package com.hyphenate.chatuidemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.enums.SearchType;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.permission.PermissionsManager;
import com.hyphenate.chatuidemo.common.permission.PermissionsResultAction;
import com.hyphenate.chatuidemo.section.MainViewModel;
import com.hyphenate.chatuidemo.section.base.BaseFragment;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.conference.ConferenceActivity;
import com.hyphenate.chatuidemo.section.conversation.ConversationListFragment;
import com.hyphenate.chatuidemo.section.discover.DiscoverFragment;
import com.hyphenate.chatuidemo.section.friends.activity.NewGroupActivity;
import com.hyphenate.chatuidemo.section.friends.fragment.ContactListFragment;
import com.hyphenate.chatuidemo.section.friends.activity.AddContactActivity;
import com.hyphenate.chatuidemo.section.friends.viewmodels.ContactsViewModel;
import com.hyphenate.chatuidemo.section.me.AboutMeFragment;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.lang.reflect.Method;
import java.util.List;


public class MainActivity extends BaseInitActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navView;
    private EaseTitleBar mTitleBar;
    private BaseFragment mConversationListFragment, mFriendsFragment, mDiscoverFragment, mAboutMeFragment;
    private BaseFragment mCurrentFragment;
    private TextView mTvMainHomeMsg, mTvMainFriendsMsg, mTvMainDiscoverMsg, mTvMainAboutMeMsg;
    private int[] badgeIds = {R.layout.demo_badge_home, R.layout.demo_badge_friends, R.layout.demo_badge_discover, R.layout.demo_badge_about_me};
    private int[] msgIds = {R.id.tv_main_home_msg, R.id.tv_main_friends_msg, R.id.tv_main_discover_msg, R.id.tv_main_about_me_msg};
    private MainViewModel viewModel;

    public static void startAction(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_main;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.demo_conversation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_video :
                ConferenceActivity.startConferenceCall(mContext, null);
                break;
            case R.id.action_group :
                NewGroupActivity.actionStart(mContext);
                break;
            case R.id.action_friend :
                AddContactActivity.startAction(mContext, SearchType.CHAT);
                break;
            case R.id.action_scan :
                showToast("扫一扫");
                break;
        }
        return true;
    }

    /**
     * 显示menu的icon，通过反射，设置menu的icon显示
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if(menu != null) {
            if(menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void initSystemFit() {
        setFitSystemForTheme(false);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        navView = findViewById(R.id.nav_view);
        mTitleBar = findViewById(R.id.title_bar_main);
        navView.setItemIconTintList(null);
        // 可以动态显示隐藏相应tab
        //navView.getMenu().findItem(R.id.em_main_nav_me).setVisible(false);
        switchToHome();
        addTabBadge();
    }

    @Override
    protected void initListener() {
        super.initListener();
        navView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        initViewModel();
        requestPermissions();
        checkUnreadMsg();

        // 获取华为 HMS 推送 token
        HMSPushHelper.getInstance().getHMSToken(this);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(mContext).get(MainViewModel.class);
        viewModel.getSwitchObservable().observe(this, response -> {
            if(response == null || response == 0) {
                return;
            }
            if(response == R.string.em_main_title_me) {
                mTitleBar.setVisibility(View.GONE);
            }else {
                mTitleBar.setVisibility(View.VISIBLE);
                mTitleBar.setTitle(getResources().getString(response));
            }
        });

        viewModel.homeUnReadObservable().observe(this, readCount -> {
            if(!TextUtils.isEmpty(readCount)) {
                mTvMainHomeMsg.setVisibility(View.VISIBLE);
                mTvMainHomeMsg.setText(readCount);
            }else {
                mTvMainHomeMsg.setVisibility(View.GONE);
            }
        });

        //加载联系人
        ContactsViewModel contactsViewModel = new ViewModelProvider(mContext).get(ContactsViewModel.class);
        contactsViewModel.loadContactList();

        viewModel.messageChangeObservable().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.NOTIFY_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);

    }

    private void checkUnReadMsg(EaseEvent event) {
        if(event == null) {
            return;
        }
        viewModel.checkUnreadMsg();
    }

    /**
     * 添加BottomNavigationView中每个item右上角的红点
     */
    private void addTabBadge() {
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

    /**
     * 申请权限
     */
    // TODO: 2019/12/19 0019 有必要修改一下
    private void requestPermissions() {
        PermissionsManager.getInstance()
                .requestAllManifestPermissionsIfNecessary(mContext, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });
    }

    private void switchToHome() {
        if(mConversationListFragment == null) {
            mConversationListFragment = new ConversationListFragment();
        }
        replace(mConversationListFragment);
    }

    private void switchToFriends() {
        if(mFriendsFragment == null) {
            mFriendsFragment = new ContactListFragment();
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
        mTitleBar.setVisibility(View.VISIBLE);
        switch (menuItem.getItemId()) {
            case R.id.em_main_nav_home :
                switchToHome();
                mTitleBar.setTitle(getResources().getString(R.string.em_main_title_home));
                return true;
            case R.id.em_main_nav_friends :
                switchToFriends();
                mTitleBar.setTitle(getResources().getString(R.string.em_main_title_friends));
                return true;
            case R.id.em_main_nav_discover :
                switchToDiscover();
                mTitleBar.setTitle(getResources().getString(R.string.em_main_title_discover));
                return true;
            case R.id.em_main_nav_me :
                switchToAboutMe();
                mTitleBar.setVisibility(View.GONE);
                return true;
        }
        return false;
    }

    private void checkUnreadMsg() {
        viewModel.checkUnreadMsg();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DemoHelper.getInstance().showNotificationPermissionDialog();
    }
}
