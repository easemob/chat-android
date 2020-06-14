package com.hyphenate.chatuidemo.common.db.entity;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "app_key", primaryKeys = {"appKey"})
public class AppKeyEntity implements Serializable {

    @NonNull
    private String appKey;

    public AppKeyEntity(@NonNull String appKey) {
        this.appKey = appKey;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(@NonNull String appKey) {
        this.appKey = appKey;
    }
}
