package com.gigajet.mhlb.domain.chat.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Document(collection = "chat")
@NoArgsConstructor
public class Chat  {
    @Id
    private String id;
    private Long senderId;
    private String message;
    private Long workspaceId;
    private String inBoxId;
    private Integer unread;
    private LocalDateTime createdAt;
//    @Builder
//    public Chat(Long workspaceId, String message,Long senderId, String inBoxId) {
//        this.workspaceId = workspaceId;
//        this.message = message;
//        this.senderId = senderId;
//        this.inBoxId = inBoxId;
//        this.unread = 1;
//    }
}