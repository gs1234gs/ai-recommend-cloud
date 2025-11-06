package com.guanshiyun.client;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ModelChatClient {
    private final OllamaChatModel ollamaModel;
    @Bean
    public ChatClient chatClient() {
       return ChatClient.builder(ollamaModel).build();
    }

}
