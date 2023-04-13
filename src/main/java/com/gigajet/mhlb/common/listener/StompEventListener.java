package com.gigajet.mhlb.common.listener;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class StompEventListener {

    @EventListener(SessionConnectEvent.class)
    public void connect(SessionConnectEvent event) {
        chatService.readMessages(StompHeaderAccessor.wrap(event.getMessage()));
    }

    @EventListener(SessionSubscribeEvent.class)
    public void subscribe(SessionSubscribeEvent event) {
        chatService.checkRoom(StompHeaderAccessor.wrap(event.getMessage()));
    }

    @EventListener(SessionDisconnectEvent.class)
    public void disconnect(SessionDisconnectEvent event) {
        chatService.exitRoom(StompHeaderAccessor.wrap(event.getMessage()));
    }
}
