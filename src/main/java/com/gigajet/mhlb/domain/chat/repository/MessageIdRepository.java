package com.gigajet.mhlb.domain.chat.repository;

import com.gigajet.mhlb.domain.chat.entity.MessageId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageIdRepository extends MongoRepository<MessageId, String>{
    MessageId findTopByKey(Integer key);
}