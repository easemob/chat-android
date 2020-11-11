package com.hyphenate.easeim.common.db.entity;

import androidx.annotation.StringRes;

import com.hyphenate.easeim.R;

public enum InviteMessageStatus {

        //==contact
        /**being invited*/
        BEINVITEED(R.string.demo_contact_listener_onContactInvited),
        /**being refused*/
        BEREFUSED(R.string.demo_contact_listener_onFriendRequestDeclined),
        /**remote user already agreed*/
        BEAGREED(R.string.demo_contact_listener_onFriendRequestAccepted),

        //==group application
        /**remote user apply to join*/
        BEAPPLYED(R.string.demo_group_listener_onRequestToJoinReceived),
        /**you have agreed to join*/
        AGREED(R.string.Has_agreed_to),
        /**you refused the join request*/
        REFUSED(R.string.Has_refused_to),

        //==group invitation
        /**received remote user's invitation**/
        GROUPINVITATION(R.string.demo_group_listener_onInvitationReceived),
        /**remote user accept your invitation**/
        GROUPINVITATION_ACCEPTED(R.string.demo_group_listener_onInvitationAccepted),
        /**remote user declined your invitation**/
        GROUPINVITATION_DECLINED(R.string.demo_system_other_decline_received_remote_user_invitation),

        //==multi-device
        /**current user accept contact invitation in other device**/
        MULTI_DEVICE_CONTACT_ACCEPT(R.string.multi_device_contact_accept),
        /**current user decline contact invitation in other device**/
        MULTI_DEVICE_CONTACT_DECLINE(R.string.multi_device_contact_decline),
        /**current user send contact invite in other device**/
        MULTI_DEVICE_CONTACT_ADD(R.string.multi_device_contact_add),
        /**current user add black list in other device **/
        MULTI_DEVICE_CONTACT_BAN(R.string.multi_device_contact_ban),
        /** current user remove someone from black list in other device **/
        MULTI_DEVICE_CONTACT_ALLOW(R.string.multi_device_contact_allow),

        /**current user create group in other device*/
        MULTI_DEVICE_GROUP_CREATE(R.string.multi_device_group_create),
        /**current user destroy group in other device*/
        MULTI_DEVICE_GROUP_DESTROY(R.string.multi_device_group_destroy),
        /**current user join group in other device*/
        MULTI_DEVICE_GROUP_JOIN(R.string.multi_device_group_join),
        /**current user leave group in other device*/
        MULTI_DEVICE_GROUP_LEAVE(R.string.multi_device_group_leave),
        /**current user apply to join group in other device*/
        MULTI_DEVICE_GROUP_APPLY(R.string.multi_device_group_apply),
        /**current user accept group application in other device*/
        MULTI_DEVICE_GROUP_APPLY_ACCEPT(R.string.multi_device_group_apply_accept),
        /**current user refuse group application in other device*/
        MULTI_DEVICE_GROUP_APPLY_DECLINE(R.string.multi_device_group_apply_decline),
        /**current user invite some join group in other device*/
        MULTI_DEVICE_GROUP_INVITE(R.string.multi_device_group_invite),
        /**current user accept group invitation in other device*/
        MULTI_DEVICE_GROUP_INVITE_ACCEPT(R.string.multi_device_group_invite_accept),
        /**current user decline group invitation in other device*/
        MULTI_DEVICE_GROUP_INVITE_DECLINE(R.string.multi_device_group_invite_decline),
        /**current user kick some one out of group in other device*/
        MULTI_DEVICE_GROUP_KICK(R.string.multi_device_group_kick),
        /**current user add some one into group black list in other device*/
        MULTI_DEVICE_GROUP_BAN(R.string.multi_device_group_ban),
        /**current user remove some one from group black list in other device*/
        MULTI_DEVICE_GROUP_ALLOW(R.string.multi_device_group_allow),
        /**current user block group message in other device*/
        MULTI_DEVICE_GROUP_BLOCK(R.string.multi_device_group_block),
        /**current user unblock group message in other device*/
        MULTI_DEVICE_GROUP_UNBLOCK(R.string.multi_device_group_unblock),
        /**current user assign group owner to some one else in other device*/
        MULTI_DEVICE_GROUP_ASSIGN_OWNER(R.string.multi_device_group_assign_owner),
        /**current user add group admin in other device*/
        MULTI_DEVICE_GROUP_ADD_ADMIN(R.string.multi_device_group_add_admin),
        /**current user remove group admin in other device*/
        MULTI_DEVICE_GROUP_REMOVE_ADMIN(R.string.multi_device_group_remove_admin),
        /**current user mute some one in group in other device*/
        MULTI_DEVICE_GROUP_ADD_MUTE(R.string.multi_device_group_add_mute),
        /**current user unmute some one in group in other device*/
        MULTI_DEVICE_GROUP_REMOVE_MUTE(R.string.multi_device_group_remove_mute);

        private int msgContent;

        private InviteMessageStatus(@StringRes int msgContent) {
            this.msgContent = msgContent;
        }

        public int getMsgContent() {
            return msgContent;
        }
    }