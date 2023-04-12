package com.gigajet.mhlb.domain.mypage.dto;

import lombok.Getter;

public class MypageRequestDto {

    @Getter
    public static class Name {
        private String userName;
    }

    @Getter
    public static class Description {
        private String userDesc;
    }

    @Getter
    public static class Job {
        private String userJob;
    }

}
