package com.gigajet.mhlb.domain.user.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GoogleUserDto {

    private String id;
    private String email;
    private Boolean verifiedEmail;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;
    private String locale;

}
