package com.hello.ai.chataibackend.repository;

import com.hello.ai.chataibackend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 消息表，存储每条消息的内容(Messages)表数据库访问层
 *
 * @author makejava
 * @since 2025-04-24 13:50:17
 */
@Repository
public interface MessagesRepository extends JpaRepository<Message, Long> {
    /**
     * 根据聊天会话ID删除所有相关消息
     *
     * @param chatCompletionId 聊天会话ID
     */
    void deleteAllByChatCompletionId(Long chatCompletionId);
}