package com.hyphenate.chatdemo.section.group;

import android.text.TextUtils;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatdemo.DemoHelper;
import com.hyphenate.util.EMLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupHelper {
    private static final Map<String,Boolean> isFirstTab = new HashMap<>();
    private static final Map<String,Map<String,MemberAttributeBean>> groupMemberAttribute = new HashMap<>();
    private static final Map<String,MemberAttributeBean> map = new HashMap<>();

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
     * 是否是聊天室创建者
     * @return
     */
    public static boolean isOwner(EMChatRoom room) {
        if(room == null ||
                TextUtils.isEmpty(room.getOwner())) {
            return false;
        }
        return TextUtils.equals(room.getOwner(), DemoHelper.getInstance().getCurrentUser());
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
     * 是否是管理员
     * @return
     */
    public synchronized static boolean isAdmin(EMChatRoom group) {
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
        return group != null && (group.isMemberAllowToInvite() || isOwner(group) || isAdmin(group));
    }

    /**
     * 在黑名单中
     * @param username
     * @return
     */
    public static boolean isInAdminList(String username, List<String> adminList) {
        return isInList(username, adminList);
    }

    /**
     * 在黑名单中
     * @param username
     * @return
     */
    public static boolean isInBlackList(String username, List<String> blackMembers) {
        return isInList(username, blackMembers);
    }

    /**
     * 在禁言名单中
     * @param username
     * @return
     */
    public static boolean isInMuteList(String username, List<String> muteMembers) {
        return isInList(username, muteMembers);
    }

    /**
     * 是否在列表中
     * @param name
     * @return
     */
    public static boolean isInList(String name, List<String> list) {
        if(list == null) {
            return false;
        }
        synchronized (GroupHelper.class) {
            for (String item : list) {
                if (TextUtils.equals(name, item)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取群名称
     * @param groupId
     * @return
     */
    public static String getGroupName(String groupId) {
        EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
        if(group == null) {
            return groupId;
        }
        return TextUtils.isEmpty(group.getGroupName()) ? groupId : group.getGroupName();
    }

    /**
     * 判断是否加入了群组
     * @param allJoinGroups 所有加入的群组
     * @param groupId
     * @return
     */
    public static boolean isJoinedGroup(List<EMGroup> allJoinGroups, String groupId) {
        if(allJoinGroups == null || allJoinGroups.isEmpty()) {
            return false;
        }
        for (EMGroup group : allJoinGroups) {
            if(TextUtils.equals(group.getGroupId(), groupId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 存储群组成员属性
     * @param groupId
     * @param userName
     * @param bean
     */
    public static void saveMemberAttribute(String groupId,String userName,MemberAttributeBean bean){
        map.put(userName,bean);
        for (Map.Entry<String, MemberAttributeBean> entry : map.entrySet()) {
            EMLog.d("apex-wt","saveMap:  \n"+  "userId: " +entry.getKey() + " getNickName: " + entry.getValue().getNickName());
        }
        groupMemberAttribute.put(groupId,map);
    }

    /**
     * 获取群组成员属性
     * @param groupId
     * @param userId
     * @return
     */
    public static MemberAttributeBean getMemberAttribute(String groupId,String userId){
        MemberAttributeBean attributeBean = null;
        for (Map.Entry<String, Map<String, MemberAttributeBean>> entry : groupMemberAttribute.entrySet()) {
            Map<String, MemberAttributeBean> map = entry.getValue();
            String key = entry.getKey();
            for (Map.Entry<String, MemberAttributeBean> beanEntry : map.entrySet()) {
                EMLog.d("apex-wt", "groupMemberAttribute: " + "\n" + "groupId: " + key+ " userId: " + beanEntry.getKey() + " getNickName: " + beanEntry.getValue().getNickName());
            }
        }
        if (!TextUtils.isEmpty(groupId) && !TextUtils.isEmpty(userId)){
            for (Map.Entry<String, Map<String, MemberAttributeBean>> entry : groupMemberAttribute.entrySet()) {
                EMLog.d("apex-wt","groupMemberAttribute key: " + entry.getKey());
            }
            if (groupMemberAttribute.containsKey(groupId)){
                Map<String,MemberAttributeBean> map = groupMemberAttribute.get(groupId);
                if (map != null ){
                    if (map.containsKey(userId)){
                        attributeBean = map.get(userId);
                    }
                }
            }
        }
        return attributeBean;
    }

    /**
     * 清除指定群组成员属性
     * @param groupId
     */
    public static void clearGroupMemberAttribute(String groupId){
        groupMemberAttribute.remove(groupId);
    }

    public static void clearAllMemberAttribute(){
        groupMemberAttribute.clear();
    }

    /**
     * 设置标记 （单进程第一次进入群组获取信息）
     * @param groupId
     */
    public static void setFirstTab(String groupId){
        isFirstTab.put(groupId,true);
    }

    /**
     * 获取标记
     * @param groupId
     * @return
     */
    public static boolean isFirstTabByGroup(String groupId){
        return !isFirstTab.containsKey(groupId);
    }

}
