package com.websocks.websocks.config;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

@Component
public class ConnectionInterceptor implements ChannelInterceptor {

    @Autowired
    private Map<String, String> sessionManager;

    private final Integer MAX_CHANNEL_SIZE = 50;
    private final Logger logger  = LoggerFactory.getLogger(ConnectionInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        MessageHeaders headers = message.getHeaders();
        String messageType = Optional.ofNullable(headers.get("simpMessageType")).orElse("").toString();
        String sessionId = Optional.ofNullable(headers.get("simpSessionId")).orElse("").toString();

        if(messageType.equals("CONNECT")) {
            String username = Optional.ofNullable(headers.get("nativeHeaders")).orElse("").toString().split(",")[0];

            if(sessionManager.size() >= MAX_CHANNEL_SIZE) {
                logger.error("NEW CLIENT FAILED TO REGISTER: {} max limit reached!", username);
                throw new ResourceAccessException("?Max users limit reached! 🙁?");
            }

            if(sessionManager.containsValue(username)) {
                logger.error("NEW CLIENT FAILED TO REGISTER: {} already exists!", username);
                throw new ResourceAccessException("?Username already taken! 😅?");
            }
            sessionManager.put(sessionId, username);
            logger.info("NEW CLIENT REGISTERED SUCCESSFULLY: {} with {}", username, sessionId);
        }
        
        if(messageType.equals("DISCONNECT") && sessionManager.containsKey(sessionId)) {
            String username = sessionManager.get(sessionId);
            logger.info("CLIENT DISCONNECTED FROM CHANNEL: {} with {}", sessionId, username);
        }

        return message;
    }    
}
