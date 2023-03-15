package com.gigajet.mhlb.domain.mypage.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.mypage.dto.ChangeMypageDto;
import com.gigajet.mhlb.domain.mypage.dto.MypageDto;
import com.gigajet.mhlb.domain.mypage.service.MypageService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MypageController {
    private final MypageService mypageService;

    @GetMapping
    public MypageDto.UserResponse userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.userInfo(userDetails.getUser());
    }

    @GetMapping("/workspace")
    public List<MypageDto.AllList> workspaceInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.workspaceInfo(userDetails.getUser());
    }

    @PatchMapping("/image")
    public ResponseEntity<SendMessageDto> UpdateImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @RequestPart("image") MultipartFile image) {
        return mypageService.UpdateImage(userDetails.getUser(), image);
    }

    @PatchMapping("/name")
    public ResponseEntity<SendMessageDto> UpdateName(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody ChangeMypageDto.NameRequest nameRequest) {
        return mypageService.UpdateName(userDetails.getUser(), nameRequest);
    }

    @PatchMapping("/desc")
    public ResponseEntity<SendMessageDto> UpdateDesc(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody ChangeMypageDto.DescRequest descRequest) {
        return mypageService.UpdateDesc(userDetails.getUser(), descRequest);
    }
    @PatchMapping("/desc")
    public ResponseEntity<SendMessageDto> UpdateJob(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody ChangeMypageDto.JobRequest jobRequest) {
        return mypageService.UpdateJob(userDetails.getUser(), jobRequest);
    }
}
