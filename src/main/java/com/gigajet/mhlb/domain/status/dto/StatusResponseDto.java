package com.gigajet.mhlb.domain.status.dto;

import com.gigajet.mhlb.domain.status.entity.RedisStatus;
import com.gigajet.mhlb.domain.status.entity.SqlStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatusResponseDto {
    private Long userId;
    private String status;

    public StatusResponseDto(RedisStatus status) {
//        this.userId = status.getEmail();
        this.status = status.getStatus();
    }

    public StatusResponseDto(SqlStatus status) {
        this.userId = status.getUser().getId();
        this.status = status.getStatus();
    }
}
