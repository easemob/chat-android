package com.hyphenate.easeim.common.constant;

import com.hyphenate.easeui.constants.EaseConstant;

public interface DemoConstant extends EaseConstant {
    String ACCOUNT_CHANGE = "account_change";
    String ACCOUNT_REMOVED = "account_removed";
    String ACCOUNT_CONFLICT = "conflict";
    String ACCOUNT_FORBIDDEN = "user_forbidden";
    String ACCOUNT_KICKED_BY_CHANGE_PASSWORD = "kicked_by_change_password";
    String ACCOUNT_KICKED_BY_OTHER_DEVICE = "kicked_by_another_device";

    String EXTRA_CONFERENCE_ID = "confId";
    String EXTRA_CONFERENCE_PASS = "password";
    String EXTRA_CONFERENCE_INVITER = "inviter";
    String EXTRA_CONFERENCE_IS_CREATOR = "is_creator";
    String EXTRA_CONFERENCE_GROUP_ID = "group_id";
    String EXTRA_CONFERENCE_GROUP_EXIST_MEMBERS = "exist_members";

    String OP_INVITE = "invite";
    String OP_REQUEST_TOBE_SPEAKER = "request_tobe_speaker";
    String OP_REQUEST_TOBE_AUDIENCE = "request_tobe_audience";

    String EM_CONFERENCE_OP = "em_conference_op";
    String EM_CONFERENCE_ID = "em_conference_id";
    String EM_CONFERENCE_PASSWORD = "em_conference_password";
    String EM_CONFERENCE_TYPE = "em_conference_type";
    String EM_MEMBER_NAME = "em_member_name";
    String EM_NOTIFICATION_TYPE = "em_notification_type";

    String MSG_ATTR_CONF_ID = "conferenceId";
    String MSG_ATTR_CONF_PASS = EXTRA_CONFERENCE_PASS;
    String MSG_ATTR_EXTENSION = "msg_extension";

    String NEW_FRIENDS_USERNAME = "item_new_friends";
    String GROUP_USERNAME = "item_groups";
    String CHAT_ROOM = "item_chatroom";
    String CHAT_ROBOT = "item_robots";

    String NOTIFY_GROUP_INVITE_RECEIVE = "invite_receive";
    String NOTIFY_GROUP_INVITE_ACCEPTED = "invite_accepted";
    String NOTIFY_GROUP_INVITE_DECLINED = "invite_declined";
    String NOTIFY_GROUP_JOIN_RECEIVE = "invite_join_receive";
    String NOTIFY_CHANGE = "notify_change";

    String MESSAGE_GROUP_JOIN_ACCEPTED = "message_join_accepted";
    String MESSAGE_GROUP_AUTO_ACCEPT = "message_auto_accept";

    String CONTACT_REMOVE = "contact_remove";
    String CONTACT_ACCEPT = "contact_accept";
    String CONTACT_DECLINE = "contact_decline";
    String CONTACT_BAN = "contact_ban";
    String CONTACT_ALLOW = "contact_allow";

    String CONTACT_CHANGE = "contact_change";
    String CONTACT_ADD = "contact_add";
    String CONTACT_DELETE = "contact_delete";
    String CONTACT_UPDATE = "contact_update";
    String NICK_NAME_CHANGE = "nick_name_change";
    String AVATAR_CHANGE = "avatar_change";
    String REMOVE_BLACK = "remove_black";

    String GROUP_CHANGE = "group_change";
    String GROUP_OWNER_TRANSFER = "group_owner_transfer";
    String GROUP_SHARE_FILE_CHANGE = "group_share_file_change";

    String CHAT_ROOM_CHANGE = "chat_room_change";
    String CHAT_ROOM_DESTROY = "chat_room_destroy";

    String REFRESH_NICKNAME = "refresh_nickname";

    String CONVERSATION_DELETE = "conversation_delete";
    String CONVERSATION_READ = "conversation_read";

    String MESSAGE_NOT_SEND = "message_not_send";

    String SYSTEM_MESSAGE_FROM = "from";
    String SYSTEM_MESSAGE_REASON = "reason";
    String SYSTEM_MESSAGE_STATUS = "status";
    String SYSTEM_MESSAGE_GROUP_ID = "groupId";
    String SYSTEM_MESSAGE_NAME = "name";
    String SYSTEM_MESSAGE_INVITER = "inviter";

    String USER_CARD_EVENT = "userCard";
    String USER_CARD_ID = "uid";
    String USER_CARD_NICK = "nickname";
    String USER_CARD_AVATAR = "avatar";

}
