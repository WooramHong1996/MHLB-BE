package com.gigajet.mhlb.domain.user.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UserRequestDto {

    @Getter
    public static class CheckEmail {

        @NotBlank(message = "blank")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "wrong pattern")
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
