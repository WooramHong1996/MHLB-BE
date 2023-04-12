package com.gigajet.mhlb.domain.status.dto;

import com.gigajet.mhlb.domain.status.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class StatusResponseDto {
    private final Long userId;
    private final String status;
    private final Integer color;

    public StatusResponseDto(Status status) {
        this.userId = status.getUser().getId();
        this.status = status.getStatus().getStatus();
        this.color = status.getStatus().getColor();
    }

    public StatusResponseDto(StatusResponseDto.Convert convert) {
        this.userId = convert.getUserId();
        this.status = convert.getStatus();
        this.color = convert.getColor();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Convert {
        private Long userId;
        private String status;
        private Integer color;

        public Convert(Status status) {
            this.userId = status.getUser().getId();
            this.status = status.getStatus().getStatus();
            this.color = status.getStatus().getColor();
        }
    }
}
