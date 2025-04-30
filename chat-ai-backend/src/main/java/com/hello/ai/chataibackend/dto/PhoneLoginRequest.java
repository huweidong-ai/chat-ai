package com.hello.ai.chataibackend.dto;

import lombok.Data;
 
@Data
public class PhoneLoginRequest {
    private String phone;
    private String code;
} 