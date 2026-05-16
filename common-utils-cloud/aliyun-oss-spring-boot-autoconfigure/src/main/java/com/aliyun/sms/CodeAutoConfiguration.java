package com.aliyun.sms;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AliSMSProperties.class)
public class CodeAutoConfiguration {
    @Bean
    public CodeUtils codeUtils (AliSMSProperties aliSMSProperties) {
        CodeUtils codeUtils = new CodeUtils();
        codeUtils.setSmsProperties(aliSMSProperties);
        return codeUtils;
    }

}
