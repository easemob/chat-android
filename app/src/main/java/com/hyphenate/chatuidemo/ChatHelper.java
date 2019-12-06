package com.hyphenate.chatuidemo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chatuidemo.receiver.HeadsetReceiver;
import com.hyphenate.chatuidemo.utils.PreferenceManager;
import com.hyphenate.push.EMPushConfig;

/**
 * 作为hyphenate-sdk的入口控制类，获取sdk下的基础类均通过此类
 */
public class ChatHelper {
    private static final String TAG = "chathelper";
    private static ChatHelper mInstance;

    private ChatHelper() {}

    public static ChatHelper getInstance() {
        if(mInstance == null) {
            synchronized (ChatHelper.class) {
                if(mInstance == null) {
                    mInstance = new ChatHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取IM SDK的入口类
     * @return
     */
    public EMClient getEMClient() {
        return EMClient.getInstance();
    }


    public void init(Context context) {
        EMOptions options = initChatOptions(context);
        EMClient.getInstance().init(context, options);
        EMClient.getInstance().setDebugMode(true);
        PreferenceManager.init(context);
        setCallOptions(context);
    }

    /**
     * 判断是否在主进程
     * @param context
     * @return
     */
    public boolean isMainProcess(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return context.getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }

    private EMOptions initChatOptions(Context context){
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(false);
        // set if you need read ack
        options.setRequireAck(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(false);

        // 设置是否使用 fcm，有些华为设备本身带有 google 服务，
//        options.setUseFCM(demoModel.isUseFCM());

        /**
         * NOTE:你需要设置自己申请的账号来使用三方推送功能，详见集成文档
         */
        EMPushConfig.Builder builder = new EMPushConfig.Builder(context);
        builder.enableVivoPush() // 需要在AndroidManifest.xml中配置appId和appKey
                .enableMeiZuPush("118654", "eaf530ff717f479cab93714d45972ff6")
                .enableMiPush("2882303761517426801", "5381742660801")
                .enableOppoPush("65872dc4c26a446a8f29014f758c8272",
                        "9385ae4308d64b36bf82bc4d73c4369d")
                .enableHWPush() // 需要在AndroidManifest.xml中配置appId
                .enableFCM("921300338324");
        options.setPushConfig(builder.build());

        //set custom servers, commonly used in private deployment
//        if(demoModel.isCustomServerEnable() && demoModel.getRestServer() != null && demoModel.getIMServer() != null) {
//            options.setRestServer(demoModel.getRestServer());
//            options.setIMServer(demoModel.getIMServer());
//            if(demoModel.getIMServer().contains(":")) {
//                options.setIMServer(demoModel.getIMServer().split(":")[0]);
//                options.setImPort(Integer.valueOf(demoModel.getIMServer().split(":")[1]));
//            }
//        }
//
//        if (demoModel.isCustomAppkeyEnabled() && demoModel.getCutomAppkey() != null && !demoModel.getCutomAppkey().isEmpty()) {
//            options.setAppKey(demoModel.getCutomAppkey());
//        }

//        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
//        options.setDeleteMessagesAsExitGroup(getModel().isDeleteMessagesAsExitGroup());
//        options.setAutoAcceptGroupInvitation(getModel().isAutoAcceptGroupInvitation());
//        // Whether the message attachment is automatically uploaded to the Hyphenate server,
//        options.setAutoTransferMessageAttachments(getModel().isSetTransferFileByUser());
//        // Set Whether auto download thumbnail, default value is true.
//        options.setAutoDownloadThumbnail(getModel().isSetAutodownloadThumbnail());
        return options;
    }

    private void setCallOptions(Context context) {
        HeadsetReceiver headsetReceiver = new HeadsetReceiver();
        IntentFilter headsetFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        context.registerReceiver(headsetReceiver, headsetFilter);

        // min video kbps
        int minBitRate = PreferenceManager.getInstance().getCallMinVideoKbps();
        if (minBitRate != -1) {
            EMClient.getInstance().callManager().getCallOptions().setMinVideoKbps(minBitRate);
        }

        // max video kbps
        int maxBitRate = PreferenceManager.getInstance().getCallMaxVideoKbps();
        if (maxBitRate != -1) {
            EMClient.getInstance().callManager().getCallOptions().setMaxVideoKbps(maxBitRate);
        }

        // max frame rate
        int maxFrameRate = PreferenceManager.getInstance().getCallMaxFrameRate();
        if (maxFrameRate != -1) {
            EMClient.getInstance().callManager().getCallOptions().setMaxVideoFrameRate(maxFrameRate);
        }

        // audio sample rate
        int audioSampleRate = PreferenceManager.getInstance().getCallAudioSampleRate();
        if (audioSampleRate != -1) {
            EMClient.getInstance().callManager().getCallOptions().setAudioSampleRate(audioSampleRate);
        }

        /**
         * This function is only meaningful when your app need recording
         * If not, remove it.
         * This function need be called before the video stream started, so we set it in onCreate function.
         * This method will set the preferred video record encoding codec.
         * Using default encoding format, recorded file may not be played by mobile player.
         */
        //EMClient.getInstance().callManager().getVideoCallHelper().setPreferMovFormatEnable(true);

        // resolution
        String resolution = PreferenceManager.getInstance().getCallBackCameraResolution();
        if (resolution.equals("")) {
            resolution = PreferenceManager.getInstance().getCallFrontCameraResolution();
        }
        String[] wh = resolution.split("x");
        if (wh.length == 2) {
            try {
                EMClient.getInstance().callManager().getCallOptions().setVideoResolution(new Integer(wh[0]).intValue(), new Integer(wh[1]).intValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // enabled fixed sample rate
        boolean enableFixSampleRate = PreferenceManager.getInstance().isCallFixedVideoResolution();
        EMClient.getInstance().callManager().getCallOptions().enableFixedVideoResolution(enableFixSampleRate);

        // Offline call push
        EMClient.getInstance().callManager().getCallOptions().setIsSendPushIfOffline(PreferenceManager.getInstance().isPushCall());
    }

}
