package com.websocks.websocks;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public class ConnectionListener {
    private SimpMessagingTemplate template;
    
    @Autowired
    private Map<String, String> sessionManager;

    public ConnectionListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    @EventListener
    public void onConnect(SessionConnectedEvent event) {
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        String clientId = sessionManager.get(sessionId);
        String username = clientId.substring(clientId.indexOf("[")+1, clientId.indexOf("]"));
        template.convertAndSend("/connections", "New user - " + username + " has joined the chat! 🙌");
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        if(sessionId != null) {
            template.convertAndSend("/exits", sessionManager.get(sessionId).split("=")[1] + " has left the chat 👋");
            sessionManager.remove(sessionId);
        }
    }
}
