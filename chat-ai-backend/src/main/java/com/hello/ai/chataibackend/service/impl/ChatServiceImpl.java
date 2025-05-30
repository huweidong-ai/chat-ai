package com.hello.ai.chataibackend.service.impl;

import com.hello.ai.chataibackend.dto.ChatCompletionRequest;
import com.hello.ai.chataibackend.entity.ChatCompletion;
import com.hello.ai.chataibackend.entity.Message;
import com.hello.ai.chataibackend.exception.BusinessException;
import com.hello.ai.chataibackend.repository.ChatCompletionsRepository;
import com.hello.ai.chataibackend.repository.MessagesRepository;
import com.hello.ai.chataibackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final Map<String, ChatModel> chatModels;
    private final MessagesRepository messagesRepository;
    private final ChatCompletionsRepository chatCompletionsRepository;

    /**
     * 创建新的聊天会话
     *
     * @param request 聊天请求参数
     * @return 聊天响应流
     */
    @Override
    @Transactional
    public Flux<ChatResponse> createChatCompletion(ChatCompletionRequest request) {
        // 创建新的聊天会话
        ChatCompletion chatCompletion = new ChatCompletion();
        chatCompletion.setUserId(1L); // TODO: 从当前用户获取
        chatCompletion.setTitle(request.getMessages().getFirst().getContent());
        chatCompletion.setModel(request.getModel());
        chatCompletion.setCreatedAt(LocalDateTime.now());
        chatCompletion.setUpdatedAt(LocalDateTime.now());
        chatCompletionsRepository.save(chatCompletion);

        // 保存用户消息
        Message userMessage = new Message();
        userMessage.setChatCompletionId(chatCompletion.getId());
        userMessage.setRole("user");
        userMessage.setContent(request.getMessages().getFirst().getContent());
        userMessage.setCreatedAt(LocalDateTime.now());
        messagesRepository.save(userMessage);

        // 获取对应的 AI 模型
        ChatModel chatModel = chatModels.get(request.getModel());
        if (chatModel == null) {
            throw new BusinessException("Unsupported model: " + request.getModel());
        }

        // 使用 StringBuilder 来收集完整的响应
        StringBuilder fullResponse = new StringBuilder();

        // 调用 AI 服务并返回流式响应
        return chatModel.stream(new Prompt(request.getMessages().getFirst().getContent()))
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(response -> {
                    // 立即将每个响应块添加到完整响应中
                    String content = response.getResult().getOutput().getText();
                    fullResponse.append(content);
                    log.debug("Streaming response chunk: {}", content);
                })
                .doOnComplete(() -> {
                    // 在流完成时保存完整的 AI 消息到数据库
                    log.info("Stream completed, saving full response");
                    Message aiMessage = new Message();
                    aiMessage.setChatCompletionId(chatCompletion.getId());
                    aiMessage.setRole("assistant");
                    aiMessage.setContent(fullResponse.toString());
                    aiMessage.setCreatedAt(LocalDateTime.now());
                    messagesRepository.save(aiMessage);
                });
    }

    /**
     * 获取指定ID的聊天会话
     *
     * @param id 聊天会话ID
     * @return 聊天会话信息
     */
    @Override
    public ChatCompletion getChatCompletion(Long id) {
        return chatCompletionsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chat completion not found"));
    }

    /**
     * 获取所有聊天会话列表
     *
     * @return 聊天会话列表
     */
    @Override
    public List<ChatCompletion> getChatCompletions() {
        return chatCompletionsRepository.findAll();
    }

    /**
     * 删除指定ID的聊天会话及其相关消息
     *
     * @param id 聊天会话ID
     */
    @Override
    @Transactional
    public void deleteChatCompletion(Long id) {
        // 删除相关消息
        messagesRepository.deleteAllByChatCompletionId(id);
        // 删除聊天会话
        chatCompletionsRepository.deleteById(id);
    }
}