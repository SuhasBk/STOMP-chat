package com.websocks.websocks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@EnableScheduling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    ConnectionInterceptor connectionInterceptor;

    /* root endpoint mapping for websocket communication */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocks")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /* 'chat' and 'chatuploads' endpoints are used by clients. remaining endpoints are used by server to update the client.*/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/chat", "/chatuploads", "/connections", "/exits", "/liveUsers");
    }

    /* configure interceptor to handle user sessions */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(connectionInterceptor);
    }

    /* increase payload limit of STOMP messages*/
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(1024*1024);
    }
}
