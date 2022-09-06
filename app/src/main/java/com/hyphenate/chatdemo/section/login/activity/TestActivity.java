package com.hyphenate.chatdemo.section.login.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hyphenate.chatdemo.R;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.chatdemo.common.utils.ToastUtils;
import com.hyphenate.chatdemo.section.base.BaseInitActivity;

public class TestActivity extends BaseInitActivity implements View.OnClickListener {
    private Button btn_success_1;
    private Button btn_success_2;
    private Button btn_fail_1;
    private Button btn_fail_2;
    private Button btn_default;
    private Button btn_default_thread;
    private Button btn_success_3;
    private Button btn_success_4;

    public static void startAction(Context context) {
        Intent intent = new Intent(context, TestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_test;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        btn_success_1 = findViewById(R.id.btn_success_1);
        btn_success_2 = findViewById(R.id.btn_success_2);
        btn_fail_1 = findViewById(R.id.btn_fail_1);
        btn_fail_2 = findViewById(R.id.btn_fail_2);
        btn_default = findViewById(R.id.btn_default);
        btn_default_thread = findViewById(R.id.btn_default_thread);
        btn_success_3 = findViewById(R.id.btn_success_3);
        btn_success_4 = findViewById(R.id.btn_success_4);
    }

    @Override
    protected void initListener() {
        super.initListener();
        btn_success_1.setOnClickListener(this);
        btn_success_2.setOnClickListener(this);
        btn_fail_1.setOnClickListener(this);
        btn_fail_2.setOnClickListener(this);
        btn_default.setOnClickListener(this);
        btn_default_thread.setOnClickListener(this);
        btn_success_3.setOnClickListener(this);
        btn_success_4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_success_1 :
                ToastUtils.showSuccessToast("发送成功", "上了飞机数量急死了都放假数量的房间数量肯定放假");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_success_2 :
                //复现7.1.x版本toast crash的情况
                Toast.makeText(TestActivity.this, "上了飞机数量急死了都放假数量的房间数量肯定放假", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_fail_1 :
                ToastUtils.showFailToast("请求失败", "上了飞机数量急死了都放假数量的房间数量肯定放假");
                break;
            case R.id.btn_fail_2 :
                ToastUtils.showFailToast("上了飞机数量急死了都放假数量的房间数量肯定放假");
                break;
            case R.id.btn_default :
                ToastUtils.showToast("上了飞机数量急死了都放假数量的房间数量肯定放假");
                break;
            case R.id.btn_default_thread:
                EaseThreadManager.getInstance().runOnIOThread(()->{
                    ToastUtils.showToast("上了飞机数量急死了都放假数量的房间数量肯定放假");
                });
                break;
            case R.id.btn_success_3:
                ToastUtils.showSuccessToast(R.string.em_login_btn, R.string.em_error_network_error);
                break;
            case R.id.btn_success_4:
                ToastUtils.showSuccessToast(R.string.em_error_network_error);
                break;
        }
    }

/*    private void switchToLogin() {
        if(mLoginFragment == null) {
            mLoginFragment = new LoginFragment();
        }
        replace(mLoginFragment);
    }

    private void switchToRegister() {
        if(mRegisterFragment == null) {
            mRegisterFragment = new RegisterFragment();
        }
        replace(mRegisterFragment);
    }

    private void switchToServer() {
        if(mServerSetFragment == null) {
            mServerSetFragment = new ServerSetFragment();
        }
        replace(mServerSetFragment);
    }

    private void replace(BaseFragment fragment) {
        if(currentFragment != fragment) {
            FragmentTransaction t = getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_from_right,
                            R.anim.slide_out_to_left,
                            R.anim.slide_in_from_left,
                            R.anim.slide_out_to_right
                    );
            if(currentFragment != null) {
                t.hide(currentFragment);
            }
            currentFragment = fragment;
            if(!fragment.isAdded()) {
                t.add(R.id.fl_fragment, fragment).show(fragment).commit();
            }else {
                t.show(fragment).commit();
            }
        }
    }*/
}
