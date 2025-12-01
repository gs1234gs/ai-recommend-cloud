package com.guanshiyun.goconfig;

import com.guanshiyun.goser.GorseClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableConfigurationProperties(GorseProperties.class)
public class GorseConfig {
    @Bean
    @Scope("prototype")
    public GorseClient gorseClient() {
        GorseProperties gorseProperties = new GorseProperties();

        return GorseClient.builder().url(gorseProperties.getUrl())
                .apiKey(gorseProperties.getApiKey())
                .build();
    }
}
