package com.guanshiyun.goconfig;

import com.guanshiyun.goser.GorseClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
@EnableConfigurationProperties(GorseProperties.class)
public class GorseConfig {
    @Bean
    @Primary
    public GorseClient gorseClient(GorseProperties gorseProperties) {
        String rawUrl = gorseProperties.getUrl();

        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            log.warn("Gorse 未配置，使用空客户端");
            return new GorseClient(); // 实现所有方法为空操作
        }

        // 自动补全协议（如果缺失）
        String normalizedUrl = normalizeUrl(rawUrl);

        return GorseClient.builder()
                .url(normalizedUrl)
                .apiKey(gorseProperties.getApiKey())
                .build();
    }

    /**
     * 确保 URL 以 http:// 或 https:// 开头。
     * 如果没有协议，默认使用 http://
     */
    private String normalizeUrl(String url) {
        String trimmed = url.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        // 默认使用 http（可根据需求改为 https）
        return "http://" + trimmed;
    }
}
