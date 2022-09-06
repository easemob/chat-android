package com.hyphenate.chatdemo.common.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.hyphenate.chatdemo.common.db.converter.DateConverter;
import com.hyphenate.chatdemo.common.db.dao.AppKeyDao;
import com.hyphenate.chatdemo.common.db.dao.EmUserDao;
import com.hyphenate.chatdemo.common.db.dao.InviteMessageDao;
import com.hyphenate.chatdemo.common.db.dao.MsgTypeManageDao;
import com.hyphenate.chatdemo.common.db.entity.AppKeyEntity;
import com.hyphenate.chatdemo.common.db.entity.EmUserEntity;
import com.hyphenate.chatdemo.common.db.entity.InviteMessage;
import com.hyphenate.chatdemo.common.db.entity.MsgTypeManageEntity;

@Database(entities = {EmUserEntity.class,
        InviteMessage.class,
        MsgTypeManageEntity.class,
        AppKeyEntity.class},
        version = 17)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract EmUserDao userDao();

    public abstract InviteMessageDao inviteMessageDao();

    public abstract MsgTypeManageDao msgTypeManageDao();

    public abstract AppKeyDao appKeyDao();
}
