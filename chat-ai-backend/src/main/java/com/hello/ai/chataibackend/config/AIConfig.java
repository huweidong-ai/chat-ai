package com.hello.ai.chataibackend.config;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AIConfig {
    private final OpenAiChatModel openAiChatModel;
    private final AnthropicChatModel anthropicChatModel;
    private final ZhiPuAiChatModel zhiPuAiChatModel;

    public AIConfig(OpenAiChatModel openAiChatModel, AnthropicChatModel anthropicChatModel, ZhiPuAiChatModel zhiPuAiChatModel) {
        this.openAiChatModel = openAiChatModel;
        this.anthropicChatModel = anthropicChatModel;
        this.zhiPuAiChatModel = zhiPuAiChatModel;
    }

    @Bean
    public Map<String, ChatModel> chatModels() {
        Map<String, ChatModel> models = new HashMap<>();
        
        // OpenAI model
        models.put("gpt-3.5-turbo", openAiChatModel);
        
        // Anthropic model
        models.put("claude-3-opus", anthropicChatModel);
        
        // ZhiPuAI model
        models.put("glm-4", zhiPuAiChatModel);
        
        return models;
    }
} 