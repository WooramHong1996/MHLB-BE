package com.gigajet.mhlb.domain.chat.repository;

import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    //    ChatRoom findByUserListAndWorkspaceId(List<Long> userIdList, Long workspaceId);
    ChatRoom findByUserSetAndWorkspaceId(HashSet<Long> userSet, Long workspaceId);
    ChatRoom findByInBoxId(String inboxId);
    List<ChatRoom> findByWorkspaceIdAndUserSetInOrderByLastChatDesc(Long id, Long userId);
}