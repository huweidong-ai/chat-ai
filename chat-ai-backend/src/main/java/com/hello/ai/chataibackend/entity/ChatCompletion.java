package com.hello.ai.chataibackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_completions")
public class ChatCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String title;
    private String model;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}