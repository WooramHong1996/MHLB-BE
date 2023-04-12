package com.gigajet.mhlb.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "회원정보 DTO")
@Getter
public class UserResponseDto {
    @Schema(description = "회원 프로필 이미지")
    private final String userImage;
    @Schema(description = "회원 id")
    private final Long userId;

    public UserResponseDto(String userImage, Long userId) {
        this.userImage = userImage;
        this.userId = userId;
    }
}
