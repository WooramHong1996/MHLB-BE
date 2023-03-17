package com.gigajet.mhlb.domain.status.repository;

import com.gigajet.mhlb.domain.status.entity.Status;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StatusRepository extends CrudRepository<Status, String> {
    Status findByEmail(String email);
}
