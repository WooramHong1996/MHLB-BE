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
import com.gigajet.mhlb.domain.status.repository.StatusRepository;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceUserRepository;
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
    private final StatusRepository statusRepository;
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
        }


    @Transactional
    public void sendMsg(ChatRequestDto.Chat message, String email, String sessionId, Long messageId) {
        Long id = userRepository.findByEmail(email).get().getId();

        Chat chat = Chat.builder()
                .senderId(id)
                .inBoxId(message.getUuid())
                .workspaceId(message.getWorkspaceId())
                .message(message.getMessage())
                .messageId(messageId)
                .build();
        chat.setCreatedAt(LocalDateTime.now());
        messageId.addMessageId();

        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(chat.getInBoxId());
        Long otherId = 0L;
        UserAndMessage unmessage = new UserAndMessage(otherId);

        for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {
            if (id == userAndMessage.getUserId()) {
                continue;
            }
            otherId = userAndMessage.getUserId();
            unmessage = userAndMessage;
        }

        Map<Object, Object> room = redisTemplate.opsForHash().entries("/sub/inbox/" + message.getUuid());
        if (!room.containsValue(otherId)) { //방에 혼자일 경우 상대의 안읽은 메시지를 +1
            unmessage.addUnread();
//                userAndMessage.addUnread();
            checkUnreadMessage(unmessage.getUserId(), message.getWorkspaceId(), message.getUuid());
        }

        chatRoom.update(chat);
        chatRoomRepository.save(chatRoom);
        messageIdRepository.save(messageId);

        chatRepository.save(chat);

        redisTemplate.convertAndSend("chatMessageChannel", new ChatResponseDto.Convert(chat));
    }

    //이전 채팅목록 불러오기
    @Transactional
    public List<ChatResponseDto.Chatting> getChat(User user, Long workspaceId, Long opponentsId, Pageable pageable) {
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, true).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(opponentsId, workspaceId, true).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

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

    //uuid 불러오기
    @Transactional
    public ChatResponseDto.GetUuid getUuid(User user, Long workspaceId, Long opponentsId) {
        if (user.getId() == opponentsId) {
            throw new CustomException(ErrorCode.WRONG_USER);
        }
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, true).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(opponentsId, workspaceId, true).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

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

    //인박스 불러오기
    @Transactional
    public List<ChatResponseDto.Inbox> getInbox(User user, Long workspaceId) {
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, true).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

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

    //토큰 값 추출
    public String resolveToken(String authorization) {
        return jwtUtil.getUserEmail(authorization.substring(7));
    }

    //메시지 읽음 처리
    @Transactional
    public void readMessages(StompHeaderAccessor accessor) {
//        String email = String.valueOf(userRepository.findByEmail(resolveToken(accessor.getFirstNativeHeader("Authorization"))));
        Long id = userRepository.findByEmail(resolveToken(accessor.getFirstNativeHeader("Authorization"))).get().getId();
        User user = userRepository.findById(id).get();

        String uuid = accessor.getFirstNativeHeader("uuid");

        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(uuid);

        for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {
            if (user.getId() != userAndMessage.getUserId()) {
                continue;
            }
            userAndMessage.resetUnread();
        }
        chatRoomRepository.save(chatRoom);
        List<Alarm> alarms = alarmRepository.findByUser_IdAndWorkspaceIdAndUuidAndUnreadMessage(user.getId(), chatRoom.getWorkspaceId(), uuid, true);
        if (!alarms.isEmpty()) {
            for (Alarm alarm : alarms) {
                alarm.change();
            }
        }
        Optional<Alarm> alarm = alarmRepository.findTopByUserAndWorkspaceIdAndUnreadMessage(user, chatRoom.getWorkspaceId(), true);
        if (alarm.isEmpty()) {
            checkReadMessage(user, chatRoom.getWorkspaceId());
        }
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

    private void checkUnreadMessage(Long id, Long workspaceId, String uuid) {
        //user -> 메시지를 읽지 않은 사람
        User user = userRepository.findById(id).orElseThrow();

        Alarm alarm = alarmRepository.save(Alarm.builder()
                .unreadMessage(true)
                .type(AlarmTypeEnum.CHAT)
                .workspaceId(workspaceId)
                .uuid(uuid)
                .user(user).build());
        AlarmRequestDto alarmRequestDto = new AlarmRequestDto(alarm);

        redisTemplate.convertAndSend("alarmMessageChannel", alarmRequestDto);
    }

    private void checkReadMessage(User user, Long workspaceId) {

        Alarm alarm = alarmRepository.save(Alarm.builder()
                .unreadMessage(false)
                .type(AlarmTypeEnum.CHAT)
                .workspaceId(workspaceId)
                .user(user).build());
        AlarmRequestDto alarmRequestDto = new AlarmRequestDto(alarm);

        redisTemplate.convertAndSend("alarmMessageChannel", alarmRequestDto);
    }
}