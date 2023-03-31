package com.gigajet.mhlb.domain.user.entity;

import com.gigajet.mhlb.domain.mypage.dto.MypageRequestDto;
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

    @Column
    private String description;

    @Column
    private String password;

    @Column
    private String job;

    @Column
    private SocialType type;

    @Column(nullable = false)
    private Integer isShow;

    public User(UserRequestDto.Register registerDto, String password, String image) {
        this.image = image;
        this.email = registerDto.getEmail();
        this.username = registerDto.getUserName();
        this.description = registerDto.getUserDesc();
        this.password = password;
        this.job = registerDto.getUserJob();
        this.isShow = 1;
    }

    public User(GoogleOAuthRequestDto.GoogleUser googleUserDto) {
        this.image = googleUserDto.getPicture();
        this.email = googleUserDto.getEmail();
        this.username = googleUserDto.getName();
        this.type = SocialType.GOOGLE;
        this.isShow = 1;
    }

    public void updateName(MypageRequestDto.Name nameRequest) {
        this.username = nameRequest.getUserName();
    }

    public void updateJob(MypageRequestDto.Job jobRequest) {
        this.job = jobRequest.getUserJob();
    }

    public void updateDesc(MypageRequestDto.Description descRequest) {
        this.description = descRequest.getUserDesc();
    }

    public void updateImage(String imageUrl) {
        this.image = imageUrl;
    }

    public void resetPassword(String password) {
        this.password = password;
    }
}
