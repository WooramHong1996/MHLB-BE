package com.gigajet.mhlb.domain.mail.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.mail.dto.MailResponseDto;
import com.gigajet.mhlb.domain.mail.service.MailService;
import com.gigajet.mhlb.domain.user.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    // 비밀번호 찾기 이메일 발송
    @PostMapping("/check/email")
    public ResponseEntity<SendMessageDto> sendMail(@RequestBody UserRequestDto.CheckEmail emailDto) {
        return mailService.sendMail(emailDto.getEmail());
    }

    // 비밀번호 찾기 인증 코드 유효 검사
    @PostMapping("/check/code/{uuid}")
    public ResponseEntity<SendMessageDto> checkCode(@PathVariable String uuid) {
        return mailService.checkCode(uuid);
    }

    // 비밀번호 변경
    @PostMapping("/reset/password/{uuid}")
    public ResponseEntity<SendMessageDto> resetPassword(@PathVariable String uuid, @RequestBody UserRequestDto.Password passwordDto) {
        return mailService.resetPassword(uuid, passwordDto);
    }

    // 워크스페이스 초대 인증 코드 유효 검사 및 회원가입 유무 검사
    @PostMapping("/check/invite/code/{uuid}")
    public ResponseEntity<MailResponseDto.CheckInviteCode> checkInviteCode(@PathVariable String uuid) {
        return mailService.checkInviteCode(uuid);
    }

}
