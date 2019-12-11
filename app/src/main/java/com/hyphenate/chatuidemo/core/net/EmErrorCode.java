package com.hyphenate.chatuidemo.core.net;

import com.hyphenate.EMError;
import com.hyphenate.chatuidemo.R;

/**
 * 定义一些本地的错误code
 */
public class EmErrorCode extends EMError {
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

    public enum Error {
        EM_NETWORK_ERROR(EmErrorCode.EM_NETWORK_ERROR, R.string.em_error_network_error),
        EM_NOT_LOGIN(EmErrorCode.EM_NOT_LOGIN, R.string.em_error_not_login),
        EM_PARSE_ERROR(EmErrorCode.EM_PARSE_ERROR, R.string.em_error_parse_error),
        EM_ERR_UNKNOWN(EmErrorCode.EM_ERR_UNKNOWN, R.string.em_error_err_unknown),
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
