package com.gigajet.mhlb.domain.chat.repository;

import com.gigajet.mhlb.domain.chat.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    Slice<Chat> findByInBoxId(String uuid, Pageable pageable);

    List<Chat> findByInBoxId(String uuid);

}
