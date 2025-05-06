package com.hello.ai.chataibackend.controller;

import com.hello.ai.chataibackend.common.ApiResponse;
import com.hello.ai.chataibackend.entity.File;
import com.hello.ai.chataibackend.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileService.uploadFile(file);
            return ApiResponse.success(fileUrl);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<File> getFile(@PathVariable Long id) {
        return ApiResponse.success(fileService.getFile(id));
    }

    @GetMapping
    public ApiResponse<List<File>> getFiles() {
        return ApiResponse.success(fileService.getFiles());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFile(@PathVariable Long id) {
        try {
            fileService.deleteFile(id);
            return ApiResponse.success("文件删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
} 