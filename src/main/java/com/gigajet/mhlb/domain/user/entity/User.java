package com.gigajet.mhlb.domain.user.entity;

import com.gigajet.mhlb.domain.user.dto.GoogleOAuthRequestDto;
import com.gigajet.mhlb.domain.user.dto.UserRequestDto;
import com.gigajet.mhlb.domain.user.social.SocialType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "users")
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    private String description;

    private String password;

    private String job;

    private SocialType type;

    @Column(nullable = false)
    private Boolean isShow;

    public User(UserRequestDto.Register registerDto, String password, String image) {
        this.image = image;
        this.email = registerDto.getEmail();
        this.username = registerDto.getUserName();
        this.description = registerDto.getUserDesc();
        this.password = password;
        this.job = registerDto.getUserJob();
        this.isShow = true;
    }

    public User(GoogleOAuthRequestDto.GoogleUser googleUserDto) {
        this.image = googleUserDto.getPicture();
        this.email = googleUserDto.getEmail();
        this.username = googleUserDto.getName();
        this.type = SocialType.GOOGLE;
        this.isShow = true;
    }

    public void resetPassword(String password) {
        this.password = password;
    }
}
