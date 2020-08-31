package com.hyphenate.chatuidemo.section.me.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.livedatas.LiveDataBus;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.me.viewmodels.OfflinePushSetViewModel;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;

public class OfflinePushNickActivity extends BaseInitActivity implements OnClickListener, TextWatcher {
	private EaseTitleBar titleBar;
	private EditText inputNickName;
	private TextView nicknameDescription;
	private Button saveNickName;
	private OfflinePushSetViewModel viewModel;

	public static void actionStart(Context context) {
	    Intent intent = new Intent(context, OfflinePushNickActivity.class);
	    context.startActivity(intent);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.demo_activity_offline_push;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		titleBar = findViewById(R.id.title_bar);
		inputNickName = (EditText) findViewById(R.id.et_input_nickname);
		saveNickName = (Button) findViewById(R.id.btn_save);
		nicknameDescription = (TextView) findViewById(R.id.tv_nickname_description);
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
		saveNickName.setOnClickListener(this);
		inputNickName.addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		viewModel.updatePushNickname(inputNickName.getText().toString());
	}

	@Override
	protected void initData() {
		super.initData();
		viewModel = new ViewModelProvider(this).get(OfflinePushSetViewModel.class);
		viewModel.getConfigsObservable().observe(this, response -> {
			parseResource(response, new OnResourceParseCallback<EMPushConfigs>() {
				@Override
				public void onSuccess(EMPushConfigs data) {
					if(data != null && !TextUtils.isEmpty(data.getDisplayNickname())) {
						inputNickName.setText(data.getDisplayNickname());
					}
				}
			});
		});
		viewModel.getUpdatePushNicknameObservable().observe(this, response -> {
			parseResource(response, new OnResourceParseCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean data) {
					boolean updateOK= DemoHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(inputNickName.getText().toString());
					if (!updateOK) {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(OfflinePushNickActivity.this, "update nickname failed!",
										Toast.LENGTH_SHORT).show();
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(OfflinePushNickActivity.this, "update nickname success!",
										Toast.LENGTH_SHORT).show();
							}
						});
					}
					LiveDataBus.get().with(DemoConstant.REFRESH_NICKNAME).postValue(true);
					finish();
				}

				@Override
				public void onLoading() {
					super.onLoading();
					showLoading();
				}

				@Override
				public void hideLoading() {
					super.hideLoading();
					dismissLoading();
				}
			});
		});
		viewModel.getPushConfigs();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.toString().length() > 0) {
			nicknameDescription.setTextColor(Color.RED);
		}else{
			nicknameDescription.setTextColor(Color.parseColor("#cccccc"));
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}
}
