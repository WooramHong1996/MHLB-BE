package com.gigajet.mhlb.domain.chat.controller;

import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.domain.chat.service.ChatService;
import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat", description = "채팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inbox")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅방 목록 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, W-02", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @GetMapping
    public List<ChatResponseDto.Inbox> getInbox(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam Long id) {
        return chatService.getInbox(userDetails.getUser(), id);
    }

    @Operation(summary = "채팅 목록 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, W-02", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @GetMapping("/{workspaceId}/{userId}")
    public List<ChatResponseDto.Chatting> getChat(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @PathVariable Long workspaceId, @PathVariable Long userId,
                                                  @PageableDefault(size = 25, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return chatService.getChat(userDetails.getUser(), workspaceId, userId, pageable);
    }

    @Operation(summary = "채팅방 조회, 생성 API", description = "채팅방을 조회하고 없을 경우 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 조회 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, W-02", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/{workspaceId}")
    public ChatResponseDto.GetUuid findChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId, @RequestBody ChatRequestDto.UserId userId) {
        return chatService.getUuid(userDetails.getUser(), workspaceId, userId.getUserId());
    }
}
