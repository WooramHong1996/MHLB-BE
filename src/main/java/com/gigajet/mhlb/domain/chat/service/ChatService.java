package com.gigajet.mhlb.domain.chat.service;

import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponse;
import com.gigajet.mhlb.domain.chat.entity.Chat;
import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import com.gigajet.mhlb.domain.chat.repository.ChatRepository;
import com.gigajet.mhlb.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

//    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatResponse sendMsg(ChatRequestDto message){
//        chatRepository.save(new Chat());
        chatRoomRepository.save(new ChatRoom());
        return null;
    }



//    @Transactional(readOnly = true)//이전메시지 불러오기 레거시
//    public Flux<Chat> getMsg(User user, Long userId, Long workspaceId) {
//        List<Long> userIdList = new ArrayList<>();
//        if(user.getId()>userId) {
//            userIdList.add(user.getId());
//            userIdList.add(userId);
//        }else{
//            userIdList.add(userId);
//            userIdList.add(user.getId());
//        }
//        ChatRoom chatRoom = chatMemRepository.findByUserListAndWorkspaceId(userIdList, workspaceId);
//
//        return chatRepository.findByWorkspaceIdAndRoomNum(chatRoom.getWorkspaceId(), chatRoom.getRoomNum()).subscribeOn(Schedulers.boundedElastic());
//    }

//    @Transactional//방 불러오기 레거시
//    public ChatResponse setMsg(User user, Long userId, Long workspaceId, ChatRequest chatRequest) {
//        List<Long> userIdList = new ArrayList<>();
//        if(user.getId()>userId) {
//            userIdList.add(user.getId());
//            userIdList.add(userId);
//        }else{
//            userIdList.add(userId);
//            userIdList.add(user.getId());
//        }
//        ChatRoom chatRoom = chatMemRepository.findByUserListAndWorkspaceId(userIdList, workspaceId);
//
//            Chat chat = Chat.builder()
//                    .message(chatRequest.getMessage())
//                    .sender(user.getEmail())
//                    .workspaceId(workspaceId)
//                    .roomNum(chatRoom.getRoomNum())
//                    .build();
//            chat.setCreatedAt(LocalDateTime.now());
//
//            chatRepository.insert(chat).subscribe(System.out::println);
//
//            return new ChatResponse(chat);
//
//    }
}