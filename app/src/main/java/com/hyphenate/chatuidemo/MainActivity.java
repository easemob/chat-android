package com.hyphenate.chatuidemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;


public class MainActivity extends BaseInitActivity {
    private BottomNavigationView navView;

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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.fl_main_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        //动态设置tab是否展示
//        MenuItem item = navView.getMenu().findItem(R.id.em_main_me);
//        item.setVisible(false);
        initTab();
    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    private void initTab() {

    }

}
