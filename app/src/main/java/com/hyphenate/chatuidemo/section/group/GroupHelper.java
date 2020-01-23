package com.hyphenate.chatuidemo.section.group;

import android.text.TextUtils;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.DemoHelper;

import java.util.List;

public class GroupHelper {

    /**
     * 是否是群主
     * @return
     */
    public static boolean isOwner(EMGroup group) {
        if(group == null ||
                TextUtils.isEmpty(group.getOwner())) {
            return false;
        }
        return TextUtils.equals(group.getOwner(), DemoHelper.getInstance().getCurrentUser());
    }

    /**
     * 是否是管理员
     * @return
     */
    public synchronized static boolean isAdmin(EMGroup group) {
        List<String> adminList = group.getAdminList();
        if(adminList != null && !adminList.isEmpty()) {
            return adminList.contains(DemoHelper.getInstance().getCurrentUser());
        }
        return false;
    }

    /**
     * 是否有邀请权限
     * @return
     */
    public static boolean isCanInvite(EMGroup group) {
        return group.isMemberAllowToInvite() || isOwner(group) || isAdmin(group);
    }

    /**
     * 在黑名单中
     * @param username
     * @return
     */
    public static boolean isInAdminList(EMGroup group, String username) {
        synchronized (GroupMemberAuthorityActivity.class) {
            List<String> adminList = group.getAdminList();
            for (String item : adminList) {
                if (TextUtils.equals(username, item)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 在黑名单中
     * @param username
     * @return
     */
    public static boolean isInBlackList(String username, List<String> blackMembers) {
        synchronized (GroupMemberAuthorityActivity.class) {
            for (String item : blackMembers) {
                if (TextUtils.equals(username, item)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 在禁言名单中
     * @param username
     * @return
     */
    public static boolean isInMuteList(String username, List<String> muteMembers) {
        synchronized (GroupMemberAuthorityActivity.class) {
            for (String item : muteMembers) {
                if (TextUtils.equals(username, item)) {
                    return true;
                }
            }
        }
        return false;
    }
}
