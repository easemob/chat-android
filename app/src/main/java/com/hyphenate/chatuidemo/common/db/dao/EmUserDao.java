package com.hyphenate.chatuidemo.common.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

@Dao
public interface EmUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(EmUserEntity... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<EmUserEntity> users);

    @Query("select * from em_users where username = :username")
    LiveData<List<EaseUser>> loadUserById(String username);

    @Query("select * from em_users")
    LiveData<List<EaseUser>> loadUsers();

    @Query("select username from em_users")
    List<String> loadAllUsers();

    @Query("delete from em_users")
    int clearUsers();
}
