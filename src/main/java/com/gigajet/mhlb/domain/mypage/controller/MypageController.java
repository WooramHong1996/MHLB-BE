package com.gigajet.mhlb.domain.mypage.controller;

import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.mypage.dto.MypageRequestDto;
import com.gigajet.mhlb.domain.mypage.dto.MypageResponseDto;
import com.gigajet.mhlb.domain.mypage.service.MypageService;
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

@Tag(name = "My page", description = "마이페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MypageController {
    
    private final MypageService mypageService;

    @Operation(summary = "마이페이지 회원정보 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보 조회 성공")
    })
    @GetMapping
    public MypageResponseDto.Info getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.findUserInfo(userDetails.getUser());
    }

    @Operation(summary = "회원이 속한 워크스페이스 정보 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 전체 조회 성공")
    })
    @GetMapping("/workspaces")
    public MypageResponseDto.AllList getWorkspaceInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.findWorkspaceInfo(userDetails.getUser());
    }

    @Operation(summary = "회원 프로필 이미지 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 이미지 변경 성공"),
            @ApiResponse(responseCode = "400", description = "E-01, E-02, E-03, E-04", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/image")
    public MypageResponseDto.Image postImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestPart MultipartFile userImage) throws IOException {
        return mypageService.changeImage(userDetails.getUser(), userImage);
    }

    @Operation(summary = "회원 이름 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이름 성공")
    })
    @PatchMapping("/name")
    public MypageResponseDto.Name patchName(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody MypageRequestDto.Name nameRequest) {
        return mypageService.changeName(userDetails.getUser(), nameRequest.getUserName());
    }

    @Operation(summary = "회원 상태 메세지 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상태 메세지 성공")
    })
    @PatchMapping("/status-message")
    public MypageResponseDto.Description patchDesc(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @RequestBody MypageRequestDto.Description descRequest) {
        return mypageService.changeDesc(userDetails.getUser(), descRequest.getUserDesc());
    }

    @Operation(summary = "회원 직업 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "직업 변경 성공")
    })
    @PatchMapping("/job")
    public MypageResponseDto.Job patchJob(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestBody MypageRequestDto.Job jobRequest) {
        return mypageService.changeJob(userDetails.getUser(), jobRequest.getUserJob());
    }

    @Operation(summary = "마이페이지 워크스페이스 탈퇴 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 탈퇴"),
            @ApiResponse(responseCode = "400", description = "W-01", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PatchMapping("/{workspaceId}")
    public ResponseEntity<SendMessageDto> patchWorkspaceUserIsShow(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @PathVariable Long workspaceId) {
        return mypageService.leaveWorkspace(userDetails.getUser(), workspaceId);
    }

    @Operation(summary = "마이페이지 워크스페이스 초대 수락 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 초대 수락"),
            @ApiResponse(responseCode = "400", description = "W-01, W-02", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/{workspaceId}/invite")
    public ResponseEntity<SendMessageDto> postWorkspaceInvite(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @PathVariable Long workspaceId) {
        return mypageService.acceptWorkspaceInvite(userDetails.getUser(), workspaceId);
    }

    @Operation(summary = "마이페이지 워크스페이스 초대 거절 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 초대 거절"),
            @ApiResponse(responseCode = "400", description = "W-01", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @DeleteMapping("/{workspaceId}/invite")
    public ResponseEntity<SendMessageDto> deleteWorkspaceInvite(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                @PathVariable Long workspaceId) {
        return mypageService.rejectWorkspaceInvite(userDetails.getUser(), workspaceId);
    }
}
