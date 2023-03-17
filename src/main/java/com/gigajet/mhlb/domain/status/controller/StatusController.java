package com.gigajet.mhlb.domain.status.controller;

import com.gigajet.mhlb.domain.status.dto.StatusRequestDto;
import com.gigajet.mhlb.domain.status.dto.StatusResponseDto;
import com.gigajet.mhlb.domain.status.service.StatusService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/status")
public class StatusController {
    private final StatusService statusService;

    @PostMapping
    public String stausUpdate(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody StatusRequestDto statusRequestDto) {
        return statusService.statusUpdate(userDetails.getUser(), statusRequestDto);
    }

    @GetMapping
    public StatusResponseDto getMyStatus(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return statusService.myStatus(userDetails.getUser());
    }

    @GetMapping("/{id}")//id워크스페이스에 속한 유저들의 상태를 전부 가져옴
    public List getWorkspacePeople(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return statusService.getWorkspacePeople(userDetails.getUser(), id);
    }
}
