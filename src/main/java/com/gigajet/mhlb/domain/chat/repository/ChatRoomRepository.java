package com.gigajet.mhlb.domain.chat.repository;

import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
//    ChatRoom findByUserListAndWorkspaceId(List<Long> userIdList, Long workspaceId);
//    Page<ChatRoom> findByWorkspaceIdAndUserListIn(Pageable pageable, Long workspaceId, List<Long> list);
}