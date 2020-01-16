package com.hyphenate.chatuidemo.common.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.hyphenate.chatuidemo.common.db.converter.DateConverter;
import com.hyphenate.chatuidemo.common.db.dao.EmUserDao;
import com.hyphenate.chatuidemo.common.db.dao.InviteMessageDao;
import com.hyphenate.chatuidemo.common.db.dao.MsgTypeManageDao;
import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;
import com.hyphenate.chatuidemo.common.db.entity.InviteMessage;
import com.hyphenate.chatuidemo.common.db.entity.MsgTypeManageEntity;

@Database(entities = {EmUserEntity.class, InviteMessage.class, MsgTypeManageEntity.class}, version = 8)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract EmUserDao userDao();

    public abstract InviteMessageDao inviteMessageDao();

    public abstract MsgTypeManageDao msgTypeManageDao();
}
