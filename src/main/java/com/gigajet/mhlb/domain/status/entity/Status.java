package com.gigajet.mhlb.domain.status.entity;

import com.gigajet.mhlb.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@NoArgsConstructor
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private StatusEnum status;
    private LocalDate updateDay;
    private LocalTime updateTime;

    public Status(User user) {
        this.user = user;
        this.status = StatusEnum.COB;
        this.updateDay = LocalDate.now();
        this.updateTime = LocalTime.now();
    }

    public Status(User user, StatusEnum status) {
        this.user = user;
        this.updateDay = LocalDate.now();
        this.updateTime = LocalTime.now();
        this.status = status;
    }
}
