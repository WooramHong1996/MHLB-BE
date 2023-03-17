package com.gigajet.mhlb.domain.user.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.status.service.StatusService;
import com.gigajet.mhlb.domain.user.dto.UserRequestDto;
import com.gigajet.mhlb.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final StatusService statusService;

    @PostMapping("/duplicate-email")
    public ResponseEntity<SendMessageDto> duplicateEmail(@RequestBody UserRequestDto.CheckEmailDto emailDto) {
        return userService.duplicateEmail(emailDto.getEmail());
    }

    @PostMapping("/register")
    public ResponseEntity<SendMessageDto> register(@RequestBody UserRequestDto.Register registerDto) {
        userService.register(registerDto);
        return statusService.register(registerDto);
    }

    @PostMapping("/login")
    public ResponseEntity<SendMessageDto> login(@RequestBody UserRequestDto.Login loginDto, HttpServletResponse response) {
        return userService.login(loginDto, response);
    }

}
