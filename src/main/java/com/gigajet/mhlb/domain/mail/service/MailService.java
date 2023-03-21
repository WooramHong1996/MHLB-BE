package com.gigajet.mhlb.domain.mail.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
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
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.mail.username}")
    private String myAddress;

    // 비밀번호 찾기 이메일 발송
    public ResponseEntity<SendMessageDto> sendMail(String email) {
        userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.UNREGISTER_USER));

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        saveEmailAndRandomNumber(email, uuid);

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setFrom(myAddress);
            mimeMessageHelper.setSubject("뭐한라봉 :: 이메일 인증");
            mimeMessageHelper.setText("<h1>안뇽안뇽</h1>", true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return SendMessageDto.toResponseEntity(SuccessCode.VALID_EMAIL);
    }

    private void saveEmailAndRandomNumber(String email, String randomNumber) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(email, randomNumber, Duration.ofMinutes(5));
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
