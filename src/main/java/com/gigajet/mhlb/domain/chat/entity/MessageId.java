package com.gigajet.mhlb.domain.chat.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "messageId")
@NoArgsConstructor
public class MessageId {
    @Id
    String id;
    Long messageId;
    int key = 1;

    public MessageId(Long messageId) {
        this.messageId = messageId;
        this.key = 1;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public void addMessageId() {
        this.messageId++;
    }
}
