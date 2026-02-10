package com.guanshiyun.client;

import com.guanshiyun.service.aitool.ProductToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ModelChatClient {
    private final OllamaChatModel ollamaModel;
    private final ProductToolService productToolService;
    /**
     * 创建一个ChatClient实例，并使用OllamaChatModel作为模型
     * @return
     */
    @Bean
    public ChatClient chatClient() {
       return ChatClient
               .builder(ollamaModel)
               .defaultTools(productToolService)
               .build();
    }
}
