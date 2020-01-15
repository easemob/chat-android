package com.hyphenate.chatuidemo.common.db.entity;

import androidx.annotation.StringRes;
import androidx.room.Entity;
import androidx.room.Index;

import java.io.Serializable;

@Entity(tableName = "em_invite_message", primaryKeys = {"id"},
        indices = {@Index(value = {"time"}, unique = true)})
public class InviteMessageEntity implements Serializable {
    private int id;
    private String from;
    private long time;
    private String reason;

    private InviteMessageStatus status;
    private String groupId;
    private String groupName;
    private String groupInviter;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public InviteMessageStatus getStatus() {
        return status;
    }

    public void setStatus(InviteMessageStatus status) {
        this.status = status;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupInviter() {
        return groupInviter;
    }

    public void setGroupInviter(String groupInviter) {
        this.groupInviter = groupInviter;
    }

    public enum InviteMessageStatus {

        //==contact
        /**being invited*/
        BEINVITEED(0),
        /**being refused*/
        BEREFUSED(0),
        /**remote user already agreed*/
        BEAGREED(0),

        //==group application
        /**remote user apply to join*/
        BEAPPLYED(0),
        /**you have agreed to join*/
        AGREED(0),
        /**you refused the join request*/
        REFUSED(0),

        //==group invitation
        /**received remote user's invitation**/
        GROUPINVITATION(0),
        /**remote user accept your invitation**/
        GROUPINVITATION_ACCEPTED(0),
        /**remote user declined your invitation**/
        GROUPINVITATION_DECLINED(0),

        //==multi-device
        /**current user accept contact invitation in other device**/
        MULTI_DEVICE_CONTACT_ACCEPT(0),
        /**current user decline contact invitation in other device**/
        MULTI_DEVICE_CONTACT_DECLINE(0),
        /**current user send contact invite in other device**/
        MULTI_DEVICE_CONTACT_ADD(0),
        /**current user add black list in other device **/
        MULTI_DEVICE_CONTACT_BAN(0),
        /** current user remove someone from black list in other device **/
        MULTI_DEVICE_CONTACT_ALLOW(0),

        /**current user create group in other device*/
        MULTI_DEVICE_GROUP_CREATE(0),
        /**current user destroy group in other device*/
        MULTI_DEVICE_GROUP_DESTROY(0),
        /**current user join group in other device*/
        MULTI_DEVICE_GROUP_JOIN(0),
        /**current user leave group in other device*/
        MULTI_DEVICE_GROUP_LEAVE(0),
        /**current user apply to join group in other device*/
        MULTI_DEVICE_GROUP_APPLY(0),
        /**current user accept group application in other device*/
        MULTI_DEVICE_GROUP_APPLY_ACCEPT(0),
        /**current user refuse group application in other device*/
        MULTI_DEVICE_GROUP_APPLY_DECLINE(0),
        /**current user invite some join group in other device*/
        MULTI_DEVICE_GROUP_INVITE(0),
        /**current user accept group invitation in other device*/
        MULTI_DEVICE_GROUP_INVITE_ACCEPT(0),
        /**current user decline group invitation in other device*/
        MULTI_DEVICE_GROUP_INVITE_DECLINE(0),
        /**current user kick some one out of group in other device*/
        MULTI_DEVICE_GROUP_KICK(0),
        /**current user add some one into group black list in other device*/
        MULTI_DEVICE_GROUP_BAN(0),
        /**current user remove some one from group black list in other device*/
        MULTI_DEVICE_GROUP_ALLOW(0),
        /**current user block group message in other device*/
        MULTI_DEVICE_GROUP_BLOCK(0),
        /**current user unblock group message in other device*/
        MULTI_DEVICE_GROUP_UNBLOCK(0),
        /**current user assign group owner to some one else in other device*/
        MULTI_DEVICE_GROUP_ASSIGN_OWNER(0),
        /**current user add group admin in other device*/
        MULTI_DEVICE_GROUP_ADD_ADMIN(0),
        /**current user remove group admin in other device*/
        MULTI_DEVICE_GROUP_REMOVE_ADMIN(0),
        /**current user mute some one in group in other device*/
        MULTI_DEVICE_GROUP_ADD_MUTE(0),
        /**current user unmute some one in group in other device*/
        MULTI_DEVICE_GROUP_REMOVE_MUTE(0);

        private int msgContent;

        private InviteMessageStatus(@StringRes int msgContent) {
            this.msgContent = msgContent;
        }

    }
}
