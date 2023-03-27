package com.gigajet.mhlb.domain.chat.controller;

import com.gigajet.mhlb.domain.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessageSendingOperations sendingOperations;

    @MessageMapping("/chat")
    public void setMsg(ChatMessage message){

        sendingOperations.convertAndSend("/sub/" + message.getId(), message);
    }

}
