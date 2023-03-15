package com.gigajet.mhlb.domain.managing.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.managing.dto.ManagingRequestDto;
import com.gigajet.mhlb.domain.managing.dto.ManagingResponseDto;
import com.gigajet.mhlb.domain.managing.service.ManagingService;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUserRole;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/managing")
public class ManagingController {
    private final ManagingService managingService;

    @GetMapping
    public ManagingResponseDto.ManagementResponse management(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam Long id) {
        return managingService.management(userDetails.getUser(), id);
    }

    @PatchMapping("/{id}/image")
    public String imagePatch(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestPart MultipartFile image) throws IOException {
        return managingService.imagePatch(userDetails.getUser(), id, image);
    }

    @PatchMapping("/{id}/title")
    public String titlePatch(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody ManagingRequestDto.Title workspaceTitle) {
        return managingService.titlePatch(userDetails.getUser(), id, workspaceTitle.getWorkspaceTitle());
    }

    @PatchMapping("/{id}/description")
    public String descPatch(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody ManagingRequestDto.Desc workspaceDesc) {
        return managingService.descPatch(userDetails.getUser(), id, workspaceDesc.getWorkspaceDesc());
    }

    @GetMapping("/{id}/people")
    public List getPeople(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return managingService.getPeople(userDetails.getUser(), id);
    }

    @DeleteMapping("/{id}/people/{userid}")
    public ResponseEntity<SendMessageDto> deletePeople(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @PathVariable Long userid) {
        return managingService.deletePeople(userDetails.getUser(), id, userid);
    }

    @PostMapping("/{id}/people/{userid}")
    public WorkspaceUserRole changeRole(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @PathVariable Long userid, @RequestBody WorkspaceUserRole userRole) {
        return managingService.changeRole(userDetails.getUser(), id, userid, userRole);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<SendMessageDto> deleteWorkspace(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return managingService.deleteWorkspace(userDetails.getUser(),id);
    }
}
