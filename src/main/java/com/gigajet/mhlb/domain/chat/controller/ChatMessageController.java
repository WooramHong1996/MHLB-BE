package com.gigajet.mhlb.domain.chat.controller;

import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.service.ChatMessageService;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    private final JwtUtil jwtUtil;

    @MessageMapping("/inbox")
    public void sendMsg(ChatRequestDto.Chat message, StompHeaderAccessor accessor) {
        chatMessageService.sendMsg(message, accessor, chatMessageService.getMessageId());
    }
}
