package com.gigajet.mhlb.domain.managing.controller;

import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.global.common.util.SuccessCode;
import com.gigajet.mhlb.domain.mail.service.MailService;
import com.gigajet.mhlb.domain.managing.dto.ManagingRequestDto;
import com.gigajet.mhlb.domain.managing.dto.ManagingResponseDto;
import com.gigajet.mhlb.domain.managing.service.ManagingService;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceRequestDto;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceResponseDto;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceInvite;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Managing", description = "워크스페이스 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/managing")
public class ManagingController {

    private final ManagingService managingService;
    private final MailService mailService;

    @Operation(summary = "관리 페이지 워크스페이스 정보 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 변경 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, E-11, SC-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @GetMapping
    public ManagingResponseDto.Management getWorkspaceInfo(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam(name = "id") Long workspaceId) {
        return managingService.findWorkspaceInfo(userDetails.getUser(), workspaceId);
    }

    @Operation(summary = "관리 페이지 워크스페이스 이미지 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 변경 성공"),
            @ApiResponse(responseCode = "400", description = "E-11, E-01, E-02, E-03, W-01, SC-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class))),
            @ApiResponse(responseCode = "500", description = "E-04", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/{workspaceId}/image")
    public ManagingResponseDto.Image postImage(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId, @RequestPart MultipartFile workspaceImage) throws IOException {
        return managingService.changeImage(userDetails.getUser(), workspaceId, workspaceImage);
    }

    @Operation(summary = "관리 페이지 워크스페이스 타이틀 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "타이틀 변경 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, E-11, SC-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PatchMapping("/{workspaceId}/title")
    public ManagingResponseDto.Title patchTitle(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId, @RequestBody ManagingRequestDto.Title workspaceTitle) {
        return managingService.changeTitle(userDetails.getUser(), workspaceId, workspaceTitle.getWorkspaceTitle());
    }

    @Operation(summary = "관리 페이지 워크스페이스 상세정보 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상세정보 변경 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, E-11, SC-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PatchMapping("/{workspaceId}/description")
    public ManagingResponseDto.Description patchDesc(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId, @RequestBody ManagingRequestDto.Desc workspaceDesc) {
        return managingService.changeDesc(userDetails.getUser(), workspaceId, workspaceDesc.getWorkspaceDesc());
    }

    @Operation(summary = "관리 페이지 워크스페이스에 속한 회원 전체 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 전체 조회 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, E-11, SC-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @GetMapping("/{workspaceId}/people")
    public List<ManagingResponseDto.People> getPeople(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId) {
        return managingService.findPeople(userDetails.getUser(), workspaceId);
    }

    @Operation(summary = "관리 페이지 워크스페이스에 속한 회원 추방 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 추방 성공"),
            @ApiResponse(responseCode = "400", description = "U-02, W-01, W-02, E-11, SC-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PatchMapping("/{workspaceId}/people/{userId}")
    public ResponseEntity<SendMessageDto> patchPeople(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId, @PathVariable Long userId) {
        return managingService.exilePeople(userDetails.getUser(), workspaceId, userId);
    }

    @Operation(summary = "관리 페이지 워크스페이스에 속한 회원 권한 수정 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 권한 변경 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, W-02, M-02, E-11, SC-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PatchMapping("/{workspaceId}/people/{userId}/role")
    public ManagingResponseDto.Role patchRole(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId, @PathVariable Long userId, @RequestBody ManagingRequestDto.Role roleRequestDto) {
        return managingService.changeRole(userDetails.getUser(), workspaceId, userId, roleRequestDto);
    }

    @Operation(summary = "관리 페이지 워크스페이스 삭제(ADMIN만 가능) API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, W-02, E-11, SC-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PatchMapping("/{workspaceId}")
    public ResponseEntity<SendMessageDto> patchWorkspaceIsShow(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId) {
        return managingService.offWorkspaceIsShow(userDetails.getUser(), workspaceId);
    }


    @Operation(summary = "관리 페이지 워크스페이스 회원 초대 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 회원 초대 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, M-01, E-11, SC-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/{workspaceId}/invite")
    public ResponseEntity<SendMessageDto> invite(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId, @RequestBody WorkspaceRequestDto.Invite email) {
        WorkspaceInvite workspaceInvite = managingService.invite(userDetails.getUser(), workspaceId, email.getEmail());
        mailService.sendInviteMail(workspaceInvite);

        return SendMessageDto.toResponseEntity(SuccessCode.INVITE_SUCCESS);
    }

    @Operation(summary = "관리 페이지 워크스페이스 초대한 회원 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 초대한 회원 조회 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, E-11, SC-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @GetMapping("/{workspaceId}/invite")
    public List<WorkspaceResponseDto.Invite> getInvite(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId) {
        return managingService.getInvite(userDetails.getUser(), workspaceId);
    }

    @Operation(summary = "관리 페이지 워크스페이스 회원 초대 취소 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 회원 초대 취소 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, W-02, E-11, SC-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @DeleteMapping("/{workspaceId}/invite/{inviteId}")
    public ResponseEntity<SendMessageDto> deleteInvite(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long workspaceId, @PathVariable Long inviteId) {
        return managingService.cancelInvite(userDetails.getUser(), workspaceId, inviteId);
    }
}
