package com.gigajet.mhlb.domain.status.controller;

import com.gigajet.mhlb.domain.status.dto.StatusRequestDto;
import com.gigajet.mhlb.domain.status.dto.StatusResponseDto;
import com.gigajet.mhlb.domain.status.service.StatusService;
import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Status", description = "상태 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/status")
public class StatusController {

    private final StatusService statusService;

    @MessageMapping("/status")
    public void changeStatus(StatusRequestDto statusRequestDto, StompHeaderAccessor accessor) {
        statusService.SocketStatusUpdate(statusRequestDto.getStatus(), accessor);
    }

    @Operation(summary = "나의 상태 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상태 조회 성공")
    })
    @GetMapping
    public StatusResponseDto.StatusInfo getMyStatus(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return statusService.myStatus(userDetails.getUser());
    }

    @Operation(summary = "워크스페이스에 속한 사용자 전체 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 전체 조회 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, E-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @GetMapping("/{workspaceId}")
    public List<StatusResponseDto.StatusInfo> getWorkspacePeople(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId) {
        return statusService.getWorkspacePeople(userDetails.getUser(), workspaceId);
    }
}
