package com.gigajet.mhlb.domain.workspaceuser.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.workspaceuser.service.WorkspaceUserService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkspaceUserController {

    private final WorkspaceUserService workspaceUserService;

    // 임시적인 완전 잠깐 쓸 진짜진짜 가짜인 테스트용용
//    @PostMapping("/api/workspace/{workspaceId}/test/join")
//    public ResponseEntity<SendMessageDto> testJoinUser(@PathVariable Long workspaceId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return workspaceUserService.testJoinUser(workspaceId, userDetails.getUser());
//    }

}
