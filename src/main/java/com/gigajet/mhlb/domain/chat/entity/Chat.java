package com.gigajet.mhlb.domain.chat.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@Document(collection = "chat")
@NoArgsConstructor
public class Chat {
    @Id
    private String id;
    private Long messageId;
    private Long senderId;
    private String message;
    private Long workspaceId;
    private String inBoxId;
    private LocalDateTime createdAt;

    @Builder
    public Chat(Long workspaceId, String message, Long senderId, String inBoxId, Long messageId) {
        this.workspaceId = workspaceId;
        this.message = message;
        this.senderId = senderId;
        this.inBoxId = inBoxId;
        this.messageId = messageId;
        this.createdAt = LocalDateTime.now(ZoneOffset.of("+09:00"));
    }
}