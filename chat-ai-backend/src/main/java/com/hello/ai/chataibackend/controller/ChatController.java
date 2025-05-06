package com.hello.ai.chataibackend.controller;

import com.hello.ai.chataibackend.common.ApiResponse;
import com.hello.ai.chataibackend.dto.ChatCompletionRequest;
import com.hello.ai.chataibackend.entity.ChatCompletion;
import com.hello.ai.chataibackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    public ApiResponse<ChatCompletion> getChatCompletion(@PathVariable Long id) {
        return ApiResponse.success(chatService.getChatCompletion(id));
    }

    @GetMapping("/completions")
    public ApiResponse<List<ChatCompletion>> getChatCompletions() {
        return ApiResponse.success(chatService.getChatCompletions());
    }

    @DeleteMapping("/completions/{id}")
    public ApiResponse<Void> deleteChatCompletion(@PathVariable Long id) {
        chatService.deleteChatCompletion(id);
        return ApiResponse.success(null);
    }
} 