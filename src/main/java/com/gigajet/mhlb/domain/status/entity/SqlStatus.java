package com.gigajet.mhlb.domain.status.entity;

import com.gigajet.mhlb.domain.status.dto.StatusRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class SqlStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String status;
    private LocalDateTime updatedAt;
    public SqlStatus (String email) {
        this.email = email;

        this.status = "기본값";

        this.updatedAt = LocalDateTime.now();
    }

    public void update(StatusRequestDto statusRequestDto) {
        this.status = statusRequestDto.getStatus();

        this.updatedAt = LocalDateTime.now();
    }
}
