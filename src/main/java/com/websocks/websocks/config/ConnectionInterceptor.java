package com.websocks.websocks.config;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConnectionInterceptor implements ChannelInterceptor {

    private static final Integer MAX_CHANNEL_SIZE = 10;
    private static final String USER_HEADER = "client-id";

    @Autowired
    private Map<String, String> sessionManager;

    /* before accepting connections (preSend), validate session */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = Optional.ofNullable(accessor.getHeader(StompHeaderAccessor.SESSION_ID_HEADER)).orElse("").toString();

        if(StompCommand.CONNECT.equals(accessor.getCommand())) {
            String username = Optional.ofNullable(accessor.getNativeHeader(USER_HEADER)).orElse(Collections.emptyList()).get(0);

            if(sessionManager.size() > MAX_CHANNEL_SIZE) {
                log.error("NEW CLIENT FAILED TO REGISTER: {} max limit reached!", username);
                throw new ResourceAccessException("?Max users limit reached! 🙁?");
            }

            if(sessionManager.containsValue(username)) {
                log.error("NEW CLIENT FAILED TO REGISTER: {} already exists!", username);
                throw new ResourceAccessException("?Username already taken! 😅?");
            }
            sessionManager.put(sessionId, username);
            log.info("NEW CLIENT REGISTERED SUCCESSFULLY: {} with {}", username, sessionId);
        }
        
        if(StompCommand.DISCONNECT.equals(accessor.getCommand()) && sessionManager.containsKey(sessionId)) {
            String username = sessionManager.get(sessionId);
            log.info("CLIENT DISCONNECTED FROM CHANNEL: {} with {}", username, sessionId);
        }

        return message;
    }    
}
