package com.hyphenate.easeim.common.net;

import com.hyphenate.EMError;
import com.hyphenate.easeim.R;

/**
 * 定义一些本地的错误code
 */
public class ErrorCode extends EMError {
    /**
     * 当前网络不可用
     */
    public static final int EM_NETWORK_ERROR = -2;

    /**
     * 未登录过环信
     */
    public static final int EM_NOT_LOGIN = -8;

    /**
     * result解析错误
     */
    public static final int EM_PARSE_ERROR = -10;

    /**
     * 网络问题请稍后重试
     */
    public static final int EM_ERR_UNKNOWN = -20;

    /**
     * 安卓版本问题,只支持4.4以上
     */
    public static final int EM_ERR_IMAGE_ANDROID_MIN_VERSION = -50;

    /**
     * 文件不存在
     */
    public static final int EM_ERR_FILE_NOT_EXIST = -55;

    /**
     * 添加自己为好友
     */
    public static final int EM_ADD_SELF_ERROR = -100;

    /**
     * 已经是好友
     */
    public static final int EM_FRIEND_ERROR = -101;

    /**
     * 已经添加到黑名单中
     */
    public static final int EM_FRIEND_BLACK_ERROR = -102;

    /**
     * 没有群组成员
     */
    public static final int EM_ERR_GROUP_NO_MEMBERS = -105;


    /**
     * 删除对话失败
     */
    public static final int EM_DELETE_CONVERSATION_ERROR = -110;

    public static final int EM_DELETE_SYS_MSG_ERROR = -115;

    public enum Error {
        EM_NETWORK_ERROR(ErrorCode.EM_NETWORK_ERROR, R.string.em_error_network_error),
        EM_NOT_LOGIN(ErrorCode.EM_NOT_LOGIN, R.string.em_error_not_login),
        EM_PARSE_ERROR(ErrorCode.EM_PARSE_ERROR, R.string.em_error_parse_error),
        EM_ERR_UNKNOWN(ErrorCode.EM_ERR_UNKNOWN, R.string.em_error_err_unknown),
        EM_ERR_IMAGE_ANDROID_MIN_VERSION(ErrorCode.EM_ERR_IMAGE_ANDROID_MIN_VERSION, R.string.em_err_image_android_min_version),
        EM_ERR_FILE_NOT_EXIST(ErrorCode.EM_ERR_FILE_NOT_EXIST, R.string.em_err_file_not_exist),
        EM_ADD_SELF_ERROR(ErrorCode.EM_ADD_SELF_ERROR, R.string.em_add_self_error),
        EM_FRIEND_ERROR(ErrorCode.EM_FRIEND_ERROR, R.string.em_friend_error),
        EM_FRIEND_BLACK_ERROR(ErrorCode.EM_FRIEND_BLACK_ERROR, R.string.em_friend_black_error),
        EM_ERR_GROUP_NO_MEMBERS(ErrorCode.EM_ERR_GROUP_NO_MEMBERS, R.string.em_error_group_no_members),
        EM_DELETE_CONVERSATION_ERROR(ErrorCode.EM_DELETE_CONVERSATION_ERROR, R.string.ease_delete_conversation_error),
        EM_DELETE_SYS_MSG_ERROR(ErrorCode.EM_DELETE_SYS_MSG_ERROR, R.string.em_delete_sys_msg_error),
        USER_ALREADY_EXIST(ErrorCode.USER_ALREADY_EXIST, R.string.em_error_user_already_exist),
        UNKNOWN_ERROR(-9999, 0);


        private int code;
        private int messageId;

        private Error(int code, int messageId) {
            this.code = code;
            this.messageId = messageId;
        }

        public static Error parseMessage(int errorCode) {
            for (Error error: Error.values()) {
                if(error.code == errorCode) {
                    return error;
                }
            }
            return UNKNOWN_ERROR;
        }


        public int getMessageId() {
            return messageId;
        }


    }
}
