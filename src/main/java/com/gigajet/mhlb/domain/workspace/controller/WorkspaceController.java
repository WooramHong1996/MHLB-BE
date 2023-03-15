package com.gigajet.mhlb.domain.workspace.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceDto;
import com.gigajet.mhlb.domain.workspace.service.WorkspaceService;
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
@RequestMapping("/api/workspace")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @GetMapping
    public List workspaceAllList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return workspaceService.workspaceAllList(userDetails.getUser());
    }

    @PostMapping
    public WorkspaceDto.CreateResponse workspaceCreate(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @RequestPart("image") MultipartFile image,
                                                       @RequestPart("data") WorkspaceDto.Create workspaceDto) throws IOException {
        return workspaceService.workspaceCreate(userDetails.getUser(), image, workspaceDto);
    }

    @GetMapping("/{id}/inbox")
    public List inboxGet(@AuthenticationPrincipal UserDetailsImpl userDetails,
                         @PathVariable Long id,
                         @RequestParam(defaultValue = "3") Integer size) {
        return workspaceService.inboxGet(userDetails.getUser(), id, size);
    }

    @GetMapping("/")
    public WorkspaceDto.InfoAndRoll infoAndRoll(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @RequestParam Long id) {
        return workspaceService.infoAndRoll(userDetails.getUser(), id);
    }
}
