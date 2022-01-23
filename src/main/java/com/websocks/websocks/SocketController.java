package com.websocks.websocks;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {
    
    @MessageMapping("/message")
    @SendTo("/chat")
    public String getMessage(StompMessage msg) throws Exception {
        return msg.getId() + ": " + msg.getMessage();
    }
}