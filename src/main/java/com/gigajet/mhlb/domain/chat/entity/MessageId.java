package com.gigajet.mhlb.domain.chat.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "message_id")
@NoArgsConstructor
public class MessageId {
    @Id
    private String id;
    private Long messageId;
    private Integer key;

    public MessageId(Long messageId) {
        this.messageId = messageId;
        this.key = 1;
    }

    public void addMessageId() {
        this.messageId++;
    }
}