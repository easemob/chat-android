package com.hyphenate.easeim.common.interfaceOrImplement;

import com.hyphenate.EMValueCallBack;

public abstract class ResultCallBack<T> implements EMValueCallBack<T> {

    /**
     * 针对只返回error code的情况
     * @param error
     */
    public void onError(int error) {
        onError(error, null);
    }
}
