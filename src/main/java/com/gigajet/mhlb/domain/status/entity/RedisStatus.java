package com.gigajet.mhlb.domain.status.entity;


import com.gigajet.mhlb.domain.status.dto.StatusRequestDto;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Getter
@RedisHash(value = "status")
public class RedisStatus {

    @Id
    private String id;
    @Indexed
    private String email;
    private String status;
    private LocalDateTime updatedAt;

    public RedisStatus(String email) {
        this.email = email;

        this.status = "기본값";

        this.updatedAt = LocalDateTime.now();
    }

//    public void update(StatusRequestDto statusRequestDto) {
//        this.status = statusRequestDto.getStatus();
//
//        this.updatedAt = LocalDateTime.now();
//    }
}
