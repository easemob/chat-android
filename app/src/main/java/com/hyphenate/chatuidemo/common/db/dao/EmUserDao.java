package com.hyphenate.chatuidemo.common.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;

import java.util.List;

@Dao
public interface EmUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(EmUserEntity... users);

    @Query("select * from em_users where username = :username")
    LiveData<EmUserEntity> loadUserById(String username);
}
