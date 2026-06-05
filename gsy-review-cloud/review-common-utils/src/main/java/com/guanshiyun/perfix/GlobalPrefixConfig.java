package com.guanshiyun.perfix;

import com.guanshiyun.reviewenums.ReviewPrefixUrl;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class GlobalPrefixConfig implements WebFluxConfigurer {
//    private static final String API_PREFIX = "/order-api";
    @Override
    public void configurePathMatching(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(ReviewPrefixUrl.PREFIX_URL.getValue(), c -> true);
    }
}
