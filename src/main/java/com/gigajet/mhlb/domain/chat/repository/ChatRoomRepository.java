package com.gigajet.mhlb.domain.chat.repository;

import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    ChatRoom findByUserListAndWorkspaceId(List<Long> userIdList, Long workspaceId);
    ChatRoom findByInBoxId(String inboxId);
//    List<ChatRoom> findByWorkspaceIdAndUserListIn(Long workspaceId, List<Long> list);
//    List<ChatRoom> findByWorkspaceIdAndUserListIn(Long workspaceId, Long userId);
    List<ChatRoom> findByWorkspaceIdAndUserListInOrderByLastChatDesc(Long id,Long userId);
}