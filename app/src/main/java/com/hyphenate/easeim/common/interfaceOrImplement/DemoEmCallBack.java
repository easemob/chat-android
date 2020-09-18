package com.hyphenate.easeim.common.interfaceOrImplement;

import com.hyphenate.EMCallBack;

/**
 * 作为EMCallBack的抽象类，onError()和onProgress()根据情况进行重写
 */
public abstract class DemoEmCallBack implements EMCallBack {

    @Override
    public void onError(int code, String error) {
        // do something for error
    }

    @Override
    public void onProgress(int progress, String status) {
        // do something in progress
    }
}
