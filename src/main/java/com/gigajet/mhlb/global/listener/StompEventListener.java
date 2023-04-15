package com.gigajet.mhlb.global.listener;

import com.gigajet.mhlb.domain.chat.service.ChatMessageService;
import com.gigajet.mhlb.domain.user.service.UserService;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompEventListener {

    private final ChatMessageService chatMessageService;
    private final UserService userService;

    private final JwtUtil jwtUtil;

    // 연결 시도
    @EventListener(SessionConnectEvent.class)
    public void connect(SessionConnectEvent event) {
        try {
            // 토큰 검사
            String userEmail = jwtUtil.getUserEmail(StompHeaderAccessor.wrap(event.getMessage()));
            if (userEmail == null) {
                throw new MessagingException("Invalid user email");
            }

            // 존재하는 user인지 검사
            userService.validateEmail(userEmail);

        } catch (Exception exception) {
//            event.getMessage().getHeaders().put("stompCommand", StompCommand.ERROR);
//            event.getMessage().getHeaders().put("simpErrorMessage", "Invalid user email");
            log.error(exception.getMessage());
        }
    }

    @EventListener(SessionConnectedEvent.class)
    public void connected(SessionConnectedEvent event) {
//        log.info("connected " + StompHeaderAccessor.wrap(event.getMessage()).getSessionId());
    }

    // 이 부분이 비대해질 예정
    @EventListener(SessionSubscribeEvent.class)
    public void subscribe(SessionSubscribeEvent event) {
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

            if (accessor.getDestination() == null || accessor.getSessionId() == null) {
                throw new MessagingException("subscribe socket info null");
            } else if (accessor.getDestination().contains("/inbox")) {
                chatMessageService.checkRoom(accessor);
                chatMessageService.readMessages(accessor);
            } else if (accessor.getDestination().contains("/workspace-invite")) {
//                log.info("subscribe workspace-invite");
            }
        } catch (Exception exception) {
//            event.getMessage().getHeaders().put("stompCommand", StompCommand.ERROR);
//            event.getMessage().getHeaders().put("simpErrorMessage", "subscribe error");
            log.error(exception.getMessage());
        }
    }

    @EventListener(SessionUnsubscribeEvent.class)
    public void unsubscribe(SessionUnsubscribeEvent event) {
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            String destination = accessor.getFirstNativeHeader("destination");

            if (destination == null) {
//                log.info("destination null");
            } else if (destination.contains("/inbox")) {
                chatMessageService.exitRoom(accessor);
            }
        } catch (Exception exception) {
//            event.getMessage().getHeaders().put("stompCommand", StompCommand.ERROR);
//            event.getMessage().getHeaders().put("simpErrorMessage", "unsubscribe error");
            log.error(exception.getMessage());
        }
    }

    @EventListener(SessionDisconnectEvent.class)
    public void disconnect(SessionDisconnectEvent event) {
    }
}
