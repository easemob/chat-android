package com.hyphenate.easeim.section.me.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.EMLog;

/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

/**
 * Diagnose activity；user can upload log for debug purpose
 * 
 * @author lyuzhao
 * 
 */
public class DiagnoseActivity extends BaseInitActivity{
	private EaseTitleBar titleBar;
	private TextView currentVersion;
	private Button uploadLog;

	public static void actionStart(Context context) {
	    Intent starter = new Intent(context, DiagnoseActivity.class);
	    context.startActivity(starter);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.demo_activity_diagnose;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		titleBar = findViewById(R.id.title_bar);
		currentVersion = (TextView) findViewById(R.id.tv_version);
		uploadLog = (Button) findViewById(R.id.button_uploadlog);
	}

	@Override
	protected void initListener() {
		super.initListener();
		titleBar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
			@Override
			public void onBackPress(View view) {
				onBackPressed();
			}
		});
		uploadLog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				uploadlog();
			}
		});
	}

	@Override
	protected void initData() {
		super.initData();
		String strVersion = "";
		try {
			strVersion = getVersionName();
		} catch (Exception e) {
		}
		if (!TextUtils.isEmpty(strVersion))
			currentVersion.setText("V" + strVersion);
		else{
			String st = getResources().getString(R.string.Not_Set);
			currentVersion.setText(st);}
	}

	private String getVersionName() throws Exception {
		return EMClient.getInstance().VERSION;
	}

	private ProgressDialog progressDialog;

	public void uploadlog() {

		if (progressDialog == null)
			progressDialog = new ProgressDialog(this);
		String stri = getResources().getString(R.string.Upload_the_log);
		progressDialog.setMessage(stri);
		progressDialog.setCancelable(false);
		progressDialog.show();
		final String st = getResources().getString(R.string.Log_uploaded_successfully);
		try {
			EMClient.getInstance().uploadLog(new EMCallBack() {

				@Override
				public void onSuccess() {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(DiagnoseActivity.this, st,
									Toast.LENGTH_SHORT).show();
						}
					});
				}

				@Override
				public void onProgress(final int progress, String status) {
					// getActivity().runOnUiThread(new Runnable() {
					//
					// @Override
					// public void run() {
					// progressDialog.setMessage("上传中 "+progress+"%");
					//
					// }
					// });

				}
				@Override
				public void onError(int code, String message) {
					EMLog.e("###", message);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							progressDialog.dismiss();
							String st3 = getResources().getString(R.string.Log_Upload_failed);
							Toast.makeText(DiagnoseActivity.this, st3,
									Toast.LENGTH_SHORT).show();
						}
					});

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
