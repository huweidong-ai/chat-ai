package com.hello.ai.chataibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hello.ai.chataibackend.dto.LoginRequest;
import com.hello.ai.chataibackend.dto.PhoneLoginRequest;
import com.hello.ai.chataibackend.dto.RegisterRequest;
import com.hello.ai.chataibackend.dto.VerificationCodeRequest;
import com.hello.ai.chataibackend.entity.User;
import com.hello.ai.chataibackend.mapper.UsersMapper;
import com.hello.ai.chataibackend.security.JwtTokenProvider;
import com.hello.ai.chataibackend.service.AuthService;
import com.hello.ai.chataibackend.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsersMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;

    @Override
    public Object login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(request.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        return response;
    }

    @Override
    @Transactional
    public Object register(RegisterRequest request) {
        if (userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()))) {
            throw new RuntimeException("Username is already taken");
        }

        if (userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getEmail, request.getEmail()))) {
            throw new RuntimeException("Email is already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return response;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public Object sendVerificationCode(VerificationCodeRequest request) {
        if (isInvalidPhoneNumber(request.getPhone())) {
            throw new IllegalArgumentException("无效的手机号码");
        }

        String code = verificationCodeService.generateAndSaveCode(request.getPhone());
        
        System.out.println("发送验证码到手机: " + request.getPhone() + ", 验证码: " + code);
        
        return new HashMap<String, Object>() {{
            put("success", true);
            put("message", "验证码已发送");
        }};
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object loginByPhone(PhoneLoginRequest request) {
        if (isInvalidPhoneNumber(request.getPhone())) {
            throw new IllegalArgumentException("无效的手机号码");
        }

        if (!verificationCodeService.verifyCode(request.getPhone(), request.getCode())) {
            throw new IllegalArgumentException("验证码错误或已过期");
        }

        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getPhone, request.getPhone())
        );

        if (user == null) {
            user = new User();
            user.setPhone(request.getPhone());
            user.setUsername("用户" + request.getPhone().substring(request.getPhone().length() - 4));
            user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + request.getPhone());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(user);
        }
        String token = tokenProvider.generateToken(user.getUsername());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);

        return response;
    }

    private boolean isInvalidPhoneNumber(String phone) {
        return phone == null || !phone.matches("^1[3-9]\\d{9}$");
    }

} 