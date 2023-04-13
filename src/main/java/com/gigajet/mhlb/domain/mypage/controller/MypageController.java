package com.gigajet.mhlb.domain.mypage.controller;

import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.mypage.dto.MypageRequestDto;
import com.gigajet.mhlb.domain.mypage.dto.MypageResponseDto;
import com.gigajet.mhlb.domain.mypage.service.MypageService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "My page", description = "마이페이지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MypageController {
    
    private final MypageService mypageService;

    @Operation(summary = "회원정보", description = "마이페이지 회원정보 조회 API")
    @GetMapping
    public MypageResponseDto.Info getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.findUserInfo(userDetails.getUser());
    }

    @Operation(summary = "회원의 워크스페이스", description = "회원이 속한 워크스페이스 정보 조회 API")
    @GetMapping("/workspaces")
    public MypageResponseDto.AllList getWorkspaceInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.findWorkspaceInfo(userDetails.getUser());
    }

    @Operation(summary = "프로필 이미지 변경", description = "회원 프로필 이미지 변경 API")
    @PostMapping("/image")
    public MypageResponseDto.Image postImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestPart MultipartFile userImage) throws IOException {
        return mypageService.changeImage(userDetails.getUser(), userImage);
    }

    @Operation(summary = "이름 변경", description = "회원 이름 변경 API")
    @PatchMapping("/name")
    public MypageResponseDto.Name patchName(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody MypageRequestDto.Name nameRequest) {
        return mypageService.changeName(userDetails.getUser(), nameRequest.getUserName());
    }

    @Operation(summary = "상태 메세지 변경", description = "회원 상태 메세지 변경 API")
    @PatchMapping("/status-message")
    public MypageResponseDto.Description patchDesc(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @RequestBody MypageRequestDto.Description descRequest) {
        return mypageService.changeDesc(userDetails.getUser(), descRequest.getUserDesc());
    }

    @Operation(summary = "직업 변경", description = "회원 직업 변경 API")
    @PatchMapping("/job")
    public MypageResponseDto.Job patchJob(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestBody MypageRequestDto.Job jobRequest) {
        return mypageService.changeJob(userDetails.getUser(), jobRequest.getUserJob());
    }

    @Operation(summary = "워크스페이스 탈퇴", description = "마이페이지 워크스페이스 탈퇴 API")
    @PatchMapping("/{workspaceId}")
    public ResponseEntity<SendMessageDto> patchWorkspaceUserIsShow(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @PathVariable Long workspaceId) {
        return mypageService.leaveWorkspace(userDetails.getUser(), workspaceId);
    }

    @Operation(summary = "워크스페이스 초대 수락", description = "마이페이지 워크스페이스 초대 수락 API")
    @PostMapping("/{workspaceId}/invite")
    public ResponseEntity<SendMessageDto> postWorkspaceInvite(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @PathVariable Long workspaceId) {
        return mypageService.acceptWorkspaceInvite(userDetails.getUser(), workspaceId);
    }

    @Operation(summary = "워크스페이스 초대 거절", description = "마이페이지 워크스페이스 초대 거절 API")
    @DeleteMapping("/{workspaceId}/invite")
    public ResponseEntity<SendMessageDto> deleteWorkspaceInvite(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                @PathVariable Long workspaceId) {
        return mypageService.rejectWorkspaceInvite(userDetails.getUser(), workspaceId);
    }
}
