package com.gigajet.mhlb.domain.chat.controller;

import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponse;
import com.gigajet.mhlb.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inbox")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations sendingOperations;

    @MessageMapping("/inbox")//메시지매핑은 리퀘스트매핑 못받음
    public void sendMsg(ChatRequestDto.Chat chat) {
        chatService.sendMsg(chat);

        sendingOperations.convertAndSend("/sub/" + chat.getUuid(), chat);
    }

//    @PostMapping("/api/inbox/{workspaceId}")
//    public
}
