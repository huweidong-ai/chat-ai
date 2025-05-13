package com.hello.ai.chataibackend.repository;

import com.hello.ai.chataibackend.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 文件表，存储用户上传的文件信息(Files)表数据库访问层
 *
 * @author makejava
 * @since 2025-04-24 13:50:17
 */
@Repository
public interface FilesRepository extends JpaRepository<File, Long> {

}