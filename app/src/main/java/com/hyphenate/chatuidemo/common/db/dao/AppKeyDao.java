package com.hyphenate.chatuidemo.common.db.dao;

import com.hyphenate.chatuidemo.common.db.entity.AppKeyEntity;
import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AppKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(AppKeyEntity... keys);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<AppKeyEntity> keys);

    @Query("select * from app_key  order by timestamp asc")
    List<AppKeyEntity> loadAllAppKeys();

    @Query("delete from app_key where appKey = :arg0")
    void deleteAppKey(String arg0);

    @Query("select * from app_key where appKey = :arg0")
    List<AppKeyEntity> queryKey(String arg0);
}
