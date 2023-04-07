package com.gigajet.mhlb.domain.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
@Service
@RequiredArgsConstructor
public class RedisRepository {
    private static final String ENTER_INFO = "ENTER_INFO";
    private static final String USER_INFO = "USER_INFO";

    /**
     * "ENTER_INFO", roomId, userId (유저가 입장한 채팅방 정보)
     */
    @Autowired
    @Resource(name = "redisTemplate")
    private HashOperations<String, Long, String> chatRoomInfo;

    /**
     * 채팅방 마다 유저가 읽지 않은 메세지 갯수 저장
     * 1:1 채팅에서만 사용 O, 그륩채팅에서 사용 X
     * roomId, userId, 안 읽은 메세지 갯수
     */
    @Resource(name = "redisTemplate")
    private HashOperations<String, Long, Long> chatRoomUnReadMessageInfo;

    /**
     * 상대 정보는 sessionId 로 저장, 나의 정보는 userId에 저장
     * "USER_INFO", sessionId, userId
     */
//    @Resource(name = "redisTemplate")
//    private HashOperations<String, String, Long> userInfo;

    // step1
    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void userEnterRoomInfo(Long userId, String chatRoomId) {
        chatRoomInfo.put(ENTER_INFO, userId, chatRoomId);
    }

    // 사용자가 채팅방에 입장해 있는지 확인
    public boolean existChatRoomUserInfo(Long userId) {
        return chatRoomInfo.hasKey(ENTER_INFO, userId);
    }

    // 사용자가 특정 채팅방에 입장해 있는지 확인
    public boolean existUserRoomInfo(String chatRoomId, Long userId) {
        return getUserEnterRoomId(userId).equals(chatRoomId);
    }

    // 사용자가 입장해 있는 채팅방 ID 조회
    public String getUserEnterRoomId(Long userId) {
        return chatRoomInfo.get(ENTER_INFO, userId);
    }

    // 사용자가 입장해 있는 채팅방 ID 조회
    @Transactional
    public void exitUserEnterRoomId(Long userId) {
        chatRoomInfo.delete(ENTER_INFO, userId);
    }

    // 채팅방에서 사용자가 읽지 않은 메세지의 갯수 초기화
    @Transactional
    public void initChatRoomMessageInfo(String chatRoomId, Long userId) {
        chatRoomUnReadMessageInfo.put(chatRoomId, userId, 0L);
    }

    // 채팅방에서 사용자가 읽지 않은 메세지의 갯수 추가
    @Transactional
    public void addChatRoomMessageCount(String chatRoomId, Long userId) {
        if (chatRoomUnReadMessageInfo.get(chatRoomId, userId)==null){
            chatRoomUnReadMessageInfo.put(chatRoomId, userId,  1L);
        }
        chatRoomUnReadMessageInfo.put(chatRoomId, userId, chatRoomUnReadMessageInfo.get(chatRoomId, userId) + 1);
    }

    public Long getChatRoomMessageCount(String chatRoomId, Long userId) {
        return chatRoomUnReadMessageInfo.get(chatRoomId, userId);
    }
}
