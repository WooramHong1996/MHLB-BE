package com.gigajet.mhlb.domain.status.dto;

import com.gigajet.mhlb.domain.status.entity.SqlStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatusResponseDto {
    private Long userId;
    private String status;
    private Integer color;

    public StatusResponseDto(SqlStatus status) {
        this.userId = status.getUser().getId();
        this.status = status.getStatus().getStatus();
        this.color = status.getStatus().getColor();
    }
}
