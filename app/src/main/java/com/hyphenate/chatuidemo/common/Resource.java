package com.hyphenate.chatuidemo.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Resource<T> {
    private Status mStatus;
    private T mData;
    private int mErrorCode;
    private String mMessage;

    public Resource(Status status, T data, int errorCode) {
        mStatus = status;
        mData = data;
        mErrorCode = errorCode;
    }

    public static <T> ApiResponse<T> success(@Nullable T data) {
        return new ApiResponse<>(Status.SUCCESS, data, ErrorCode.EM_NO_ERROR);
    }

    public static <T> ApiResponse<T> error(int code, @Nullable T data) {
        return new ApiResponse<>(Status.ERROR, data, code);
    }

    public static <T> ApiResponse<T> loading(@Nullable T data) {
        return new ApiResponse<>(Status.LOADING, data, ErrorCode.EM_NO_ERROR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource<?> resource = (Resource<?>) o;

        if (mErrorCode != resource.mErrorCode) return false;
        if (mStatus != resource.mStatus) return false;
        if (mData != null ? !mData.equals(resource.mData) : resource.mData != null) return false;
        return mMessage != null ? mMessage.equals(resource.mMessage) : resource.mMessage == null;
    }

    @Override
    public int hashCode() {
        int result = mStatus != null ? mStatus.hashCode() : 0;
        result = 31 * result + (mData != null ? mData.hashCode() : 0);
        result = 31 * result + mErrorCode;
        result = 31 * result + (mMessage != null ? mMessage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "mStatus=" + mStatus +
                ", mData=" + mData +
                ", mErrorCode=" + mErrorCode +
                ", mMessage='" + mMessage + '\'' +
                '}';
    }
}
