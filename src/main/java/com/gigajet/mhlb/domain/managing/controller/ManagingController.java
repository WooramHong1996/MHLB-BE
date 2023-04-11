package com.gigajet.mhlb.domain.managing.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.mail.service.MailService;
import com.gigajet.mhlb.domain.managing.dto.ManagingRequestDto;
import com.gigajet.mhlb.domain.managing.dto.ManagingResponseDto;
import com.gigajet.mhlb.domain.managing.service.ManagingService;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceRequestDto;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceResponseDto;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceInvite;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Managing", description = "워크스페이스 관리자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/managing")
public class ManagingController {

    private final ManagingService managingService;
    private final MailService mailService;

    @Operation(summary = "워크스페이스 정보", description = "관리자 페이지 워크스페이스 정보 조회 API")
    @GetMapping
    public ManagingResponseDto.Management management(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam Long id) {
        return managingService.management(userDetails.getUser(), id);
    }

    @Operation(summary = "이미지 변경", description = "관리자 페이지 워크스페이스 이미지 변경 API")
    @PostMapping("/{id}/image")
    public ManagingResponseDto.Image imagePost(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestPart MultipartFile workspaceImage) throws IOException {
        return managingService.imagePost(userDetails.getUser(), id, workspaceImage);
    }

    @Operation(summary = "타이틀 변경", description = "관리자 페이지 워크스페이스 타이틀 변경 API")
    @PatchMapping("/{id}/title")
    public ManagingResponseDto.Title titlePatch(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody ManagingRequestDto.Title workspaceTitle) {
        return managingService.titlePatch(userDetails.getUser(), id, workspaceTitle.getWorkspaceTitle());
    }

    @Operation(summary = "상세정보 변경", description = "관리자 페이지 워크스페이스 상세정보 변경 API")
    @PatchMapping("/{id}/description")
    public ManagingResponseDto.Description descPatch(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody ManagingRequestDto.Desc workspaceDesc) {
        return managingService.descPatch(userDetails.getUser(), id, workspaceDesc.getWorkspaceDesc());
    }

    @Operation(summary = "회원 리스트 조회", description = "관리자 페이지 워크스페이스에 속한 회원 모두 조회 API")
    @GetMapping("/{id}/people")
    public List<ManagingResponseDto.People> getPeople(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return managingService.getPeople(userDetails.getUser(), id);
    }

    @Operation(summary = "회원 추방", description = "관리자 페이지 워크스페이스에 속한 회원 추방 API")
    @DeleteMapping("/{id}/people/{userid}")
    public ResponseEntity<SendMessageDto> deletePeople(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @PathVariable Long userid) {
        return managingService.deletePeople(userDetails.getUser(), id, userid);
    }

    @Operation(summary = "회원 권한 수정", description = "관리자 페이지 워크스페이스에 속한 회원 권한 수정 API")
    @PatchMapping("/{id}/people/{userid}")
    public ManagingResponseDto.Role changeRole(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @PathVariable Long userid, @RequestBody ManagingRequestDto.Role roleRequestDto) {
        return managingService.changeRole(userDetails.getUser(), id, userid, roleRequestDto);
    }

    @Operation(summary = "워크스페이스 삭제", description = "관리자 페이지 워크스페이스 삭제(관리자만 가능) API")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<SendMessageDto> deleteWorkspace(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return managingService.deleteWorkspace(userDetails.getUser(), id);
    }


    @Operation(summary = "워크스페이스 회원 초대", description = "관리 페이지 워크스페이스 회원 초대 API")
    @PostMapping("/{id}/invite")
    public ResponseEntity<SendMessageDto> invite(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody WorkspaceRequestDto.Invite email) {
        WorkspaceInvite workspaceInvite = managingService.invite(userDetails.getUser(), id, email.getEmail());
        mailService.inviteMail(workspaceInvite);

        return SendMessageDto.toResponseEntity(SuccessCode.INVITE_SUCCESS);
    }

    @Operation(summary = "워크스페이스 초대한 회원", description = "관리 페이지 워크스페이스 초대한 회원 조회 API")
    @GetMapping("/{id}/invite")
    public List<WorkspaceResponseDto.Invite> getInvite(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return managingService.getInvite(userDetails.getUser(), id);
    }

    @Operation(summary = "워크스페이스 회원 초대 취소", description = "관리 페이지 워크스페이스 회원 초대 취소 API")
    @DeleteMapping("/{id}/invite/{inviteid}")
    public ResponseEntity<SendMessageDto> deleteInvite(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @PathVariable Long inviteid) {
        return managingService.deleteInvite(userDetails.getUser(), id, inviteid);
    }
}
