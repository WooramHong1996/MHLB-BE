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
import com.gigajet.mhlb.domain.status.repository.SqlStatusRepository;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final UserRepository userRepository;
    private final SqlStatusRepository statusRepository;
    private final MessageIdRepository messageIdRepository;
    private final AlarmRepository alarmRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private final JwtUtil jwtUtil;

    @Transactional
    public void sendMsg(ChatRequestDto.Chat message, String email, String sessionId) {
        MessageId messageId = messageIdRepository.findTopByKey(1);
        messageId.addMessageId();
//        MessageId messageId = new MessageId(1L);
        messageIdRepository.save(messageId);

        Long id = userRepository.findByEmail(email).get().getId();

        Chat chat = Chat.builder()
                .senderId(id)
                .inBoxId(message.getUuid())
                .workspaceId(message.getWorkspaceId())
                .message(message.getMessage())
                .messageId(messageId.getMessageId())
                .build();
        chat.setCreatedAt(LocalDateTime.now());

        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(chat.getInBoxId());

        Map<Object, Object> room = redisTemplate.opsForHash().entries("/sub/inbox/" + message.getUuid());
        if (room.size() == 1) { //방에 혼자일 경우 상대의 안읽은 메시지를 +1
            for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {
                if (id == userAndMessage.getUserId()) {
                    continue;
                }
                userAndMessage.addUnread();
                messageIfExistsOtherUser(userAndMessage.getUserId(), message.getWorkspaceId());
            }
        }

        chatRoom.update(chat);
        chatRoomRepository.save(chatRoom);

        chatRepository.save(chat);

        redisTemplate.convertAndSend("chatMessageChannel", new ChatResponseDto.Convert(chat));
    }

    @Transactional
    public List<ChatResponseDto.Chatting> getChat(User user, Long workspaceId, Long opponentsId, Pageable pageable) {
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(opponentsId, workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        userRepository.findById(user.getId()).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        List<ChatResponseDto.Chatting> chatList = new ArrayList<>();

        HashSet<Long> userIdSet = new HashSet<>();
        userIdSet.add(user.getId());
        userIdSet.add(opponentsId);

        ChatRoom chatRoom = chatRoomRepository.findByUserSetAndWorkspaceId(userIdSet, workspaceId);
        if (chatRoom == null) {
            return new ArrayList<>();
        }

        Slice<Chat> messageList = chatRepository.findByInBoxId(chatRoom.getInBoxId(), pageable);
        for (Chat chat : messageList) {
            chatList.add(new ChatResponseDto.Chatting(chat));
        }

        Collections.sort(chatList, (a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));

        List<Alarm> alarmList = alarmRepository.findAllByUserIdAndWorkspaceIdAndUuidAndUnreadMessage(user.getId(), workspaceId, chatRoom.getInBoxId(), true);
        alarmRepository.saveAll(alarmList);

        Optional<Alarm> alarm = alarmRepository.findTopByUserAndWorkspaceIdAndUnreadMessage(user, workspaceId, true);
        if (alarm.isEmpty()) {
            AlarmRequestDto socket = new AlarmRequestDto(Alarm.builder()
                    .unreadMessage(false)
                    .type(AlarmTypeEnum.CHAT)
                    .workspaceId(workspaceId)
                    .user(user).build());
            redisTemplate.convertAndSend("alarmMessageChannel", socket);
        }

        return chatList;
    }

    @Transactional
    public ChatResponseDto.GetUuid getUuid(User user, Long workspaceId, Long opponentsId) {
        if (user.getId() == opponentsId) {
            throw new CustomException(ErrorCode.WRONG_USER);
        }
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(opponentsId, workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        List<UserAndMessage> userIdList = new ArrayList<>();
        userIdList.add(new UserAndMessage(user.getId()));
        userIdList.add(new UserAndMessage(opponentsId));

        HashSet<Long> userIdSet = new HashSet<>();
        userIdSet.add(user.getId());
        userIdSet.add(opponentsId);

        ChatRoom chatRoom = chatRoomRepository.findByUserSetAndWorkspaceId(userIdSet, workspaceId);

        if (chatRoom == null) {
            String inboxId = String.valueOf(UUID.randomUUID());

            ChatRoom inbox = ChatRoom.builder()
                    .inBoxId(inboxId)
                    .userAndMessages(userIdList)
                    .userSet(userIdSet)
                    .workspaceId(workspaceId)
                    .build();
            chatRoomRepository.save(inbox);
            return new ChatResponseDto.GetUuid(inbox.getInBoxId());
        }
        return new ChatResponseDto.GetUuid(chatRoom.getInBoxId());
    }

    @Transactional
    public List<ChatResponseDto.Inbox> getInbox(User user, Long workspaceId) {
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        List<ChatResponseDto.Inbox> response = new ArrayList<>();
        List<ChatRoom> list = chatRoomRepository.findByWorkspaceIdAndUserSetInOrderByLastChatDesc(workspaceId, user.getId());
        //user 요청자

        for (ChatRoom chatRoom : list) {
            ChatResponseDto.Inbox inbox = new ChatResponseDto.Inbox();
            for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {//리스트대로 돌림
                if (user.getId() == userAndMessage.getUserId()) {
                    inbox.unreadMessage(userAndMessage.getUnread());
                    continue;
                }
                Optional<User> opponents = userRepository.findById(userAndMessage.getUserId());
                //퇴사한 유저인지 확인하여 퇴사한 경우 채팅 보낼 수 없도록 기능 수정 필요
                inbox.inbox(chatRoom, opponents.get(), statusRepository.findTopByUserIdOrderByUpdateDayDescUpdateTimeDesc(userAndMessage.getUserId()).getStatus().getColor());
            }
            response.add(inbox);
        }
        return response;
    }

    public String resolveToken(String authorization) {
        return jwtUtil.getUserEmail(authorization.substring(7));
    }

    @Transactional
    public void readMessages(StompHeaderAccessor accessor) {
        Long id = userRepository.findByEmail(resolveToken(accessor.getFirstNativeHeader("Authorization"))).get().getId();

        String uuid = accessor.getFirstNativeHeader("uuid");

        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(uuid);

        for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {
            if (id != userAndMessage.getUserId()) {
                continue;
            }
            userAndMessage.resetUnread();
        }
        chatRoomRepository.save(chatRoom);
    }

    /**
     * disconnect시 endpoint의 값을 받아올 수 없으므로 sessionId : endpoint의 데이터를 저장해둠
     */
    @Transactional
    public void checkRoom(StompHeaderAccessor accessor) {
        Long id = userRepository.findByEmail(resolveToken(accessor.getFirstNativeHeader("Authorization"))).get().getId();
        String endpoint = accessor.getDestination();
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
        String endpoint = (String) redisTemplate.opsForValue().get(sessionId);

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

    private void messageIfExistsOtherUser(Long id, Long workspaceId) {
        //user -> 메시지를 읽지 않은 사람
        User user = userRepository.findById(id).orElseThrow();

        Alarm alarm = alarmRepository.save(Alarm.builder()
                .unreadMessage(true)
                .type(AlarmTypeEnum.CHAT)
                .workspaceId(workspaceId)
                .user(user).build());
        AlarmRequestDto alarmRequestDto = new AlarmRequestDto(alarm);

        redisTemplate.convertAndSend("alarmMessageChannel", alarmRequestDto);
    }
}