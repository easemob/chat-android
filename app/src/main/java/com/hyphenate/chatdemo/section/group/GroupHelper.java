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
    private static final Map<String,MemberAttributeBean> attributeMap = new HashMap<>();

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
        attributeMap.put(userName,bean);
        groupMemberAttribute.put(groupId,attributeMap);
    }

    /**
     * 获取群组成员属性
     * @param groupId
     * @param userId
     * @return
     */
    public static MemberAttributeBean getMemberAttribute(String groupId,String userId){
        MemberAttributeBean attributeBean = null;
        if (!TextUtils.isEmpty(groupId) && !TextUtils.isEmpty(userId)){
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
     * 移除指定群组成员属性 用于自己退出群组
     * @param groupId
     */
    public static void clearGroupMemberAttribute(String groupId){
        groupMemberAttribute.remove(groupId);
        attributeMap.clear();
    }

    /**
     * 移除所有群组成员属性 用于退出登录
     */
    public static void clearAllGroupMemberAttribute(){
        groupMemberAttribute.clear();
        attributeMap.clear();
    }

    /**
     * 移除指定群组指定成员的属性 用于成员被踢出群组或者有成员离开群组时
     * @param groupId
     * @param userId
     */
    public static void clearGroupMemberAttributeByUserId(String groupId,String userId){
        if (groupMemberAttribute.containsKey(groupId)){
           Map<String,MemberAttributeBean> map = groupMemberAttribute.get(groupId);
           if (map != null){
               map.remove(userId);
               attributeMap.remove(userId);
           }
        }
    }
}
