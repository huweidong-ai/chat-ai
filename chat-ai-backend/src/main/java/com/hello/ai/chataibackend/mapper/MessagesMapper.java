package com.hello.ai.chataibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hello.ai.chataibackend.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息表，存储每条消息的内容(Messages)表数据库访问层
 *
 * @author makejava
 * @since 2025-04-24 13:50:17
 */
@Mapper
public interface MessagesMapper extends BaseMapper<Message> {

}

