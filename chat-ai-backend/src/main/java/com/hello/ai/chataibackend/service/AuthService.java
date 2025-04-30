package com.hello.ai.chataibackend.service;

import com.hello.ai.chataibackend.dto.LoginRequest;
import com.hello.ai.chataibackend.dto.RegisterRequest;
import com.hello.ai.chataibackend.entity.User;

public interface AuthService {
    Object login(LoginRequest request);
    Object register(RegisterRequest request);
    User getCurrentUser();
}