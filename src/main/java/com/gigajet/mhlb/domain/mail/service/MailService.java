package com.gigajet.mhlb.domain.mail.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.user.dto.UserRequestDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final UserRepository userRepository;

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String myAddress;

    // 비밀번호 찾기 이메일 발송
    public ResponseEntity<SendMessageDto> sendMail(String email) {
        userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.UNREGISTER_USER));

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        saveRandomNumberAndEmail(uuid, email);

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setFrom(myAddress);
            mimeMessageHelper.setSubject("PIN ME :: 이메일 인증");
            mimeMessageHelper.setText("<a href='http://localhost:3000/reset-password/" + uuid + "'>비밀번호 변경</a>", true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return SendMessageDto.toResponseEntity(SuccessCode.VALID_EMAIL);
    }

    // 캐시 저장
    private void saveRandomNumberAndEmail(String randomNumber, String email) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(randomNumber, email, Duration.ofMinutes(3));
    }

    // 비밀번호 찾기 인증 코드 유효 검사
    public ResponseEntity<SendMessageDto> checkCode(String uuid) {
        String email = redisTemplate.opsForValue().get(uuid);
        if (email == null) {
            throw new CustomException(ErrorCode.INVALID_CODE);
        }

        return SendMessageDto.toResponseEntity(SuccessCode.VALID_CODE);
    }

    // 비밀번호 변경
    @Transactional
    public ResponseEntity<SendMessageDto> resetPassword(String uuid, UserRequestDto.Password passwordDto) {
        String email = redisTemplate.opsForValue().get(uuid);
        if (email == null) {
            throw new CustomException(ErrorCode.INVALID_CODE);
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.UNREGISTER_USER));
        user.resetPassword(passwordEncoder.encode(passwordDto.getPassword()));

        return SendMessageDto.toResponseEntity(SuccessCode.RESET_PASSWORD_SUCCESS);
    }


    public void inviteMail(String email) {
        //유저 없는거 확인 하고 왓서오
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setFrom(myAddress);
            mimeMessageHelper.setSubject("핀미에 초대댓서오");
            mimeMessageHelper.setText("<h1>초대 바들래용?</h1>", true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
