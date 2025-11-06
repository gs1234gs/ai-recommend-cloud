package com.guanshiyun.prefix;

import com.guanshiyun.enums.BehaviorPrefix;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class GlobalPrefixConfig implements WebFluxConfigurer {
    @Override
    public void configurePathMatching(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(BehaviorPrefix.API_PREFIX, c -> true);
    }
}
