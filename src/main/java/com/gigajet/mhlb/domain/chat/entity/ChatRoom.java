package com.gigajet.mhlb.domain.chat.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Data
@Document(collection = "chat_room")
@NoArgsConstructor
public class ChatRoom {
    @Id
    private String id;

    private HashSet<Long> userSet;
    private List<UserAndMessage> userAndMessages;
    private Long workspaceId;
    private String inBoxId;
    private LocalDateTime lastChat;
    private String lastMessage;

    @Builder
    public ChatRoom(HashSet<Long> userSet, Long workspaceId, String inBoxId, List<UserAndMessage> userAndMessages) {
        this.userSet = userSet;
        this.workspaceId = workspaceId;
        this.inBoxId = inBoxId;
        this.lastChat = LocalDateTime.now();
        this.userAndMessages = userAndMessages;
        this.lastMessage = "아직 대화가 없습니다";

    }

    public void update(Chat chat) {
        this.lastMessage = chat.getMessage();
        this.lastChat = chat.getCreatedAt();
    }
}
