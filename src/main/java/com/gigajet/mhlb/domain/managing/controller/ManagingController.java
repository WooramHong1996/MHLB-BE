package com.gigajet.mhlb.domain.managing.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.managing.dto.ManagingRequestDto;
import com.gigajet.mhlb.domain.managing.dto.ManagingResponseDto;
import com.gigajet.mhlb.domain.managing.service.ManagingService;
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
    public ManagingResponseDto.Management management(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam Long id) {
        return managingService.management(userDetails.getUser(), id);
    }

    @PostMapping("/{id}/image")
    public ManagingResponseDto.Image imagePost(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestPart MultipartFile workspaceImage) throws IOException {
        return managingService.imagePost(userDetails.getUser(), id, workspaceImage);
    }

    @PatchMapping("/{id}/title")
    public ManagingResponseDto.Title titlePatch(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody ManagingRequestDto.Title workspaceTitle) {
        return managingService.titlePatch(userDetails.getUser(), id, workspaceTitle.getWorkspaceTitle());
    }

    @PatchMapping("/{id}/description")
    public ManagingResponseDto.Description descPatch(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody ManagingRequestDto.Desc workspaceDesc) {
        return managingService.descPatch(userDetails.getUser(), id, workspaceDesc.getWorkspaceDesc());
    }

    @GetMapping("/{id}/people")
    public List<ManagingResponseDto.People> getPeople(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return managingService.getPeople(userDetails.getUser(), id);
    }

    @DeleteMapping("/{id}/people/{userid}")
    public ResponseEntity<SendMessageDto> deletePeople(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @PathVariable Long userid) {
        return managingService.deletePeople(userDetails.getUser(), id, userid);
    }

    @PatchMapping("/{id}/people/{userid}")
    public ManagingResponseDto.Role changeRole(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @PathVariable Long userid, @RequestBody ManagingRequestDto.Role roleRequestDto) {
        return managingService.changeRole(userDetails.getUser(), id, userid, roleRequestDto);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<SendMessageDto> deleteWorkspace(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return managingService.deleteWorkspace(userDetails.getUser(), id);
    }
}
