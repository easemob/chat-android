package com.hyphenate.chatuidemo.common.db.entity;

import androidx.room.Entity;
import androidx.room.Index;

import com.hyphenate.easeui.domain.EaseUser;

@Entity(tableName = "em_users", primaryKeys = {"username"},
        indices = {@Index(value = "username")})
public class EmUserEntity extends EaseUser {

}
