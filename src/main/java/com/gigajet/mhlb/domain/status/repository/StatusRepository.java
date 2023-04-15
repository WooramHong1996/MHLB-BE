package com.gigajet.mhlb.domain.status.repository;

import com.gigajet.mhlb.domain.status.entity.Status;
import com.gigajet.mhlb.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long> {
    Status findTopByUserOrderByUpdateDayDescUpdateTimeDesc(User user);

    Status findTopByUserIdOrderByUpdateDayDescUpdateTimeDesc(Long userId);
}
