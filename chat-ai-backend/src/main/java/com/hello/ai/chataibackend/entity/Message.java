package com.hello.ai.chataibackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatCompletionId;
    private String role;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime createdAt;
}