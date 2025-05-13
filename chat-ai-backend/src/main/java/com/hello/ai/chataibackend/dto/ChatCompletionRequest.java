package com.hello.ai.chataibackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionRequest {
    private String model;
    private List<ChatMessage> messages;
    private boolean stream;

    @Data
    @AllArgsConstructor
    public static class ChatMessage {
        private String role;
        private String content;
    }
} 