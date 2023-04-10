package com.gigajet.mhlb.domain.user.social;

import com.gigajet.mhlb.domain.user.dto.GoogleOAuthRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOAuth {

    @Value("${OAuth2.google.client-id}")
    private String googleClientId;
    @Value("${OAuth2.google.callback-url}")
    private String googleCallbackUrl;
    @Value("${OAuth2.google.client-secret}")
    private String googleClientSecret;

    private final RestTemplate restTemplate;

    public GoogleOAuthRequestDto.Token getAccessToken(String code) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("code", code);
        paramMap.put("client_id", googleClientId);
        paramMap.put("client_secret", googleClientSecret);
        paramMap.put("redirect_uri", googleCallbackUrl);
        paramMap.put("grant_type", "authorization_code");

        ResponseEntity<GoogleOAuthRequestDto.Token> responseEntity = restTemplate.postForEntity("https://oauth2.googleapis.com/token", paramMap, GoogleOAuthRequestDto.Token.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        }

        return null;
    }

    public GoogleOAuthRequestDto.GoogleUser getUserInfo(GoogleOAuthRequestDto.Token googleOAuthTokenRequestDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", googleOAuthTokenRequestDto.getToken_type() + " " + googleOAuthTokenRequestDto.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);
        ResponseEntity<GoogleOAuthRequestDto.GoogleUser> responseEntity = restTemplate.exchange("https://www.googleapis.com/oauth2/v1/userinfo", HttpMethod.GET, request, GoogleOAuthRequestDto.GoogleUser.class);

        return responseEntity.getBody();
    }

}
