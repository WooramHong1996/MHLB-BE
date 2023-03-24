package com.gigajet.mhlb.domain.status.repository;

import com.gigajet.mhlb.domain.status.entity.SqlStatus;
import com.gigajet.mhlb.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlStatusRepository extends JpaRepository<SqlStatus, Long> {
    SqlStatus findTopByUserOrderByUpdateDayDescUpdateTimeDesc(User user);
}
