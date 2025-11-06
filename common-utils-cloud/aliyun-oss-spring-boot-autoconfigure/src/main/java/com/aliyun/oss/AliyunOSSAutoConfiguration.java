package com.aliyun.oss;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * 实现自动配置
 * */
@Configuration
@EnableConfigurationProperties(AliOSSProperties.class)
public class AliyunOSSAutoConfiguration {

    @Bean
    public AliOSSUtils aliOssUtils(AliOSSProperties aliOSSProperties) {
        AliOSSUtils aliOSSUtils = new AliOSSUtils();
        aliOSSUtils.setAliyunOSSProperties(aliOSSProperties);
        return aliOSSUtils;
    }
}
