package com.gigajet.mhlb.domain.status.dto;

import com.gigajet.mhlb.domain.status.entity.StatusEnum;
import lombok.Getter;

@Getter
public class StatusRequestDto {
    private StatusEnum status;
}
