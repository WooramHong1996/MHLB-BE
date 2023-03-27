package com.gigajet.mhlb.domain.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long id;
    private Long senderId;
    private String message;
}
