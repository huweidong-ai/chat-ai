package com.hello.ai.chataibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hello.ai.chataibackend.entity.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件表，存储用户上传的文件信息(Files)表数据库访问层
 *
 * @author makejava
 * @since 2025-04-24 13:50:17
 */
@Mapper
public interface FilesMapper extends BaseMapper<File> {

}

