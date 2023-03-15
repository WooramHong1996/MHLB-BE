package com.gigajet.mhlb.domain.mail.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String myAddress;

    public ResponseEntity<SendMessageDto> sendMail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(myAddress);
        message.setSubject("뭐한라봉");
        message.setText("링크링크");

        mailSender.send(message);

        return SendMessageDto.toResponseEntity(SuccessCode.CHECKUP_SUCCESS);
    }
}
