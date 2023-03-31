package com.gigajet.mhlb.config;

import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;

//@Component
//@RequiredArgsConstructor
//public class WebSocketInterceptor implements ChannelInterceptor {
//    private final JwtUtil jwtTokenProvider;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (StompCommand.SEND.equals(accessor.getCommand())) {
//            String authToken = accessor.getFirstNativeHeader("Authorization");
//            authToken.substring(7);
//            accessor.addNativeHeader("user",authToken);
//        }
        ////======================================================
//        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        assert headerAccessor != null;
//        if(headerAccessor.getCommand() == StompCommand.SEND){
//      headerAccessor.getNativeHeader("Authorization").get(0);
//        headerAccessor.addNativeHeader("Authorization",headerAccessor.getNativeHeader("Authorization").get(0));
//        }
//        return message;
//    }
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel)
//    {
//        StompHeaderAccessor accessor =
//                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            Authentication user = securityContext.getAuthentication(); // access authentication header(s)
//            accessor.setUser(user);
//        }
//        return message;
//    }
//}

//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            jwtTokenProvider.validateToken(Objects.requireNonNull(accessor.getFirstNativeHeader("Authorization")).substring(7));
//        }

