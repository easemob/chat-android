package com.hyphenate.chatuidemo.common.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.hyphenate.chatuidemo.common.db.converter.DateConverter;
import com.hyphenate.chatuidemo.common.db.dao.EmUserDao;
import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;

@Database(entities = {EmUserEntity.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract EmUserDao userDao();

}
