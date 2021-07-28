package com.hyphenate.easeim.common.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hyphenate.easeim.common.db.entity.EmUserEntity;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

@Dao
public interface EmUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(EmUserEntity... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<EmUserEntity> users);

    @Query("select * from em_users where username = :arg0")
    LiveData<List<EaseUser>> loadUserById(String arg0);

    @Query("select * from em_users where username = :arg0")
    List<EaseUser> loadUserByUserId(String arg0);

    @Query("select * from em_users where contact = 0")
    LiveData<List<EaseUser>> loadUsers();

    @Query("select * from em_users where contact = 0")
    List<EaseUser> loadContacts();

    @Query("select * from em_users where contact = 1")
    LiveData<List<EaseUser>> loadBlackUsers();

    @Query("select * from em_users where contact = 1")
    List<EaseUser> loadBlackEaseUsers();

    @Query("select username from em_users")
    List<String> loadAllUsers();

    @Query("select username from em_users where contact = 0 or contact = 1")
    List<String> loadContactUsers();

    @Query("select * from em_users")
    List<EaseUser> loadAllEaseUsers();

    @Query("select * from em_users where contact = 0 or contact = 1")
    List<EaseUser> loadAllContactUsers();

    @Query("delete from em_users")
    int clearUsers();

    @Query("delete from em_users where contact = 1")
    int clearBlackUsers();

    @Query("delete from em_users where username = :arg0")
    int deleteUser(String arg0);

    @Query("update em_users set contact = :arg0  where username = :arg1")
    int updateContact(int arg0,String arg1);

    @Query("select username from em_users where lastModifyTimestamp + :arg0  <= :arg1")
    List<String> loadTimeOutEaseUsers(long arg0,long arg1);

    @Query("select username from em_users where lastModifyTimestamp + :arg0  <= :arg1 and contact = 1")
    List<String> loadTimeOutFriendUser(long arg0,long arg1);
}
