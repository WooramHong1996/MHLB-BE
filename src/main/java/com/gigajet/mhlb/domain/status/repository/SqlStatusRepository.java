package com.gigajet.mhlb.domain.status.repository;

import com.gigajet.mhlb.domain.status.entity.SqlStatus;
import com.gigajet.mhlb.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface SqlStatusRepository extends JpaRepository<SqlStatus, Long> {
    SqlStatus findTopByUserOrderByUpdatedAtDesc(User user);
}
