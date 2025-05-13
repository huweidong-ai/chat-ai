package com.hello.ai.chataibackend.service;

import com.hello.ai.chataibackend.dto.ChatCompletionRequest;
import com.hello.ai.chataibackend.entity.ChatCompletion;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    Flux<ChatResponse> createChatCompletion(ChatCompletionRequest request);
    ChatCompletion getChatCompletion(Long id);
    List<ChatCompletion> getChatCompletions();
    void deleteChatCompletion(Long id);
} 