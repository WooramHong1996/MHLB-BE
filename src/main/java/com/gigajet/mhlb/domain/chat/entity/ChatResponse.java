package com.gigajet.mhlb.domain.chat.entity;

import com.gigajet.mhlb.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatResponse {
    private Long id;
    private Long senderId;
    private String message;

    public ChatResponse(ChatMessage message, User user) {
        this.id = message.getId();
        this.message = message.getMessage();
        this.senderId = user.getId();
    }

    public ChatResponse(ChatMessage message) {
        this.id = message.getId();
        this.message = message.getMessage();
        this.senderId = 0l;
    }
}
