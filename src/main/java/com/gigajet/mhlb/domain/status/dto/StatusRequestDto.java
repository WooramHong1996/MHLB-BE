package com.gigajet.mhlb.domain.status.dto;

import com.gigajet.mhlb.domain.status.entity.StatusEnum;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
