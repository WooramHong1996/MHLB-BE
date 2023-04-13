package com.gigajet.mhlb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            log.info("publish message");
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            log.info(publishMessage);
            ChatResponseDto.Convert chat = objectMapper.readValue(publishMessage, ChatResponseDto.Convert.class);
            ChatResponseDto.Chat response = new ChatResponseDto.Chat(chat);

            log.info(chat.getInboxId() + "의 " + chat.getUserId() + "로 부터 " + chat.getMessage());
            messagingTemplate.convertAndSend("/sub/inbox/" + chat.getInboxId(), response);
            log.info("message 보내기 성공!");
        } catch (Exception e) {
            log.error(e.getMessage());
//            throw new CustomException(ErrorCode.UNDEFINED_REQUEST);
        }
    }
}