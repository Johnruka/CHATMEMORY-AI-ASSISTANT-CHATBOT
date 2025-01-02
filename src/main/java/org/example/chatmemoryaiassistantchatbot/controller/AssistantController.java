package org.example.chatmemoryaiassistantchatbot.controller;

import org.example.chatmemoryaiassistantchatbot.service.AssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AssistantController {

    AssistantService service;

    @Autowired
    public AssistantController(AssistantService service) {
        this.service = service;
    }

    @GetMapping("/memory/chat")
    public ResponseEntity<String> chatMemory(@RequestParam String chatId, @RequestParam String question) {
        // todo: add validation
        return ResponseEntity.ok(service.chatMemory(chatId, question));
    }
}
