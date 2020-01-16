package com.hyphenate.chatuidemo.common.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hyphenate.chatuidemo.common.db.entity.InviteMessage;

import java.util.List;

@Dao
public interface InviteMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(InviteMessage... entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<InviteMessage> entities);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(InviteMessage... entities);

    @Query("select * from em_invite_message")
    LiveData<List<InviteMessage>> loadAllInviteMessages();

    @Query("select * from em_invite_message")
    List<InviteMessage> loadAll();

    @Query("select `from` from em_invite_message")
    List<String> loadAllNames();

    @Query("select * from em_invite_message order by time desc limit 1")
    InviteMessage lastInviteMessage();

    @Query("select count(isUnread) from em_invite_message where isUnread = 1")
    int queryUnreadCount();

    @Query("delete from em_invite_message where groupId = :groupId")
    void deleteByGroupId(String groupId);

    @Query("delete from em_invite_message where groupId=:groupId and `from`= :username")
    void deleteByGroupId(String groupId, String username);

    @Query("delete from em_invite_message where `from`=:from")
    void deleteByFrom(String from);

    @Query("delete from em_invite_message where :key =:from")
    void delete(String key, String from);

}
