package com.gigajet.mhlb.domain.user.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.mail.service.MailService;
import com.gigajet.mhlb.domain.status.service.StatusService;
import com.gigajet.mhlb.domain.user.dto.UserRequestDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.service.OAuthService;
import com.gigajet.mhlb.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final StatusService statusService;
    private final OAuthService oAuthService;
    private final MailService mailService;

    // 중복 체크
    @PostMapping("/duplicate-email")
    public ResponseEntity<SendMessageDto> duplicateEmail(@Valid @RequestBody UserRequestDto.CheckEmail emailDto) {
        return userService.duplicateEmail(emailDto.getEmail());
    }

    // 유효 체크
    @PostMapping("/validate-email")
    public ResponseEntity<SendMessageDto> validateEmail(@Valid @RequestBody UserRequestDto.CheckEmail emailDto) {
        return userService.validateEmail(emailDto.getEmail());
    }

    @PostMapping("/register")
    public ResponseEntity<SendMessageDto> register(@Valid @RequestBody UserRequestDto.Register registerDto) {
        User user = userService.register(registerDto);
        return statusService.register(user);
    }

    @PostMapping("/register/{uuid}")
    public ResponseEntity<SendMessageDto> register(@PathVariable String uuid, @RequestBody UserRequestDto.Register registerDto) {
        Map<Object, Object> userInfo = mailService.getUserInfo(uuid);
        User user = userService.register(registerDto, userInfo);

        return statusService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<SendMessageDto> login(@Valid @RequestBody UserRequestDto.Login loginDto, HttpServletResponse response) {
        return userService.login(loginDto, response);
    }


    /*
        소셜 로그인
     */
    @GetMapping("/auth/google")
    public void socialLoginRedirect() throws IOException {
        oAuthService.sendRedirect();
    }

    @GetMapping("/auth/google/callback")
    public ResponseEntity<SendMessageDto> callback(@RequestParam(name = "code") String code) {
        return oAuthService.oAuthLogin(code);
    }

}
