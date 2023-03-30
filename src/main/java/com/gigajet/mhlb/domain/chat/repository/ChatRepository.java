package com.gigajet.mhlb.domain.chat.repository;

import com.gigajet.mhlb.domain.chat.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    List<Chat> findByInBoxId(String uuid);
}
