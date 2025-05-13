package com.hello.ai.chataibackend.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AIConfig {
    private final ZhiPuAiChatModel zhiPuAiChatModel;

    public AIConfig(ZhiPuAiChatModel zhiPuAiChatModel) {
        this.zhiPuAiChatModel = zhiPuAiChatModel;
    }

    @Bean
    public Map<String, ChatModel> chatModels() {
        Map<String, ChatModel> models = new HashMap<>();
        
        // ZhiPuAI model
        models.put("glm-4-flash", zhiPuAiChatModel);
        
        return models;
    }
} 