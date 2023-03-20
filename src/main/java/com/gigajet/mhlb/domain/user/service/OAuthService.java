package com.gigajet.mhlb.domain.user.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.user.dto.GoogleOAuthTokenRequestDto;
import com.gigajet.mhlb.domain.user.dto.GoogleUserDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.user.social.GoogleOAuth;
import com.gigajet.mhlb.domain.user.social.SocialType;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final GoogleOAuth googleOAuth;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private final HttpServletResponse response;

    public void sendRedirect() throws IOException {
        String redirectURL = googleOAuth.getOAuthRedirectURL();
        response.sendRedirect(redirectURL);
    }

    public ResponseEntity<SendMessageDto> oAuthLogin(String code) {
        GoogleOAuthTokenRequestDto googleOAuthTokenRequestDto = googleOAuth.getAccessToken(code);
        GoogleUserDto googleUserDto = googleOAuth.getUserInfo(googleOAuthTokenRequestDto);

        createOAuthUser(googleUserDto);

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(googleUserDto.getEmail()));

        return SendMessageDto.toResponseEntity(SuccessCode.LOGIN_SUCCESS);
    }

    @Transactional
    void createOAuthUser(GoogleUserDto googleUserDto) {
        Optional<User> userOptional = userRepository.findByEmail(googleUserDto.getEmail());
        if (userOptional.isEmpty()) {
            userRepository.save(new User(googleUserDto));
        } else if (userOptional.get().getType() != SocialType.GOOGLE) {
            throw new CustomException(ErrorCode.NOT_SOCIAL_EMAIL);
        }
    }

}
