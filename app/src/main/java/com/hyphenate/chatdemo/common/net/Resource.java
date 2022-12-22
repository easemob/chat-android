package com.hyphenate.chatdemo.common.net;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.hyphenate.chatdemo.DemoApplication;
import com.hyphenate.chatdemo.common.enums.Status;

public class Resource<T> {
    public Status status;
    public T data;
    public int errorCode;
    private String message;
    private int messageId;

    public Resource(Status status, T data, int errorCode) {
        this.status = status;
        this.data = data;
        this.errorCode = errorCode;
        this.messageId = ErrorCode.Error.parseMessage(errorCode).getMessageId();
    }

    public Resource(Status status, T data, int errorCode, String message) {
        this.status = status;
        this.data = data;
        this.errorCode = errorCode;
        this.message = message;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, ErrorCode.EM_NO_ERROR);
    }

    public static <T> Resource<T> error(int code, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, code);
    }

    public static <T> Resource<T> error(int code, String message, @Nullable T data) {
        return TextUtils.isEmpty(message) ?
                new Resource<>(Status.ERROR, data, code) :
                new Resource<>(Status.ERROR, data, code, message);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, ErrorCode.EM_NO_ERROR);
    }

    /**
     * 获取错误信息
     * @return
     */
    public String getMessage() {
        if(!TextUtils.isEmpty(message)) {
            return message;
        }
        if(messageId > 0) {
            return DemoApplication.getInstance().getString(messageId);
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource<?> resource = (Resource<?>) o;

        if (errorCode != resource.errorCode) return false;
        if (status != resource.status) return false;
        if (data != null ? !data.equals(resource.data) : resource.data != null) return false;
        return message != null ? message.equals(resource.message) : resource.message == null;
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + errorCode;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "mStatus=" + status +
                ", data=" + data +
                ", errorCode=" + errorCode +
                ", message='" + message + '\'' +
                '}';
    }
}
