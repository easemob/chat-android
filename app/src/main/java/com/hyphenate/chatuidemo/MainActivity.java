package com.hyphenate.chatuidemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.hyphenate.chatuidemo.base.BaseInitActivity;
import com.hyphenate.chatuidemo.ui.home.HomeFragment;


public class MainActivity extends BaseInitActivity implements BottomNavigationBar.OnTabSelectedListener {
    private BottomNavigationBar navBar;
    private int lastSelectedPosition;
    private TextBadgeItem homeBadgeItem;
    private TextBadgeItem friendBadgeItem;
    private TextBadgeItem setBadgeItem;

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
        navBar = findViewById(R.id.nav_view);
        initTab();
    }

    @Override
    protected void initListener() {
        super.initListener();
        navBar.setTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(int position) {
        lastSelectedPosition = position;
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    private void initTab() {
        navBar.clearAll();
        setFragment();

        initBadgeItem();

        navBar.setMode(BottomNavigationBar.MODE_FIXED);
        navBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        navBar.addItem(new BottomNavigationItem(R.drawable.ic_home_black_24dp, R.string.main_home)
                            .setActiveColorResource(R.color.red).setBadgeItem(homeBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.ic_book_white_24dp, R.string.main_friend)
                            .setActiveColorResource(R.color.red).setBadgeItem(friendBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.ic_music_note_white_24dp, R.string.main_set)
                            .setActiveColorResource(R.color.red).setBadgeItem(setBadgeItem))
                .initialise();

    }

    private void setFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, new HomeFragment()).commit();
    }

    private void initBadgeItem() {
        homeBadgeItem = new TextBadgeItem()
                .setBorderWidth(4)
                .setBackgroundColorResource(R.color.red)
                .setText(6+"")
                .setHideOnSelect(true);

        friendBadgeItem = new TextBadgeItem()
                .setBorderWidth(4)
                .setBackgroundColorResource(R.color.red)
                .setText(8+"")
                .setHideOnSelect(true);

        setBadgeItem = new TextBadgeItem()
                .setBorderWidth(4)
                .setBackgroundColorResource(R.color.red)
                .setText(20+"")
                .setHideOnSelect(true);
    }
}
