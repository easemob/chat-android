package com.hyphenate.easeim.common.db.entity;

import android.text.TextUtils;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import com.hyphenate.easeim.DemoApplication;
import com.hyphenate.easeim.common.db.DemoDbHelper;

import java.io.Serializable;

@Entity(tableName = "em_msg_type", primaryKeys = {"id"},
        indices = {@Index(value = {"type"}, unique = true)})
public class MsgTypeManageEntity implements Serializable {
    private int id;
    private String type;
    private String extField;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtField() {
        return extField;
    }

    public void setExtField(String extField) {
        this.extField = extField;
    }

    @Ignore
    public Object getLastMsg() {
        if(TextUtils.equals(type, msgType.NOTIFICATION.name())) {
            return DemoDbHelper.getInstance(DemoApplication.getInstance()).getInviteMessageDao().lastInviteMessage();
        }
        return null;
    }

    public int getUnReadCount() {
        if(TextUtils.equals(type, msgType.NOTIFICATION.name())) {
            return DemoDbHelper.getInstance(DemoApplication.getInstance()).getInviteMessageDao().queryUnreadCount();
        }
        return 0;
    }

    public enum msgType {

        /**
         * 通知
         */
        NOTIFICATION
    }
}
