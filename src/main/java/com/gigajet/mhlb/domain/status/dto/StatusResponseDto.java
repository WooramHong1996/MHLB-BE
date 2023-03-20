package com.gigajet.mhlb.domain.status.dto;

import com.gigajet.mhlb.domain.status.entity.RedisStatus;
import com.gigajet.mhlb.domain.status.entity.SqlStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatusResponseDto {
    private String email;
    private String status;

    public StatusResponseDto(RedisStatus status) {
        this.email = status.getEmail();
        this.status = status.getStatus();
    }

    public StatusResponseDto(SqlStatus status) {
        this.email = status.getEmail();
        this.status = status.getStatus();
    }
}
