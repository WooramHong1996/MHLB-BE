package com.gigajet.mhlb.domain.user.service;

import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.global.common.util.SuccessCode;
import com.gigajet.mhlb.domain.user.dto.GoogleOAuthRequestDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.user.social.GoogleOAuth;
import com.gigajet.mhlb.domain.user.social.SocialType;
import com.gigajet.mhlb.global.exception.CustomException;
import com.gigajet.mhlb.global.exception.ErrorCode;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;

    private final GoogleOAuth googleOAuth;
    private final JwtUtil jwtUtil;

    private final HttpServletResponse response;

    public ResponseEntity<SendMessageDto> oAuthLogin(String code) {
        GoogleOAuthRequestDto.Token tokenRequestDto = googleOAuth.getAccessToken(code);
        GoogleOAuthRequestDto.GoogleUser googleUserDto = googleOAuth.getUserInfo(tokenRequestDto);

        createOAuthUser(googleUserDto);

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(googleUserDto.getEmail()));

        return SendMessageDto.toResponseEntity(SuccessCode.LOGIN_SUCCESS);
    }

    @Transactional
    void createOAuthUser(GoogleOAuthRequestDto.GoogleUser googleUserDto) {
        Optional<User> userOptional = userRepository.findByEmail(googleUserDto.getEmail());

        if (userOptional.isEmpty()) {
            userRepository.save(new User(googleUserDto));
        } else if (userOptional.get().getType() != SocialType.GOOGLE) {
            throw new CustomException(ErrorCode.NOT_SOCIAL_EMAIL);
        }
    }

}
