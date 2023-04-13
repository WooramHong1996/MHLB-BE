package com.gigajet.mhlb.domain.chat.service;

import com.gigajet.mhlb.domain.alarm.Entity.Alarm;
import com.gigajet.mhlb.domain.alarm.Entity.AlarmTypeEnum;
import com.gigajet.mhlb.domain.alarm.Repository.AlarmRepository;
import com.gigajet.mhlb.domain.alarm.dto.AlarmRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.domain.chat.entity.Chat;
import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import com.gigajet.mhlb.domain.chat.entity.MessageId;
import com.gigajet.mhlb.domain.chat.entity.UserAndMessage;
import com.gigajet.mhlb.domain.chat.repository.ChatRepository;
import com.gigajet.mhlb.domain.chat.repository.ChatRoomRepository;
import com.gigajet.mhlb.domain.chat.repository.MessageIdRepository;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageIdRepository messageIdRepository;
    private final AlarmRepository alarmRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private final JwtUtil jwtUtil;


    //메시지 보내기
    @Transactional
    public Long getMessageId() {
        MessageId messageId = messageIdRepository.findTopByKey(1);
        if (messageId == null) {
            messageId = new MessageId(1L);
        } else {
            messageId.addMessageId();
        }
        messageIdRepository.save(messageId);
        return messageId.getMessageId();
    }

    @Transactional
    public void sendMsg(ChatRequestDto.Chat message, String email, String sessionId, Long messageId) {
        Long id = userRepository.findByEmail(email).get().getId();
        Long workspaceId = message.getWorkspaceId();

        Chat chat = Chat.builder()
                .senderId(id)
                .inBoxId(message.getUuid())
                .workspaceId(workspaceId)
                .message(message.getMessage())
                .messageId(messageId)
                .build();
        chat.setCreatedAt(LocalDateTime.now());

        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(chat.getInBoxId());

        log.info("여기1");
        Map<Object, Object> room = redisTemplate.opsForHash().entries("/sub/inbox/" + message.getUuid());
        if (room.size() == 1) { //방에 혼자일 경우 상대의 안읽은 메시지를 +1
            for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {
                Long userId = userAndMessage.getUserId();
                if (Objects.equals(id, userId)) {
                    continue;
                }
                userAndMessage.addUnread();

                Optional<Alarm> optionalAlarm = alarmRepository.findAllByUserIdAndWorkspaceIdAndUuid(userId, workspaceId, message.getUuid());
                if (optionalAlarm.isPresent() && !optionalAlarm.get().getUnreadMessage()) {
                    log.info(optionalAlarm.get().getUnreadMessage().toString());
                    optionalAlarm.get().toggleUnreadMessage();
                }

                checkUnreadMessage(userId, workspaceId, message.getUuid(), optionalAlarm);
            }
        }
        chatRoom.update(chat);
        chatRoomRepository.save(chatRoom);

        chatRepository.save(chat);
        log.info("chat 저장 완료");

        redisTemplate.convertAndSend("chatMessageChannel", new ChatResponseDto.Convert(chat));
    }


    /*
        메세지 읽음 처리 메서드
        기존에 websocket에 session이 연결될 때 사용하던 메서드였지만
        Connect를 한번만, Subscribe을 여러번하기로 변경해서 subscribe event때 실행하는 메서드로 변경
     */
    @Transactional
    public void readMessages(StompHeaderAccessor accessor) {
        log.info("connect");
        jwt(accessor.getFirstNativeHeader("Authorization"))
        User user = userRepository.findByEmail().orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        String uuid = accessor.getFirstNativeHeader("uuid");

        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(uuid);

        if (chatRoom == null) {
            return;
        }

        for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {
            if (user.getId() != userAndMessage.getUserId()) {
                continue;
            }
            userAndMessage.resetUnread();
        }
        chatRoomRepository.save(chatRoom);
        Optional<Alarm> optionalAlarm = alarmRepository.findByUserIdAndWorkspaceIdAndUuidAndUnreadMessage(user.getId(), chatRoom.getWorkspaceId(), uuid, true);
        if (optionalAlarm.isPresent()) {
            alarmRepository.update(false, optionalAlarm.get().getId());
            checkReadMessage(optionalAlarm.get());
        }
    }

    /**
     * disconnect시 endpoint의 값을 받아올 수 없으므로 sessionId : endpoint의 데이터를 저장해둠
     */
    @Transactional
    public void checkRoom(StompHeaderAccessor accessor) {
        if (!(accessor.getDestination().split("/")[2].equals("inbox"))) {
            return;
        }
        Long id = userRepository.findByEmail((accessor.getFirstNativeHeader("Authorization"))).get().getId();
        log.info("subscribe user id : " + id);
        String endpoint = accessor.getDestination();
        log.info("subscribe endpoint : " + endpoint);
        if (!(endpoint.contains("/inbox/"))) {
            return;
        }
        Map<Object, Object> room = redisTemplate.opsForHash().entries(endpoint);
        if (room.size() == 0) {//해당 채팅방 최초 접속시
            Map<String, Long> userSessionInfo = new HashMap<>();

            userSessionInfo.put(accessor.getSessionId(), id);

            redisTemplate.opsForHash().putAll(accessor.getDestination(), userSessionInfo);
            redisTemplate.opsForValue().set(accessor.getSessionId(), accessor.getDestination());

        } else {//채팅방에 이미 유저가 존재 할 경우
            Map<String, Long> updatedData = new HashMap<>();
            for (Map.Entry<Object, Object> entry : room.entrySet()) {
                String key = (String) entry.getKey();
                Long value = (Long) entry.getValue();
                updatedData.put(key, value);
            }
            updatedData.put(accessor.getSessionId(), id);
            redisTemplate.opsForHash().putAll(accessor.getDestination(), updatedData);

            redisTemplate.opsForValue().set(accessor.getSessionId(), accessor.getDestination());
        }
    }

    @Transactional
    public void exitRoom(StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        log.info("disconnect session id : " + sessionId);
        String endpoint = (String) redisTemplate.opsForValue().get(sessionId);
        log.info("disconnect endpoint : " + endpoint);
        Map<Object, Object> room = redisTemplate.opsForHash().entries(endpoint);

        if (room.size() == 1) {//disconnect시 마지막 인원일 경우 바로 삭제
            redisTemplate.delete(sessionId);
            redisTemplate.delete(endpoint);
        } else {
            Map<String, Long> updatedData = new HashMap<>();

            for (Map.Entry<Object, Object> entry : room.entrySet()) {
                if ((entry.getValue()).equals(sessionId)) {
                    continue;
                }
                String key = (String) entry.getKey();
                Long value = (Long) entry.getValue();
                updatedData.put(key, value);
                System.out.println();
            }

            redisTemplate.delete(endpoint);
            redisTemplate.opsForHash().putAll(endpoint, updatedData);//기존 값 제거 후 새 값 저장
            redisTemplate.delete(sessionId);
        }
    }

    private void checkUnreadMessage(Long id, Long workspaceId, String uuid, Optional<Alarm> optionalAlarm) {
        //user -> 메시지를 읽지 않은 사람
        User user = userRepository.findById(id).orElseThrow();

        Alarm alarm;

        alarm = optionalAlarm.orElseGet(() -> alarmRepository.save(Alarm.builder()
                .unreadMessage(true)
                .type(AlarmTypeEnum.CHAT)
                .uuid(uuid)
                .workspaceId(workspaceId)
                .user(user).build()));

        AlarmRequestDto alarmRequestDto = new AlarmRequestDto(alarm);

        redisTemplate.convertAndSend("alarmMessageChannel", alarmRequestDto);
    }

    private void checkReadMessage(Alarm alarm) {
        AlarmRequestDto alarmRequestDto = new AlarmRequestDto(alarm);
        redisTemplate.convertAndSend("alarmMessageChannel", alarmRequestDto);
    }
}
