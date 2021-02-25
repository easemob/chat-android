package com.hyphenate.easeim.common.receiver;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.easeim.common.utils.PushUtils;
import com.hyphenate.push.platform.mi.EMMiMsgReceiver;
import com.hyphenate.util.EMLog;
import com.xiaomi.mipush.sdk.MiPushMessage;
import org.json.JSONObject;


/**
 * 获取有关小米音视频推送消息
 */
public class MiMsgReceiver extends EMMiMsgReceiver {

    static private String TAG = "MiMsgReceiver";
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        EMLog.i(TAG, "onNotificationMessageClicked is called. " + message.toString());
        String extStr = message.getContent();
        EMLog.i(TAG, "onReceivePassThroughMessage get extras: " + extStr);
        try {
            JSONObject extras = new JSONObject(extStr);
            EMLog.i(TAG, "onReceivePassThroughMessage get extras: " + extras.toString());
            JSONObject object = extras.getJSONObject("e");
            if(object != null){
                PushUtils.isRtcCall = object.getBoolean("isRtcCall");
                PushUtils.type = object.getInt("callType");
                EMLog.i(TAG, "onReceivePassThroughMessage get type: " + PushUtils.type);

            }
        }catch (Exception e){
            e.getStackTrace();
        }
        super.onNotificationMessageClicked(context, message);
    }
}
