package com.gigajet.mhlb.domain.mypage.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.mypage.dto.MypageRequestDto;
import com.gigajet.mhlb.domain.mypage.dto.MypageResponseDto;
import com.gigajet.mhlb.domain.mypage.service.MypageService;
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
@RequestMapping("/api/mypage")
public class MypageController {
    private final MypageService mypageService;

    @GetMapping
    public MypageResponseDto.Info userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.userInfo(userDetails.getUser());
    }

    @GetMapping("/workspace")
    public List<MypageResponseDto.AllList> workspaceInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.workspaceInfo(userDetails.getUser());
    }

    @PostMapping("/image")
    public MypageResponseDto.Image updateImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @RequestPart MultipartFile userImage) throws IOException {
        return mypageService.updateImage(userDetails.getUser(), userImage);
    }

    @PatchMapping("/name")
    public MypageResponseDto.Name updateName(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody MypageRequestDto.Name nameRequest) {
        return mypageService.updateName(userDetails.getUser(), nameRequest);
    }

    @PatchMapping("/status-message")
    public MypageResponseDto.Description updateDesc(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @RequestBody MypageRequestDto.Description descRequest) {
        return mypageService.updateDesc(userDetails.getUser(), descRequest);
    }

    @PatchMapping("/job")
    public MypageResponseDto.Job updateJob(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestBody MypageRequestDto.Job jobRequest) {
        return mypageService.updateJob(userDetails.getUser(), jobRequest);
    }

    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<SendMessageDto> deleteWorkspace(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @PathVariable Long workspaceId) {
        return mypageService.deleteWorkspace(userDetails.getUser(), workspaceId);
    }

    @PostMapping("/{workspaceId}/invite")
    public ResponseEntity<SendMessageDto> inviteWorkspace(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @PathVariable Long workspaceId) {
        return mypageService.inviteWorkspace(userDetails.getUser(), workspaceId);
    }

    @DeleteMapping("/{workspaceId}/reject")
    public ResponseEntity<SendMessageDto> rejectWorkspace(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @PathVariable Long workspaceId) {
        return mypageService.rejectWorkspace(userDetails.getUser(), workspaceId);
    }
}
