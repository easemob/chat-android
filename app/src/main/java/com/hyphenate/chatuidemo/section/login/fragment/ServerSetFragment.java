package com.hyphenate.chatuidemo.section.login.fragment;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.DialogCallBack;
import com.hyphenate.chatuidemo.common.model.DemoServerSetBean;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class ServerSetFragment extends BaseInitFragment implements EaseTitleBar.OnBackPressListener, CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnClickListener {
    private EaseTitleBar mToolbarServer;
    private Switch mSwitchServer;
    private TextView mEtServerHint;
    private EditText mEtAppkey;
    private Switch mSwitchSpecifyServer;
    private EditText mEtServerAddress;
    private EditText mEtServerPort;
    private EditText mEtServerRest;
    private Switch mSwitchHttpsSet;
    private Button mBtnReset;
    private Button mBtnServer;
    private Group mGroupServerSet;

    private String mServerAddress;
    private String mServerPort;
    private String mRestServerAddress;
    private String mAppkey;
    private boolean mCustomServerEnable;


    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_server_set;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mToolbarServer = findViewById(R.id.toolbar_server);
        mSwitchServer = findViewById(R.id.switch_server);
        mEtServerHint = findViewById(R.id.et_server_hint);
        mEtAppkey = findViewById(R.id.et_appkey);
        mSwitchSpecifyServer = findViewById(R.id.switch_specify_server);
        mEtServerAddress = findViewById(R.id.et_server_address);
        mEtServerPort = findViewById(R.id.et_server_port);
        mEtServerRest = findViewById(R.id.et_server_rest);
        mSwitchHttpsSet = findViewById(R.id.switch_https_set);
        mBtnReset = findViewById(R.id.btn_reset);
        mBtnServer = findViewById(R.id.btn_server);
        mGroupServerSet = findViewById(R.id.group_server_set);

        checkServerSet();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mToolbarServer.setOnBackPressListener(this);
        mSwitchServer.setOnCheckedChangeListener(this);
        mSwitchSpecifyServer.setOnCheckedChangeListener(this);
        mEtAppkey.addTextChangedListener(this);
        mEtServerAddress.addTextChangedListener(this);
        mEtServerPort.addTextChangedListener(this);
        mEtServerRest.addTextChangedListener(this);
        mBtnReset.setOnClickListener(this);
        mBtnServer.setOnClickListener(this);
    }

    /**
     * 检查服务器设置
     */
    private void checkServerSet() {
        boolean isInited = DemoHelper.getInstance().isSDKInit();
        //如果sdk已经初始化完成，则应该显示初始化完成后的数据
        if(isInited) {
            EMOptions options = DemoHelper.getInstance().getEMClient().getOptions();
            boolean enableDNSConfig = options.getEnableDNSConfig();
            mSwitchServer.setChecked(enableDNSConfig);
            mSwitchSpecifyServer.setChecked(enableDNSConfig);
            mEtAppkey.setText(options.getAppKey());
            mEtServerAddress.setText(options.getImServer());
            mEtServerPort.setText(options.getImPort());
            mEtServerRest.setText(options.getRestServer());
            mSwitchHttpsSet.setChecked(options.getUsingHttpsOnly());
            mBtnServer.setEnabled(false);
        }else {
            //判断是否显示设置数据，及是否可以自定义设置
            mCustomServerEnable = DemoHelper.getInstance().isCustomServerEnable();
            mSwitchServer.setChecked(mCustomServerEnable);
            mSwitchSpecifyServer.setChecked(mCustomServerEnable);
            mSwitchHttpsSet.setChecked(DemoHelper.getInstance().getUsingHttpsOnly());
            String appkey = DemoHelper.getInstance().getCutomAppkey();
            mEtAppkey.setText(TextUtils.isEmpty(appkey) ? "":appkey);
            String imServer = DemoHelper.getInstance().getIMServer();
            mEtServerAddress.setText(TextUtils.isEmpty(imServer) ? "" : imServer);
            int imServerPort = DemoHelper.getInstance().getIMServerPort();
            mEtServerPort.setText(imServerPort == 0 ? "" : imServerPort+"");
            String restServer = DemoHelper.getInstance().getRestServer();
            mEtServerRest.setText(TextUtils.isEmpty(restServer) ? "" : restServer);
        }
        mEtServerHint.setVisibility(isInited ? View.VISIBLE : View.GONE);
        mEtAppkey.setEnabled(!isInited);
        mSwitchSpecifyServer.setEnabled(!isInited);
        mEtServerAddress.setEnabled(!isInited && mCustomServerEnable);
        mEtServerPort.setEnabled(!isInited && mCustomServerEnable);
        mEtServerRest.setEnabled(!isInited && mCustomServerEnable);
        mSwitchHttpsSet.setEnabled(!isInited && mCustomServerEnable);
    }

    @Override
    public void onBackPress(View view) {
        onBackPress();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_server :
                mGroupServerSet.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                break;
            case R.id.switch_specify_server :
                mCustomServerEnable = isChecked;
                mEtServerAddress.setEnabled(isChecked);
                mEtServerPort.setEnabled(isChecked);
                mEtServerRest.setEnabled(isChecked);
                mSwitchHttpsSet.setEnabled(isChecked);
                checkButtonEnable();
                break;
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mAppkey = mEtAppkey.getText().toString().trim();
        DemoHelper.getInstance().enableCustomAppkey(!TextUtils.isEmpty(mAppkey));
        Log.e("TAG", "appkey= "+mAppkey+ " enable = "+mCustomServerEnable + " isEmpty = "+ !TextUtils.isEmpty(mAppkey));
        mServerAddress = mEtServerAddress.getText().toString().trim();
        mServerPort = mEtServerPort.getText().toString().trim();
        mRestServerAddress = mEtServerRest.getText().toString().trim();
        checkButtonEnable();

    }

    private void checkButtonEnable() {
        if(mCustomServerEnable) {
            setButtonEnable(!TextUtils.isEmpty(mServerAddress)
                    && !TextUtils.isEmpty(mAppkey)
                    && !TextUtils.isEmpty(mServerPort)
                    && !TextUtils.isEmpty(mRestServerAddress));
        }else {
            setButtonEnable(!TextUtils.isEmpty(mAppkey));
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_server) {
            saveServerSet();
        }else if(v.getId() == R.id.btn_reset) {

            showDialog(R.string.em_server_set_dialog_reset, new DialogCallBack() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DemoServerSetBean set = DemoHelper.getInstance().getDefServerSet();
                    mEtAppkey.setText(set.getAppkey());
                    mEtServerAddress.setText(set.getImServer());
                    mEtServerPort.setText(set.getImPort());
                    mEtServerRest.setText(set.getRestServer());
                }
            });

        }
    }

    private void saveServerSet() {
        if(mCustomServerEnable) {
            if(TextUtils.isEmpty(mAppkey) && !TextUtils.isEmpty(mServerAddress)
                    && !TextUtils.isEmpty(mServerPort) && !TextUtils.isEmpty(mRestServerAddress)) {
                // 保存设置
                DemoHelper.getInstance().setCustomAppkey(mAppkey);
                DemoHelper.getInstance().enableCustomServer(mCustomServerEnable);
                DemoHelper.getInstance().setIMServer(mServerAddress);
                DemoHelper.getInstance().setIMServerPort(Integer.valueOf(mServerPort));
                DemoHelper.getInstance().setRestServer(mRestServerAddress);
                DemoHelper.getInstance().setUsingHttpsOnly(mSwitchHttpsSet.isChecked());
            }
        }else {
            if(!TextUtils.isEmpty(mAppkey)) {
                DemoHelper.getInstance().setCustomAppkey(mAppkey);
            }
        }
        //保存成功后，回退到生一个页面
        onBackPress();
    }

    private void setButtonEnable(boolean enable) {
        Log.e("TAG", "setButtonEnable = "+enable);
        mBtnServer.setEnabled(enable);
        //同时需要修改右侧drawalbeRight对应的资源
        Drawable rightDrawable;
        if(enable) {
            rightDrawable = ContextCompat.getDrawable(mContext, R.drawable.em_login_btn_right_enable);
        }else {
            rightDrawable = ContextCompat.getDrawable(mContext, R.drawable.em_login_btn_right_unable);
        }
        mBtnServer.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
    }

}
