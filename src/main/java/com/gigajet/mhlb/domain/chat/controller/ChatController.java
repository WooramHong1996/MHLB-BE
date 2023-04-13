package com.gigajet.mhlb.domain.chat.controller;

import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.domain.chat.service.ChatService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inbox")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public List<ChatResponseDto.Inbox> getInbox(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam Long id) {
        return chatService.getInbox(userDetails.getUser(), id);
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
}
