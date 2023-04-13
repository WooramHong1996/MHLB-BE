package com.gigajet.mhlb.domain.status.dto;

import com.gigajet.mhlb.domain.status.entity.StatusEnum;
import com.gigajet.mhlb.global.exception.CustomException;
import com.gigajet.mhlb.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class StatusRequestDto {
    private String status;

    public StatusEnum textOf() {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        throw new CustomException(ErrorCode.STATUS_NOT_EXIST);
    }
}
