package com.gigajet.mhlb.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigajet.mhlb.domain.alarm.dto.AlarmRequestDto;
import com.gigajet.mhlb.domain.alarm.dto.AlarmResponseDto;
import com.gigajet.mhlb.domain.alarm.dto.AlarmResponseDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AlarmSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            log.info(publishMessage);

            AlarmRequestDto request = objectMapper.readValue(publishMessage, AlarmRequestDto.class);
            AlarmResponseDto.AlarmChatResponse chat = new AlarmResponseDto.AlarmChatResponse(request);

            messagingTemplate.convertAndSend("/sub/unread-message/" + request.getUserId(), chat);
            log.info("alarm 보내기 성공!");
        } catch (Exception e) {
            log.error(e.getMessage());
//            throw new CustomException(ErrorCode.UNDEFINED_REQUEST);
        }
    }
}