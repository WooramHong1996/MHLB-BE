package com.gigajet.mhlb.domain.user.dto;

import lombok.Getter;

@Getter
public class GoogleOAuthTokenRequestDto {

    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private String scope;
    private String token_type;

}
