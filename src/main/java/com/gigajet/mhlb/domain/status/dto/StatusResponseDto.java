package com.gigajet.mhlb.domain.status.dto;

import com.gigajet.mhlb.domain.status.entity.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatusResponseDto {
    private String email;
    private String status;

    public StatusResponseDto(Status status) {
        this.email = status.getEmail();
        this.status = status.getStatus();
    }
}
