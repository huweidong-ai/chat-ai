package com.hello.ai.chataibackend.controller;

import com.hello.ai.chataibackend.dto.LoginRequest;
import com.hello.ai.chataibackend.dto.PhoneLoginRequest;
import com.hello.ai.chataibackend.dto.RegisterRequest;
import com.hello.ai.chataibackend.dto.VerificationCodeRequest;
import com.hello.ai.chataibackend.entity.User;
import com.hello.ai.chataibackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PostMapping("/verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody VerificationCodeRequest request) {
        return ResponseEntity.ok(authService.sendVerificationCode(request));
    }

    @PostMapping("/login/phone")
    public ResponseEntity<?> phoneLogin(@RequestBody PhoneLoginRequest request) {
        return ResponseEntity.ok(authService.loginByPhone(request));
    }
} 