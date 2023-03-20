package com.gigajet.mhlb.domain.status.repository;

import com.gigajet.mhlb.domain.status.entity.RedisStatus;
import org.springframework.data.repository.CrudRepository;

public interface RedisStatusRepository extends CrudRepository<RedisStatus, String> {
    RedisStatus findByEmail(String email);
}
