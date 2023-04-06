package com.gigajet.mhlb.domain.user.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UserRequestDto {
//    패스워드 - 8글자 이상 20글자 미만, 알파벳 대문자, 소문자, 숫자 필수 포함 - regex : /[0-9]/g , /[a-z]/g , /[A-Z]/g

    @Getter
    public static class CheckEmail {

        @NotBlank(message = "값이 입력되지 않음")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "잘못된 형식의 이메일")
        private String email;

    }

    @Getter
    public static class Register {

        @NotBlank(message = "blank")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "wrong pattern")
        private String email;

        @NotBlank(message = "blank")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$", message = "wrong pattern")
        private String password;

        private String userName;

        private String userImage;

        private String userJob;

        private String userDesc;

    }

    @Getter
    public static class Login {

        @NotBlank(message = "blank")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "wrong pattern")
        private String email;

        private String password;

    }

    @Getter
    public static class Password {

        @NotBlank(message = "blank")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$", message = "wrong pattern")
        private String password;

    }

}
