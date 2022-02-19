package com.websocks.websocks.controller;

import com.websocks.websocks.model.StompMessage;

import com.websocks.websocks.services.FileService;
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

import java.util.HashMap;
import java.util.Map;

@Controller
public class SocketController {

    @Autowired
    FileService fileService;
    
    @MessageMapping("/message")
    @SendTo("/chat")
    public String getMessage(StompMessage msg) throws Exception {
        return msg.getId() + ": " + msg.getMessage();
    }

    @MessageMapping("/fileMessage")
    @SendTo("/chatuploads")
    public Map<String, String> getFileMessage(StompMessage msg) throws Exception {
        Map<String, String> response = new HashMap<>();
        response.put("userId", msg.getId());
        response.put("filename", msg.getFilename());
        return response;
    }

    @PostMapping(path="/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        System.out.println("file name is: "+file.getOriginalFilename());
        fileService.save(file);
        return "redirect:/";
    }

    @GetMapping(path="/files/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = fileService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
