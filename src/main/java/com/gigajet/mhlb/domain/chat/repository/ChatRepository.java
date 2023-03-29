package com.gigajet.mhlb.domain.chat.repository;

import com.gigajet.mhlb.domain.chat.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
//    @Query("{'workspaceId' : ?1, 'roomNum' : ?2}")
//    Page<Chat> findByWorkspaceIdAndRoomNum(Pageable pageable, Long workspaceId, String inBoxId);

    List<Chat> findByInBoxId(String uuid);
}
