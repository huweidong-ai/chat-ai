package com.hello.ai.chataibackend.controller;

import com.hello.ai.chataibackend.common.ApiResponse;
import com.hello.ai.chataibackend.dto.PhoneLoginRequest;
import com.hello.ai.chataibackend.dto.VerificationCodeRequest;
import com.hello.ai.chataibackend.entity.User;
import com.hello.ai.chataibackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser() {
        return ApiResponse.success(authService.getCurrentUser());
    }

    @PostMapping("/verification-code")
    public ApiResponse<?> sendVerificationCode(@RequestBody VerificationCodeRequest request) {
        return ApiResponse.success(authService.sendVerificationCode(request));
    }

    @PostMapping("/login/phone")
    public ApiResponse<?> phoneLogin(@RequestBody PhoneLoginRequest request) {
        return ApiResponse.success(authService.loginByPhone(request));
    }
} 