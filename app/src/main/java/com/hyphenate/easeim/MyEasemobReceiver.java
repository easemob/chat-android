package com.hyphenate.easeim;

import android.content.Context;

import com.hyphenate.notification.EMNotificationMessage;
import com.hyphenate.notification.core.EMNotificationIntentReceiver;
import com.hyphenate.util.EMLog;

public class MyEasemobReceiver extends EMNotificationIntentReceiver {

    @Override
    public void onNotifyMessageArrived(Context context, EMNotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
        if(!notificationMessage.isNeedNotification()){
            EMLog.d("needNotification:", notificationMessage.getExtras());
        }
    }
}
