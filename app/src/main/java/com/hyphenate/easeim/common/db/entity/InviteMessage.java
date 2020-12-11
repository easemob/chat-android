package com.hyphenate.easeim.common.db.entity;

import androidx.annotation.StringRes;
import androidx.room.Entity;
import androidx.room.Index;

import com.hyphenate.easeim.DemoApplication;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.db.DemoDbHelper;
import com.hyphenate.easeim.common.db.dao.MsgTypeManageDao;

import java.io.Serializable;

@Entity(tableName = "em_invite_message", primaryKeys = {"time"})
public class InviteMessage implements Serializable {
    private int id;
    private String from;
    private long time;
    private String reason;
    private String type = MsgTypeManageEntity.msgType.NOTIFICATION.name();

    private String status;
    private String groupId;
    private String groupName;
    private String groupInviter;
    private boolean isUnread;//是否已读

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

    public InviteMessageStatus getStatusEnum() {
        InviteMessageStatus status = null;
        try {
            status = InviteMessageStatus.valueOf(this.status);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(InviteMessageStatus status) {
        this.status = status.name();
    }

    public void setStatus(String status) {
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

    public MsgTypeManageEntity.msgType getTypeEnum() {
        MsgTypeManageEntity.msgType type = null;
        try {
            type = MsgTypeManageEntity.msgType.valueOf(this.type);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return type;
    }

    public String getType() {
        return type;
    }

    public void setType(MsgTypeManageEntity.msgType type) {
        //保存相应类型的MsgTypeManageEntity
        MsgTypeManageEntity entity = new MsgTypeManageEntity();
        entity.setType(type.name());
        MsgTypeManageDao msgTypeManageDao = DemoDbHelper.getInstance(DemoApplication.getInstance()).getMsgTypeManageDao();
        if(msgTypeManageDao != null) {
            msgTypeManageDao.insert(entity);
        }
        this.type = type.name();
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }


}
