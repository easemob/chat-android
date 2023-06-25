package com.hyphenate.chatdemo.common.service;

import com.hihonor.push.sdk.HonorMessageService;
import com.hihonor.push.sdk.HonorPushDataMsg;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

public class HONORPushService extends HonorMessageService {

    //Token发生变化时，会以onNewToken方法返回
    @Override
    public void onNewToken(String token) {
        if(token != null && !token.equals("")){
            //没有失败回调，假定token失败时token为null
            EMLog.d("HONORPush", "service register honor push token success token:" + token);
            EMClient.getInstance().sendHonorPushTokenToServer(token);
        }else{
            EMLog.e("HONORPush", "service register honor push token fail!");
        }
    }

    @Override
    public void onMessageReceived(HonorPushDataMsg honorPushDataMsg) {
        EMLog.d("HONORPush", "onMessageReceived" + honorPushDataMsg.getData());
    }

}
