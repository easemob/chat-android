package com.hyphenate.chatdemo.section.me.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.databinding.DemoActivityTestFunctionsBinding;
import com.hyphenate.chatdemo.section.base.BaseFragment;
import com.hyphenate.chatdemo.section.base.BaseInitActivity;
import com.hyphenate.chatdemo.section.me.test.fragment.TestDatabaseFragment;
import com.hyphenate.chatdemo.section.me.test.fragment.TestGroupFragment;
import com.hyphenate.chatdemo.section.me.test.fragment.TestInputListenerFragment;
import com.hyphenate.chatdemo.section.me.test.fragment.TestPresenceFragment;
import com.hyphenate.chatdemo.section.me.test.fragment.TestStatisticsFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class TestFunctionsActivity extends BaseInitActivity {

    private DemoActivityTestFunctionsBinding binding;

    public static void actionStart(Context context, String tag) {
        Intent intent = new Intent(context, TestFunctionsActivity.class);
        intent.putExtra("tag", tag);
        context.startActivity(intent);
    }

    @Override
    protected View getContentView() {
        binding = DemoActivityTestFunctionsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        String tag = getIntent().getStringExtra("tag");
        BaseFragment fragment = null;
        switch (tag) {
            case "group" :
                fragment = new TestGroupFragment();
                binding.titleBar.setTitle("Test group functions");
                break;
            case "presence" :
                fragment = new TestPresenceFragment();
                binding.titleBar.setTitle("Test presence functions");
                break;
            case "statistics" :
                fragment = new TestStatisticsFragment();
                binding.titleBar.setTitle("Test statistics functions");
                break;
            case "typingListener" :
                fragment = new TestInputListenerFragment();
                binding.titleBar.setTitle("Test typing listener");
                break;
            case "database" :
                fragment = new TestDatabaseFragment();
                binding.titleBar.setTitle("Test database functions");
                break;
        }

        if(fragment == null) {
            finish();
            return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment).commit();
    }

    @Override
    protected void initListener() {
        binding.titleBar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                onBackPressed();
            }
        });
    }
}
