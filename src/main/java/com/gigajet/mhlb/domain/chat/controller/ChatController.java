package com.gigajet.mhlb.domain.chat.controller;

import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.domain.chat.service.ChatService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inbox")
public class ChatController {
    private final ChatService chatService;

    @EventListener(SessionConnectEvent.class)
    public void connect(SessionConnectEvent event) {
        chatService.readMessages(StompHeaderAccessor.wrap(event.getMessage()));
    }

    @EventListener(SessionSubscribeEvent.class)
    public void subscribe(SessionSubscribeEvent event) {
        chatService.checkRoom(StompHeaderAccessor.wrap(event.getMessage()));
    }

    @EventListener(SessionDisconnectEvent.class)
    public void disconnect(SessionDisconnectEvent event) {
        chatService.exitRoom(StompHeaderAccessor.wrap(event.getMessage()));
    }

    @MessageMapping("/inbox")
    public void sendMsg(ChatRequestDto.Chat message, StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader("Authorization");
        String email = chatService.resolveToken(authorization);
        chatService.sendMsg(message, email, accessor.getSessionId());
    }

    @GetMapping("/{workspaceId}/{userId}")
    public List<ChatResponseDto.Chatting> getChat(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @PathVariable Long workspaceId, @PathVariable Long userId,
                                                  @PageableDefault(size = 25, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return chatService.getChat(userDetails.getUser(), workspaceId, userId, pageable);
    }


    @PostMapping("/{workspaceId}")
    public ChatResponseDto.GetUuid findChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId, @RequestBody ChatRequestDto.UserId userId) {
        return chatService.getUuid(userDetails.getUser(), workspaceId, userId.getUserId());
    }

    @GetMapping("/{workspaceId}")
    public List<ChatResponseDto.Inbox> getInbox(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId) {
        return chatService.getInbox(userDetails.getUser(), workspaceId);
    }
}
