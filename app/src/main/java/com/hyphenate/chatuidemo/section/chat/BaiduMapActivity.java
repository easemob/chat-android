package com.hyphenate.chatuidemo.section.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.baidu.location.BDLocation;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.chat.fragment.BaiduMapFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class BaiduMapActivity extends BaseInitActivity implements BaiduMapFragment.OnBDLocationListener, EaseTitleBar.OnRightClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private BDLocation lastLocation;

    public static void actionStartForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, BaiduMapActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    public static void actionStart(Context context, double latitude, double longtitude, String address) {
        Intent intent = new Intent(context, BaiduMapActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longtitude", longtitude);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_common_fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);

        titleBar.setTitle(getResources().getString(R.string.em_map_title));
        titleBar.setRightTitleResource(R.string.em_map_title);

        if(getIntent().getDoubleExtra("latitude", 0) == 0) {
            titleBar.getRightLayout().setVisibility(View.GONE);
        }else {
            titleBar.getRightLayout().setVisibility(View.VISIBLE);
            titleBar.getRightLayout().setClickable(false);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        BaiduMapFragment fragment = new BaiduMapFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", getIntent().getDoubleExtra("latitude", 0));
        bundle.putDouble("longtitude", getIntent().getDoubleExtra("longtitude", 0));
        bundle.putString("address", getIntent().getStringExtra("address"));
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment).commit();

        fragment.setOnBDLocationListener(this);
    }

    @Override
    public void onReceiveBDLocation(BDLocation bdLocation) {
        lastLocation = bdLocation;
        if(bdLocation != null) {
            titleBar.getRightLayout().setClickable(true);
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onRightClick(View view) {
        sendLocation();
    }

    private void sendLocation() {
        Intent intent = getIntent();
        intent.putExtra("latitude", lastLocation.getLatitude());
        intent.putExtra("longitude", lastLocation.getLongitude());
        intent.putExtra("address", lastLocation.getAddrStr());
        this.setResult(RESULT_OK, intent);
        finish();
    }
}
