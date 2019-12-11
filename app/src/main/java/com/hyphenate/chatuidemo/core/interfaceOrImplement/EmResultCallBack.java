package com.hyphenate.chatuidemo.core.interfaceOrImplement;

import com.hyphenate.EMValueCallBack;

public abstract class EmResultCallBack<T> implements EMValueCallBack<T> {

    /**
     * 针对只返回error code的情况
     * @param error
     */
    public void onError(int error) {
        onError(error, null);
    }
}
