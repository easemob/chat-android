/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.baidu.location.BDLocation;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.ui.base.EaseBaseActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class EaseBaiduMapActivity extends EaseBaseActivity implements EaseTitleBar.OnBackPressListener,
																		EaseTitleBar.OnRightClickListener,
																		EaseBaiduMapFragment.OnBDLocationListener {
	private EaseTitleBar titleBarMap;
	private BDLocation lastLocation;

	public static void actionStartForResult(Fragment fragment, int requestCode) {
		Intent intent = new Intent(fragment.getContext(), EaseBaiduMapActivity.class);
		fragment.startActivityForResult(intent, requestCode);
	}

	public static void actionStartForResult(Activity activity, int requestCode) {
		Intent intent = new Intent(activity, EaseBaiduMapActivity.class);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void actionStart(Context context, double latitude, double longtitude, String address) {
		Intent intent = new Intent(context, EaseBaiduMapActivity.class);
		intent.putExtra("latitude", latitude);
		intent.putExtra("longtitude", longtitude);
		intent.putExtra("address", address);
		context.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ease_activity_baidumap);
		setFitSystemForTheme(true);
		initView();
		initListener();
		initData();
	}

	private void initView() {
		titleBarMap = findViewById(R.id.title_bar_map);
		titleBarMap.setTitle(getResources().getString(R.string.ease_map_title));
		titleBarMap.setRightTitleResource(R.string.button_send);
		double latitude = getIntent().getDoubleExtra("latitude", 0);
		if(latitude != 0) {
			titleBarMap.getRightLayout().setVisibility(View.GONE);
		}else {
			titleBarMap.getRightLayout().setVisibility(View.VISIBLE);
			titleBarMap.getRightLayout().setClickable(false);
		}
	}

	private void initListener() {
		titleBarMap.setOnBackPressListener(this);
		titleBarMap.setOnRightClickListener(this);
	}

	private void initData() {
		EaseBaiduMapFragment fragment = new EaseBaiduMapFragment();
		Bundle bundle = new Bundle();
		bundle.putDouble("latitude", getIntent().getDoubleExtra("latitude", 0));
		bundle.putDouble("longtitude", getIntent().getDoubleExtra("longtitude", 0));
		bundle.putString("address", getIntent().getStringExtra("address"));
		fragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment).commit();

		fragment.setOnBDLocationListener(this);
	}

	@Override
	public void onBackPress(View view) {
		onBackPressed();
	}

	@Override
	public void onRightClick(View view) {
		sendLocation();
	}

	@Override
	public void onReceiveBDLocation(BDLocation bdLocation) {
		lastLocation = bdLocation;
		if(bdLocation != null) {
			titleBarMap.getRightLayout().setClickable(true);
		}
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
