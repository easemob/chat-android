package com.hyphenate.chatuidemo.common.manager;

import android.content.Context;

import com.hyphenate.chatuidemo.DemoApp;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.UserActivityLifecycleCallbacks;
import com.hyphenate.chatuidemo.section.chat.ConferenceActivity;
import com.hyphenate.chatuidemo.section.chat.LiveActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用于处理推送及消息相关
 */
public class PushAndMessageHelper {

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
}
