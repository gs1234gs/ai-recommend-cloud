package com.guanshiyun.prefix;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class GlobalPrefixConfig implements WebFluxConfigurer {
    private static final String API_PREFIX = "/upload-api";
    @Override
    public void configurePathMatching(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PREFIX, c -> true);
    }
}
