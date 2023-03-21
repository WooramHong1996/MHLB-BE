package com.gigajet.mhlb.domain.workspace.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.mail.service.MailService;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceRequestDto;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceResponseDto;
import com.gigajet.mhlb.domain.workspace.service.WorkspaceService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final MailService mailService;

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

    @PatchMapping("/order")
    public ResponseEntity<SendMessageDto> changeOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @RequestBody WorkspaceRequestDto.Orders orders) {
        return workspaceService.changeOrder(userDetails.getUser(), orders);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<SendMessageDto> invite(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody WorkspaceRequestDto.Invite email) {
        Optional<User> invited = workspaceService.invite(userDetails.getUser(), id, email.getEmail());

        if (invited.isEmpty()) {
            mailService.inviteMail(email.getEmail());
        }

        return SendMessageDto.toResponseEntity(SuccessCode.INVITE_SUCCESS);
    }

    @GetMapping("/{id}/invite")
    public List<WorkspaceResponseDto.Invite> getInvite(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return workspaceService.getInvite(userDetails.getUser(), id);
    }

    @DeleteMapping("/{id}/invite/{inviteid}")
    public ResponseEntity<SendMessageDto> deleteInvite(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @PathVariable Long inviteid) {
        return workspaceService.deleteInvite(userDetails.getUser(), id, inviteid);
    }
}
