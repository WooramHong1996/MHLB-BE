package com.gigajet.mhlb.domain.alarm.Repository;

import com.gigajet.mhlb.domain.alarm.Entity.Alarm;
import com.gigajet.mhlb.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Optional<Alarm> findTopByUserAndWorkspaceIdAndUnreadMessage(User user, Long workspaceId, Boolean unreadMessage);

    List<Alarm> findAllByUserIdAndWorkspaceIdAndUnreadMessage(Long userId, Long workspaceId, Boolean unreadMessage);

    List<Alarm> findAllByUserIdAndWorkspaceIdAndUuidAndUnreadMessage(Long userId, Long workspaceId, String uuid, Boolean unreadMessage);

    List<Alarm> findByUser_IdAndWorkspaceIdAndUuidAndUnreadMessage(Long userId, Long workspaceId, String uuid, Boolean unreadMessage);


    @Modifying
    @Transactional
    @Query("UPDATE Alarm SET unreadMessage = :unreadMessage WHERE id = :id ")
    Alarm update(Boolean unreadMessage, Long id);

}
