package com.hyphenate.chatuidemo.common.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hyphenate.chatuidemo.common.db.entity.InviteMessageEntity;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

@Dao
public interface InviteMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(InviteMessageEntity... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<InviteMessageEntity> users);

    @Query("select * from em_users")
    LiveData<List<InviteMessageEntity>> loadAllInviteMessages();

    @Query("delete from em_invite_message where groupId = :groupId")
    void deleteInviteMessage(String groupId);
}
