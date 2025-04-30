package com.hello.ai.chataibackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_completions")
public class ChatCompletion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String model;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}