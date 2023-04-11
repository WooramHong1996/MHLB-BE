package com.gigajet.mhlb.domain.alarm.Repository;

import com.gigajet.mhlb.domain.alarm.Entity.Alarm;
import com.gigajet.mhlb.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Optional<Alarm> findTopByUserAndWorkspaceIdAndUnreadMessage(User user, Long workspaceId,Boolean unreadMessage);
    List<Alarm> findAllByUserIdAndWorkspaceIdAndUnreadMessage(Long userId, Long workspaceId,Boolean unreadMessage);
    List<Alarm> findAllByUserIdAndWorkspaceIdAndUuidAndUnreadMessage(Long userId, Long workspaceId, String uuid,Boolean unreadMessage);

}
