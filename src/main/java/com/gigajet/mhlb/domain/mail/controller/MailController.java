package com.gigajet.mhlb.domain.mail.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.mail.dto.MailResponseDto;
import com.gigajet.mhlb.domain.mail.service.MailService;
import com.gigajet.mhlb.domain.user.dto.UserRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Mail", description = "메일 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    // 비밀번호 찾기 이메일 발송
    @Operation(summary = "이메일 발송 1", description = "비밀번호 찾기 이메일 발송 API")
    @PostMapping("/check/email")
    public ResponseEntity<SendMessageDto> sendFindPasswordMail(@RequestBody UserRequestDto.CheckEmail emailDto) {
        return mailService.sendFindPasswordMail(emailDto.getEmail());
    }

    // 비밀번호 찾기 인증 코드 유효 검사
    @Operation(summary = "인증 코드 유효 검사 1", description = "비밀번호 찾기 인증 코드 유효 검사 API")
    @PostMapping("/check/code/{uuid}")
    public ResponseEntity<SendMessageDto> checkFindPasswordCode(@PathVariable String uuid) {
        return mailService.checkFindPasswordCode(uuid);
    }

    // 비밀번호 변경
    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경 API")
    @PostMapping("/reset/password/{uuid}")
    public ResponseEntity<SendMessageDto> resetPassword(@PathVariable String uuid, @RequestBody UserRequestDto.Password passwordDto) {
        return mailService.resetPassword(uuid, passwordDto);
    }

    // 워크스페이스 초대 인증 코드 유효 검사 및 회원가입 유무 검사
    @Operation(summary = "워크스페이스 초대 인증 코드", description = "워크스페이스 초대 인증 코드 유효 검사 및 회원가입 유무 검사 API")
    @PostMapping("/check/invite/code/{uuid}")
    public ResponseEntity<MailResponseDto.CheckInviteCode> checkInviteWorkspaceCode(@PathVariable String uuid) {
        return mailService.checkInviteWorkspaceCode(uuid);
    }

}
