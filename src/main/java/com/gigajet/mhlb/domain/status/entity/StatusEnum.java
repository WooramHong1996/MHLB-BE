package com.gigajet.mhlb.domain.status.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    WIP("근무",0),
    BRB("자리비움",1),
    COB("업무종료",3),
    OOO("휴가중",3),
    OTL("식사중",1),
    IC("회의",1),
    OOT("출장중",2);

    private final String status;
    /**
     * 0 : 녹색 : 근무
     *<p>
     * 1 : 노랜색 : 자리비움, 식사중, 회의
     *<p>
     * 2 : 빨간색 : 출장중,
     *<p>
     * 3 : 회색 : 업무종료, 휴가중
     */
    private final Integer color;
}
