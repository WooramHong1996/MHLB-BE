package com.gigajet.mhlb.domain.status.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    WIP("Working", 0),
    BRB("AFK", 1),
    COB("NotWorking", 3),
    OOO("Leave", 3),
    OTL("Eating", 1),
    IC("Meeting", 1),
    OOT("BusinessTrip", 2);

    private final String status;
    /**
     * 0 : 녹색 : 근무
     * <p>
     * 1 : 노랜색 : 자리비움, 식사중, 회의
     * <p>
     * 2 : 빨간색 : 출장중,
     * <p>
     * 3 : 회색 : 업무종료, 휴가중
     */
    private final Integer color;
}
