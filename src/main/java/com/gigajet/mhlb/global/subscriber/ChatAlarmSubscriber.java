package com.gigajet.mhlb.global.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigajet.mhlb.domain.alarm.dto.ChatAlarmResponseDto;
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
public class ChatAlarmSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            log.info(publishMessage);

            ChatAlarmResponseDto.ConvertChatAlarm convertChatAlarm = objectMapper.readValue(publishMessage, ChatAlarmResponseDto.ConvertChatAlarm.class);
            messagingTemplate.convertAndSend("/sub/unread-message/" + convertChatAlarm.getReceiverId(), convertChatAlarm.getT());
            log.info("alarm 보내기 성공!");

        } catch (Exception exception) {
            log.error(exception.getMessage());
//            throw new CustomException(ErrorCode.UNDEFINED_REQUEST);
        }
    }
}