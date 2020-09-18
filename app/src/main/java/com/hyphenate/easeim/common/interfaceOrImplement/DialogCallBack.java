package com.hyphenate.easeim.common.interfaceOrImplement;

import android.content.DialogInterface;

public interface DialogCallBack {

    /**
     * 点击事件，一般指点击确定按钮
     * @param dialog
     * @param which
     */
    void onClick(DialogInterface dialog, int which);
}
