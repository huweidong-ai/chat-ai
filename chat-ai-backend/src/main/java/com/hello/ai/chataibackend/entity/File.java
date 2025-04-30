package com.hello.ai.chataibackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("files")
public class File {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String path;
    private String type;
    private Long size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}