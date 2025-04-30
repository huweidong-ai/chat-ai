package com.hello.ai.chataibackend.service;

import com.hello.ai.chataibackend.entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    File uploadFile(MultipartFile file);
    File getFile(Long id);
    List<File> getFiles();
    void deleteFile(Long id);
} 