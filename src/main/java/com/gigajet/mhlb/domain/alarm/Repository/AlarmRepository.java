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

    Optional<Alarm> findAllByUserIdAndWorkspaceIdAndUuid(Long id, Long workspaceId, String uuid);

    List<Alarm> findAllByUserIdAndWorkspaceIdAndUnreadMessage(Long userId, Long workspaceId, Boolean unreadMessage);

    Optional<Alarm> findByUserIdAndWorkspaceIdAndUuidAndUnreadMessage(Long userId, Long workspaceId, String uuid, Boolean unreadMessage);

    @Modifying
    @Transactional
    @Query("UPDATE Alarm SET unreadMessage = ?1 WHERE id = ?2 ")
    void update(Boolean unreadMessage, Long id);

}
