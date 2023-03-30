package com.gigajet.mhlb.domain.chat.controller;

import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.domain.chat.service.ChatService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inbox")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations sendingOperations;

    @MessageMapping("/inbox")//메시지매핑은 리퀘스트매핑 못받음
    public void sendMsg(ChatRequestDto.Chat chat) {
        chatService.sendMsg(chat);

        sendingOperations.convertAndSend("/sub/inbox/" + chat.getUuid(), chat);
    }

    @PostMapping("/{workspaceId}")
    public ChatResponseDto.GetUuid findChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId, @RequestBody ChatRequestDto.UserId userId) {
        return chatService.getUuid(userDetails.getUser(), workspaceId, userId.getUserId());
    }

    @GetMapping("/{workspaceId}")
    public List<ChatResponseDto.Inbox> getInbox(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId) {
        return chatService.getInbox(userDetails.getUser(), workspaceId);
    }

    @GetMapping("/{workspaceId}/{userId}")
    public List<ChatResponseDto.Chat> getChat(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId, @PathVariable Long userId) {
        return chatService.getChat(userDetails.getUser(), workspaceId, userId);
    }
}
