package com.gigajet.mhlb.domain.user.dto;

import lombok.Getter;
@Getter
public class UserResponseDto {
    private final String userImage;
    private final Long userId;

    public UserResponseDto(String userImage, Long userId) {
        this.userImage = userImage;
        this.userId = userId;
    }
}
