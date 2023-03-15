package com.gigajet.mhlb.domain.mypage.dto;

import lombok.Getter;

public class ChangeMypageDto {
    @Getter
    public static class NameRequest{
        private String userName;
    }
    @Getter
    public static class DescRequest{
        private String userDesc;
    }
    @Getter
    public static class JobRequest{
        private String userJob;
    }
    @Getter
    public static class ImageRequest{
        private String userImage;
    }
}
