package com.gigajet.mhlb.domain.status.controller;

import com.gigajet.mhlb.domain.status.dto.StatusRequestDto;
import com.gigajet.mhlb.domain.status.dto.StatusResponseDto;
import com.gigajet.mhlb.domain.status.service.StatusService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/status")
public class StatusController {

    private final StatusService statusService;

    @MessageMapping("/status")
    public void changeStatus(StatusRequestDto statusRequestDto, StompHeaderAccessor accessor) {
        statusService.SocketStatusUpdate(statusRequestDto.getStatus(), accessor);
    }

    @GetMapping
    public StatusResponseDto.StatusInfo getMyStatus(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return statusService.myStatus(userDetails.getUser());
    }

    @GetMapping("/{id}")
    public List<StatusResponseDto.StatusInfo> getWorkspacePeople(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return statusService.getWorkspacePeople(userDetails.getUser(), id);
    }
}
