package com.gigajet.mhlb.domain.chat.controller;

import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.domain.chat.service.ChatService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inbox")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations sendingOperations;

    @EventListener(SessionConnectEvent.class)
    public void connect(SessionConnectEvent event) {
        chatService.readMessages(StompHeaderAccessor.wrap(event.getMessage()));
    }

    @EventListener(SessionSubscribeEvent.class)
    public void subcribe(SessionSubscribeEvent event) {
        String endpoint = StompHeaderAccessor.wrap(event.getMessage()).getDestination();
        chatService.subcribe(endpoint);

    }

    @EventListener(SessionDisconnectEvent.class)
    public void unSubcribe(SessionUnsubscribeEvent event) {
        String endpoint = StompHeaderAccessor.wrap(event.getMessage()).getDestination();
        chatService.unSubcribe(endpoint);
    }

    @MessageMapping("/inbox")//메시지매핑은 리퀘스트매핑 못받음
    public void sendMsg(ChatRequestDto.Chat message, StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader("Authorization");
        String email = chatService.resolveTocken(authorization);
        ChatResponseDto.Chat response = chatService.sendMsg(message, email);
        sendingOperations.convertAndSend("/sub/inbox/" + message.getUuid(), response);
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
