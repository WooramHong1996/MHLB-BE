package com.gigajet.mhlb.domain.status.dto;

import com.gigajet.mhlb.domain.status.entity.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StatusResponseDto {

    @Getter
    @NoArgsConstructor
    public static class StatusInfo {
        private Long userId;
        private String status;
        private Integer color;

        public StatusInfo(Status status) {
            this.userId = status.getUser().getId();
            this.status = status.getStatus().getStatus();
            this.color = status.getStatus().getColor();
        }
    }
}
