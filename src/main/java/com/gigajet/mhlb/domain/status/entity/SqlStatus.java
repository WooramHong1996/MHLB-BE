package com.gigajet.mhlb.domain.status.entity;

import com.gigajet.mhlb.domain.status.dto.StatusRequestDto;
import com.gigajet.mhlb.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class SqlStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private String status;
    private LocalDateTime updatedAt;

    public SqlStatus(User user) {
        this.user = user;
        this.status = "기본값";
        this.updatedAt = LocalDateTime.now();
    }

    public SqlStatus(User user, StatusRequestDto statusRequestDto) {
        this.user = user;
        this.status = statusRequestDto.getStatus();
        this.updatedAt = LocalDateTime.now();
    }
}
