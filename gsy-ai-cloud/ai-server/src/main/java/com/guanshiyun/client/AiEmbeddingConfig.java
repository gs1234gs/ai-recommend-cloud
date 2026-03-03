package com.guanshiyun.client;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiEmbeddingConfig {
    /**
     * 【关键修改】Bean 名称改为 "localEmbeddingModel" (不能叫 ollamaEmbeddingModel)
     * 这样 Ollama 的自动配置就不会覆盖它了！
     * 同时保留 @Primary，确保 Qdrant 优先使用这个。
     */
    @Bean("localEmbeddingModel") // <--- 改名！避开冲突
    @Primary
    public EmbeddingModel localEmbeddingModel() {
        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl("http://127.0.0.1:11434")
                .build();

        OllamaEmbeddingOptions options = OllamaEmbeddingOptions.builder()
                .model("dengcao/Qwen3-Embedding-4B:Q4_K_M")
                .build();

        return OllamaEmbeddingModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(options)
                .build();
    }

//    @Bean
//    public DashScopeChatModel dashScopeChatModel() {
//        return DashScopeChatModel.builder()
//                .dashScopeApi(DashScopeApi.builder().apiKey(dashScopeApiKey).baseUrl("https://api.dashscope.com").build())
//                .defaultOptions(DashScopeChatOptions.builder()
//                        .model("Qwen3.5-Plus")
//                        .build())
//                .build();
//    }
}
