package com.gigajet.mhlb.domain.status.entity;

import com.gigajet.mhlb.domain.status.dto.StatusRequestDto;
import com.gigajet.mhlb.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Entity
@NoArgsConstructor
public class SqlStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private StatusEnum status;
    private LocalDateTime updatedAt;
    private LocalDate updateDay;
    private LocalTime updateTime;

    public SqlStatus(User user) {
        this.user = user;
        this.status = StatusEnum.COB;
        this.updatedAt = LocalDateTime.now();
        this.updateDay = LocalDate.now();
        this.updateTime = LocalTime.now();
    }

    public SqlStatus(User user, StatusRequestDto statusRequestDto) {
        this.user = user;
        this.status = statusRequestDto.getStatus();
        this.updatedAt = LocalDateTime.now();
        this.updateDay = LocalDate.now();
        this.updateTime = LocalTime.now();
    }
}
