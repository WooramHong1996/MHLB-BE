package com.gigajet.mhlb.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ChatRequestDto {
    private String uuid;
    private Long workspaceId;
    private String message;
}
