package com.gigajet.mhlb.domain.workspace.controller;

import com.gigajet.mhlb.domain.workspace.dto.WorkspaceRequestDto;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceResponseDto;
import com.gigajet.mhlb.domain.workspace.service.WorkspaceService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
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
    public WorkspaceResponseDto.CreateResponse workspaceCreate(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                               @RequestPart("image") MultipartFile image,
                                                               @RequestPart("data") WorkspaceRequestDto.Create workspaceDto) throws IOException {
        return workspaceService.workspaceCreate(userDetails.getUser(), image, workspaceDto);
    }

    @GetMapping("/{id}/inbox")
    public List inboxGet(@AuthenticationPrincipal UserDetailsImpl userDetails,
                         @PathVariable Long id,
                         @RequestParam(defaultValue = "3") Integer size) {
        return workspaceService.inboxGet(userDetails.getUser(), id, size);
    }

    @GetMapping("/")
    public WorkspaceResponseDto.InfoAndRoll infoAndRoll(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @RequestParam Long id) {
        return workspaceService.infoAndRoll(userDetails.getUser(), id);
    }

    @PostMapping("/{id}/invite")//테스트용 코드
    public String testInvite(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable Long id){
        return workspaceService.testInvite(userDetails.getUser(),id);
    }
}
