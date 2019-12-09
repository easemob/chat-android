package com.hyphenate.chatuidemo.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 资源结果包装类，此类反应资源获取的状态和结果
 * @param <T>
 */
public class ApiResponse<T> {
    @NonNull
    public final Status status;

    @Nullable
    public final String message = "";

    public final int code;

    @Nullable
    public final T data;

    public ApiResponse(@NonNull Status status, @Nullable T data, @Nullable int code) {
        this.status = status;
        this.data = data;
        this.code = code;
    }

    public static <T> ApiResponse<T> success(@Nullable T data) {
        return new ApiResponse<>(Status.SUCCESS, data, EmErrorCode.EM_NO_ERROR);
    }

    public static <T> ApiResponse<T> error(int code, @Nullable T data) {
        return new ApiResponse<>(Status.ERROR, data, code);
    }

    public static <T> ApiResponse<T> loading(@Nullable T data) {
        return new ApiResponse<>(Status.LOADING, data, EmErrorCode.EM_NO_ERROR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApiResponse<?> resource = (ApiResponse<?>) o;

        if (status != resource.status) {
            return false;
        }
        if (message != null ? !message.equals(resource.message) : resource.message != null) {
            return false;
        }
        return data != null ? data.equals(resource.data) : resource.data == null;
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
