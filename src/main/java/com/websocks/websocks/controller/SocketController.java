package com.websocks.websocks.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.websocks.websocks.model.StompMessagePayload;
import com.websocks.websocks.services.FileService;

@Controller
public class SocketController {

    @Autowired
    FileService fileService;
    
    @MessageMapping("/message")
    @SendTo("/chat")
    public Map<String, Object> getMessage(StompMessagePayload msg) throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("message", msg.getId() + ": " + msg.getMessage());
        return response;
    }

    @MessageMapping("/fileMessage")
    @SendTo("/chatuploads")
    public Map<String, String> getFileMessage(StompMessagePayload msg) throws Exception {
        Map<String, String> response = new HashMap<>();
        response.put("userId", msg.getId());
        response.put("filename", msg.getFilename());
        return response;
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        fileService.save(file);
        return "redirect:/";
    }

    @GetMapping("/files/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = fileService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
