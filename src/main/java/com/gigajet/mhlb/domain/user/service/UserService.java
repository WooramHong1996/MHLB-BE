package com.gigajet.mhlb.domain.user.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.user.dto.UserRequestDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ResponseEntity<SendMessageDto> duplicateEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        return SendMessageDto.toResponseEntity(SuccessCode.CHECKUP_SUCCESS);
    }

    public ResponseEntity<SendMessageDto> register(UserRequestDto.Register registerDto) {
        Optional<User> optionalUser = userRepository.findByEmail(registerDto.getEmail());
        if (optionalUser.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        String password = passwordEncoder.encode(registerDto.getPassword());

        userRepository.save(new User(registerDto, password));

        return SendMessageDto.toResponseEntity(SuccessCode.SIGNUP_SUCCESS);
    }

    public ResponseEntity<SendMessageDto> login(UserRequestDto.Login loginDto, HttpServletResponse response) {
        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new CustomException(ErrorCode.UNREGISTER_USER));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }

        response.setHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(loginDto.getEmail()));

        return SendMessageDto.toResponseEntity(SuccessCode.LOGIN_SUCCESS);
    }
}
