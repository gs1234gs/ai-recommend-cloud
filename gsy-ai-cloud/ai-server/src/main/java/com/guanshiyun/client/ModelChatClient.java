package com.guanshiyun.client;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.guanshiyun.service.aitool.ProductToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ModelChatClient {
//    private final OllamaChatModel ollamaModel;
    private final ProductToolService productToolService;
    private final DashScopeChatModel dashScopeChatModel;
    /**
     * 创建一个ChatClient实例，并使用OllamaChatModel作为模型
     * @return
     */
    @Bean
    public ChatClient chatClient() {
       return ChatClient
               .builder(dashScopeChatModel)
               .defaultTools(productToolService)
               .build();
    }
}
