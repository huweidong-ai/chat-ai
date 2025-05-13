package com.hello.ai.chataibackend.service.impl;

import com.hello.ai.chataibackend.entity.File;
import com.hello.ai.chataibackend.exception.BusinessException;
import com.hello.ai.chataibackend.repository.FilesRepository;
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

    private final FilesRepository filesRepository;


    @Override
    @Transactional
    public String uploadFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + fileExtension;

        if (fileName.contains("..")) {
            throw new BusinessException("Sorry! Filename contains invalid path sequence " + fileName);
        }

        File fileEntity = new File();
        fileEntity.setUserId(1L); // TODO: 从当前用户获取
        fileEntity.setName(fileName);
        fileEntity.setPath(newFileName);
        fileEntity.setType(file.getContentType());
        fileEntity.setSize(file.getSize());
        fileEntity.setCreatedAt(LocalDateTime.now());
        fileEntity.setUpdatedAt(LocalDateTime.now());

        filesRepository.save(fileEntity);
        return fileEntity.getPath();
    }

    @Override
    public File getFile(Long id) {
        return filesRepository.findById(id).orElse(null);
    }

    @Override
    public List<File> getFiles() {
        return filesRepository.findAll();
    }

    @SneakyThrows
    @Override
    @Transactional
    public void deleteFile(Long id) {
        File file = filesRepository.findById(id).orElse(null);
        Files.deleteIfExists(Path.of(file.getPath()));
        filesRepository.deleteById(id);
    }
} 