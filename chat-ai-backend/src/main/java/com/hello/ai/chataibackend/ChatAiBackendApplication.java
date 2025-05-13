package com.hello.ai.chataibackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.hello.ai.chataibackend"})
public class ChatAiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatAiBackendApplication.class, args);
    }

}
