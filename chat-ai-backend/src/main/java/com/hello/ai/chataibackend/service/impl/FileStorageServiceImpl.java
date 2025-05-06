package com.hello.ai.chataibackend.service.impl;

import com.hello.ai.chataibackend.entity.File;
import com.hello.ai.chataibackend.mapper.FilesMapper;
import com.hello.ai.chataibackend.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileService {

    private final FilesMapper filesMapper;


    @Override
    @Transactional
    public String uploadFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + fileExtension;

        if (fileName.contains("..")) {
            throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
        }

        File fileEntity = new File();
        fileEntity.setUserId(1L); // TODO: 从当前用户获取
        fileEntity.setName(fileName);
        fileEntity.setPath(newFileName);
        fileEntity.setType(file.getContentType());
        fileEntity.setSize(file.getSize());
        fileEntity.setCreatedAt(LocalDateTime.now());
        fileEntity.setUpdatedAt(LocalDateTime.now());

        filesMapper.insert(fileEntity);
        return fileEntity.getPath();
    }

    @Override
    public File getFile(Long id) {
        return filesMapper.selectById(id);
    }

    @Override
    public List<File> getFiles() {
        return filesMapper.selectList(null);
    }

    @SneakyThrows
    @Override
    @Transactional
    public void deleteFile(Long id) {
        File file = filesMapper.selectById(id);
        Files.deleteIfExists(Path.of(file.getPath()));
        filesMapper.deleteById(id);
    }
} 