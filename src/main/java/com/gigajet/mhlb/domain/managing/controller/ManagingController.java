package com.gigajet.mhlb.domain.managing.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.managing.service.ManagingService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/managing")
public class ManagingController {
    private final ManagingService managingService;

//    @GetMapping("/{id}")
//    public ResponseEntity<SendMessageDto> mypage(@AuthenticationPrincipal UserDetailsImpl userDetails, RequestParam id){
//
//    }
}
