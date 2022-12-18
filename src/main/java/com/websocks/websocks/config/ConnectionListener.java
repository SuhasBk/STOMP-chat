package com.websocks.websocks.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.websocks.websocks.model.StompMessage;

@Component
public class ConnectionListener {
    @Autowired
    SimpMessagingTemplate template;
    
    @Autowired
    private Map<String, String> sessionManager;

    @EventListener
    public void onConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = Optional.ofNullable(accessor.getNativeHeader("client-id")).orElse(Collections.emptyList()).get(0);
        StompMessage message = StompMessage
                                    .builder()
                                    .message("New user - " + username + " has joined the chat! 🙌")
                                    .count(sessionManager.size())
                                    .build();
        template.convertAndSend("/connections", message);
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String user = sessionManager.get(sessionId);
        if(user != null) {
            StompMessage message = StompMessage
                    .builder()
                    .message(user + " has left the chat 👋")
                    .count(sessionManager.size())
                    .build();
            sessionManager.remove(sessionId);
            template.convertAndSend("/exits", message);
        }
    }

    @Scheduled(fixedRate = 5000, initialDelay = 1000)
    public void getLiveConnections() {
        StompMessage message = StompMessage
                .builder()
                .count(sessionManager.size())
                .users(new ArrayList<>(sessionManager.values()))
                .build();
        template.convertAndSend("/liveUsers", message);
    }
}
