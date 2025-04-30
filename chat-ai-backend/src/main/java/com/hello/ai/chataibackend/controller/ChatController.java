package com.hello.ai.chataibackend.controller;

import com.hello.ai.chataibackend.dto.ChatCompletionRequest;
import com.hello.ai.chataibackend.entity.ChatCompletion;
import com.hello.ai.chataibackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping(value = "/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> createChatCompletion(@RequestBody ChatCompletionRequest request) {
        return chatService.createChatCompletion(request);
    }

    @GetMapping("/completions/{id}")
    public ResponseEntity<ChatCompletion> getChatCompletion(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.getChatCompletion(id));
    }

    @GetMapping("/completions")
    public ResponseEntity<List<ChatCompletion>> getChatCompletions() {
        return ResponseEntity.ok(chatService.getChatCompletions());
    }

    @DeleteMapping("/completions/{id}")
    public ResponseEntity<Void> deleteChatCompletion(@PathVariable Long id) {
        chatService.deleteChatCompletion(id);
        return ResponseEntity.ok().build();
    }
} 