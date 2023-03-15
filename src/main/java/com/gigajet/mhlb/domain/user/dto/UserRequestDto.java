package com.gigajet.mhlb.domain.user.dto;

import lombok.Getter;

public class UserRequestDto {

    @Getter
    public static class CheckEmailDto {

        private String email;

    }

    @Getter
    public static class Register {

        private String email;

        private String password;

        private String userName;

        private String userImage;

        private String userJob;

        private String userDesc;

    }

    @Getter
    public static class Login {

        private String email;

        private String password;

    }

}
