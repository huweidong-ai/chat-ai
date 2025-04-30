package com.hello.ai.chataibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hello.ai.chataibackend.entity.ChatCompletion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天完成记录表，存储每次聊天的基本信息(ChatCompletions)表数据库访问层
 *
 * @author makejava
 * @since 2025-04-24 13:50:17
 */
@Mapper
public interface ChatCompletionsMapper extends BaseMapper<ChatCompletion> {

}

