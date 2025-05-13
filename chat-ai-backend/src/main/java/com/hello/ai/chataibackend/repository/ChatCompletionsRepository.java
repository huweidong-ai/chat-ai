package com.hello.ai.chataibackend.repository;

import com.hello.ai.chataibackend.entity.ChatCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 聊天完成记录表，存储每次聊天的基本信息(ChatCompletions)表数据库访问层
 *
 * @author makejava
 * @since 2025-04-24 13:50:17
 */
@Repository
public interface ChatCompletionsRepository extends JpaRepository<ChatCompletion, Long> {

}