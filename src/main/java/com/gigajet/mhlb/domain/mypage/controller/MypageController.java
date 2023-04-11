package com.gigajet.mhlb.domain.mypage.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
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
import java.util.List;

@Tag(name = "My page", description = "마이페이지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MypageController {
    private final MypageService mypageService;

    @Operation(summary = "회원정보", description = "마이페이지 회원정보 조회 API")
    @GetMapping
    public MypageResponseDto.Info userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.userInfo(userDetails.getUser());
    }

    @Operation(summary = "회원의 워크스페이스", description = "회원이 속한 워크스페이스 정보 조회 API")
    @GetMapping("/workspaces")
    public MypageResponseDto.AllList workspaceInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.workspaceInfo(userDetails.getUser());
    }

    @Operation(summary = "프로필 이미지 변경", description = "회원 프로필 이미지 변경 API")
    @PostMapping("/image")
    public MypageResponseDto.Image updateImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @RequestPart MultipartFile userImage) throws IOException {
        return mypageService.updateImage(userDetails.getUser(), userImage);
    }

    @Operation(summary = "이름 변경", description = "회원 이름 변경 API")
    @PatchMapping("/name")
    public MypageResponseDto.Name updateName(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody MypageRequestDto.Name nameRequest) {
        return mypageService.updateName(userDetails.getUser(), nameRequest);
    }

    @Operation(summary = "상태 메세지 변경", description = "회원 상태 메세지 변경 API")
    @PatchMapping("/status-message")
    public MypageResponseDto.Description updateDesc(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @RequestBody MypageRequestDto.Description descRequest) {
        return mypageService.updateDesc(userDetails.getUser(), descRequest);
    }

    @Operation(summary = "직업 변경", description = "회원 직업 변경 API")
    @PatchMapping("/job")
    public MypageResponseDto.Job updateJob(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestBody MypageRequestDto.Job jobRequest) {
        return mypageService.updateJob(userDetails.getUser(), jobRequest);
    }

    @Operation(summary = "워크스페이스 탈퇴", description = "마이페이지 워크스페이스 탈퇴 API")
    @PatchMapping("/{workspaceId}")
    public ResponseEntity<SendMessageDto> deleteWorkspace(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @PathVariable Long workspaceId) {
        return mypageService.deleteWorkspace(userDetails.getUser(), workspaceId);
    }

    @Operation(summary = "워크스페이스 초대 수락", description = "마이페이지 워크스페이스 초대 수락 API")
    @PostMapping("/{workspaceId}/invite")
    public ResponseEntity<SendMessageDto> inviteWorkspace(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @PathVariable Long workspaceId) {
        return mypageService.inviteWorkspace(userDetails.getUser(), workspaceId);
    }

    @Operation(summary = "워크스페이스 초대 거절", description = "마이페이지 워크스페이스 초대 거절 API")
    @DeleteMapping("/{workspaceId}/invite")
    public ResponseEntity<SendMessageDto> rejectWorkspace(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @PathVariable Long workspaceId) {
        return mypageService.rejectWorkspace(userDetails.getUser(), workspaceId);
    }
}
