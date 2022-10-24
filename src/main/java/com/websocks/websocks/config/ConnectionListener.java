package com.websocks.websocks.config;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.websocks.websocks.model.StompMessage;

public class ConnectionListener {
    private SimpMessagingTemplate template;
    
    @Autowired
    private Map<String, String> sessionManager;

    public ConnectionListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    @EventListener
    public void onConnect(SessionConnectedEvent event) {
        String sessionId = Optional.ofNullable(event.getMessage().getHeaders().get("simpSessionId")).orElse("").toString();
        String clientId = sessionManager.get(sessionId);
        String username = clientId.substring(clientId.indexOf("[")+1, clientId.indexOf("]"));
        template.convertAndSend("/connections", new StompMessage(null, "New user - " + username + " has joined the chat! 🙌", sessionManager.size(), null));
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        if(sessionId != null) {
            template.convertAndSend("/exits", new StompMessage(null, sessionManager.get(sessionId).split("=")[1] + " has left the chat 👋", sessionManager.size(), null));
            sessionManager.remove(sessionId);
        }
    }

    @Scheduled(fixedRate = 5000, initialDelay = 1000)
    public void getLiveConnections() {
        template.convertAndSend("/liveUsers", new StompMessage(null, null, sessionManager.size(), new ArrayList<>(sessionManager.values())));
    }
}
