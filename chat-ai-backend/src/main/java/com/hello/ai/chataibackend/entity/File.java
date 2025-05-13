package com.hello.ai.chataibackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String name;
    private String path;
    private String type;
    private Long size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}