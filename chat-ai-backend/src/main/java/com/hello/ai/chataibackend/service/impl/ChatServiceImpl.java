package com.hello.ai.chataibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hello.ai.chataibackend.dto.ChatCompletionRequest;
import com.hello.ai.chataibackend.entity.ChatCompletion;
import com.hello.ai.chataibackend.entity.Message;
import com.hello.ai.chataibackend.exception.BusinessException;
import com.hello.ai.chataibackend.mapper.ChatCompletionsMapper;
import com.hello.ai.chataibackend.mapper.MessagesMapper;
import com.hello.ai.chataibackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl extends ServiceImpl<ChatCompletionsMapper, ChatCompletion> implements ChatService {

    private final Map<String, ChatModel> chatModels;
    private final MessagesMapper messagesMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Flux<String> createChatCompletion(ChatCompletionRequest request) {
        // 创建新的聊天会话
        ChatCompletion chatCompletion = new ChatCompletion();
        chatCompletion.setUserId(1L); // TODO: 从当前用户获取
        chatCompletion.setTitle(request.getMessages().getFirst().getContent());
        chatCompletion.setModel(request.getModel());
        chatCompletion.setCreatedAt(LocalDateTime.now());
        chatCompletion.setUpdatedAt(LocalDateTime.now());
        save(chatCompletion);

        // 保存用户消息
        Message userMessage = new Message();
        userMessage.setChatCompletionId(chatCompletion.getId());
        userMessage.setRole("user");
        userMessage.setContent(request.getMessages().getFirst().getContent());
        userMessage.setCreatedAt(LocalDateTime.now());
        messagesMapper.insert(userMessage);

        // 获取对应的 AI 模型
        ChatModel chatModel = chatModels.get(request.getModel());
        if (chatModel == null) {
            throw new BusinessException("Unsupported model: " + request.getModel());
        }

        // 构建系统提示
        String systemPrompt = "You are a helpful AI assistant. Please provide clear and concise responses.";
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPrompt);

        // 调用 AI 服务并返回流式响应
        return chatModel.stream(new Prompt(request.getMessages().getFirst().getContent()))
                .map(response -> {
                    // 如果是最后一个响应，保存完整的 AI 消息
                    if (response.getResult().getOutput() != null) {
                        Message aiMessage = new Message();
                        aiMessage.setChatCompletionId(chatCompletion.getId());
                        aiMessage.setRole("assistant");
                        aiMessage.setContent(response.getResult().getOutput().toString());
                        aiMessage.setCreatedAt(LocalDateTime.now());
                        messagesMapper.insert(aiMessage);
                    }
                    return response.getResult().getOutput().toString();
                });
    }

    @Override
    public ChatCompletion getChatCompletion(Long id) {
        return getById(id);
    }

    @Override
    public List<ChatCompletion> getChatCompletions() {
        return list(new LambdaQueryWrapper<ChatCompletion>()
                .orderByDesc(ChatCompletion::getCreatedAt));
    }

    @Override
    @Transactional
    public void deleteChatCompletion(Long id) {
        // 删除相关消息
        messagesMapper.delete(new LambdaQueryWrapper<Message>()
                .eq(Message::getChatCompletionId, id));
        // 删除聊天会话
        removeById(id);
    }
} 