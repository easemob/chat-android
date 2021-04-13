package com.hyphenate.easeim.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.me.viewmodels.OfflinePushSetViewModel;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.EMLog;
import com.hyphenate.chat.EMUserInfo.*;
import androidx.lifecycle.ViewModelProvider;

public class OfflinePushNickActivity extends BaseInitActivity implements OnClickListener, TextWatcher {
	static private String TAG =  "OfflinePushNickActivity";
	private EaseTitleBar titleBar;
	private EditText inputNickName;
	private Button saveNickName;
	private String nickName;
	private OfflinePushSetViewModel viewModel;


	public static void actionStart(Context context) {
	    Intent intent = new Intent(context, OfflinePushNickActivity.class);
	    context.startActivity(intent);
	}

	@Override
	protected void initIntent(Intent intent) {
		super.initIntent(intent);
		if(intent != null){
			nickName = intent.getStringExtra("nickName");
		}
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
		if(v.getId() == R.id.btn_save) {
			String nick = inputNickName.getText().toString();
			if (nick != null && nick.length() > 0) {
				EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfoType.NICKNAME, nick, new EMValueCallBack<String>() {
					@Override
					public void onSuccess(String value) {
						EMLog.d(TAG, "fetchUserInfoById :" + value);
						showToast(R.string.demo_offline_nickname_update_success);
						nickName = nick;
						PreferenceManager.getInstance().setCurrentUserNick(nick);


						EaseEvent event = EaseEvent.create(DemoConstant.NICK_NAME_CHANGE, EaseEvent.TYPE.CONTACT);
						//发送联系人更新事件
						event.message = nick;
						LiveDataBus.get().with(DemoConstant.NICK_NAME_CHANGE).postValue(event);
						runOnUiThread(new Runnable() {
							public void run() {
								//同时更新推送昵称
								viewModel.updatePushNickname(nick);
							}
						});
					}

					@Override
					public void onError(int error, String errorMsg) {
						EMLog.d(TAG, "fetchUserInfoById  error:" + error + " errorMsg:" + errorMsg);
						showToast(R.string.demo_offline_nickname_update_failed);
					}
				});
			}else{
				showToast(R.string.demo_offline_nickname_is_empty);
			}
		}
	}

	@Override
	protected void initData() {
		super.initData();
		if(nickName != null && nickName.length() > 0){
			inputNickName.setText(nickName);
		}else{
			inputNickName.setText(EMClient.getInstance().getCurrentUser());
		}
		viewModel = new ViewModelProvider(this).get(OfflinePushSetViewModel.class);
		viewModel.getUpdatePushNicknameObservable().observe(this, response -> {
			parseResource(response, new OnResourceParseCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean data) {
					getIntent().putExtra("nickName", nickName);
					setResult(RESULT_OK, getIntent());
					finish();
				}

				@Override
				public void onLoading(Boolean data) {
					super.onLoading(data);
					showLoading();
				}

				@Override
				public void hideLoading() {
					super.hideLoading();
					dismissLoading();
				}
			});
		});
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {

	}
}
