package com.gigajet.mhlb.domain.chat.dto;

import com.gigajet.mhlb.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatResponse {
    private Long id;
    private Long senderId;
    private String message;
}
