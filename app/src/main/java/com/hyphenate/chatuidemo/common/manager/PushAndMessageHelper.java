package com.hyphenate.chatuidemo.common.manager;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chatuidemo.DemoApp;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.UserActivityLifecycleCallbacks;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.common.utils.ToastUtils;
import com.hyphenate.chatuidemo.section.chat.ConferenceActivity;
import com.hyphenate.chatuidemo.section.chat.LiveActivity;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 用于处理推送及消息相关
 */
public class PushAndMessageHelper {

    private static boolean isLock;

    /**
     * 跳转到LiveActivity
     * @param context
     * @param confId
     * @param password
     * @param inviter
     */
    public static void goLive(Context context, String confId, String password, String inviter) {
        if(isDuringMediaCommunication()) {
            return;
        }
        LiveActivity.watch(context, confId, password, inviter);
    }

    /**
     * 处理会议邀请
     * @param confId 会议 id
     * @param password 会议密码
     */
    public static void goConference(Context context, String confId, String password, String extension) {
        if(isDuringMediaCommunication()) {
            return;
        }
        String inviter = "";
        String groupId = null;
        try {
            JSONObject jsonObj = new JSONObject(extension);
            inviter = jsonObj.optString(DemoConstant.EXTRA_CONFERENCE_INVITER);
            groupId = jsonObj.optString(DemoConstant.EXTRA_CONFERENCE_GROUP_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ConferenceActivity.receiveConferenceCall(context, confId, password, inviter, groupId);
    }

    private static boolean isDuringMediaCommunication() {
        UserActivityLifecycleCallbacks lifecycle = DemoApp.getInstance().getActivityLifecycle();
        String topClassName = lifecycle.current().getClass().getSimpleName();
        if (lifecycle.count() > 0 && ("LiveActivity".equals(topClassName) || "ConferenceActivity".equals(topClassName))) {
            return true;
        }
        return false;
    }

    /**
     * 转发消息
     * @param toChatUsername
     * @param msgId
     */
    public static void sendForwardMessage(String toChatUsername, String msgId) {
        if(TextUtils.isEmpty(msgId)) {
            return;
        }
        EMMessage message = DemoHelper.getInstance().getChatManager().getMessage(msgId);
        EMMessage.Type type = message.getType();
        switch (type) {
            case TXT:
                if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
                    sendBigExpressionMessage(toChatUsername, ((EMTextMessageBody) message.getBody()).getMessage(),
                            message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
                }else{
                    // get the content and send it
                    String content = ((EMTextMessageBody) message.getBody()).getMessage();
                    sendTextMessage(toChatUsername, content);
                }
                break;
            case IMAGE:
                // send image
                String filePath = ((EMImageMessageBody) message.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // send thumb nail if original image does not exist
                        filePath = ((EMImageMessageBody) message.getBody()).thumbnailLocalPath();
                    }
                    sendImageMessage(toChatUsername, filePath);
                }
                break;
        }
    }

    /**
     * send big expression message
     * @param toChatUsername
     * @param name
     * @param identityCode
     */
    private static void sendBigExpressionMessage(String toChatUsername, String name, String identityCode){
        EMMessage message = EaseCommonUtils.createExpressionMessage(toChatUsername, name, identityCode);
        sendMessage(message);
    }

    /**
     * 发送文本消息
     * @param toChatUsername
     * @param content
     */
    private static void sendTextMessage(String toChatUsername, String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        sendMessage(message);
    }

    /**
     * send image message
     * @param toChatUsername
     * @param imagePath
     */
    private static void sendImageMessage(String toChatUsername, String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
        sendMessage(message);
    }


    /**
     * send message
     * @param message
     */
    private static void sendMessage(EMMessage message) {
        // send message
        EMClient.getInstance().chatManager().sendMessage(message);

    }
}
