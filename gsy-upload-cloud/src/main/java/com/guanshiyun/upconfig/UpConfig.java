package com.guanshiyun.upconfig;

import com.aliyun.oss.AliOSSProperties;
import com.aliyun.sms.AliSMSProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanshiyun.base.ApiKey;
import com.guanshiyun.rpc.apikey.ApiKeyServiceApi;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class UpConfig {

    private final ApiKeyServiceApi apiKeyServiceApi;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Bean
    public MailProperties apiKeyService() {
        ApiKey data = Objects.requireNonNull(apiKeyServiceApi.findApiKeyById().block()).getData();
        String qqStr = data.getQq();
        var qq = objectMapper.readValue(qqStr, new TypeReference<ApiKey.QQ>() {
        });
        MailProperties mailProperties = new MailProperties();
        mailProperties.setHost("smtp.qq.com");
        mailProperties.setPort(587);
        mailProperties.setUsername(qq.getUsername());
        mailProperties.setPassword(qq.getPassword());
        mailProperties.setDefaultEncoding(StandardCharsets.UTF_8);
        return mailProperties;
    }
    @Bean
    public JavaMailSender javaMailSender(MailProperties mailProperties) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(mailProperties.getHost());
        sender.setPort(mailProperties.getPort());
        sender.setUsername(mailProperties.getUsername());
        sender.setPassword(mailProperties.getPassword());
        sender.setDefaultEncoding(mailProperties.getDefaultEncoding().name());

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        return sender;
    }

    @SneakyThrows
    @Bean
    public AliOSSProperties  aliOSSProperties() {
        ApiKey data = Objects.requireNonNull(apiKeyServiceApi.findApiKeyById().block()).getData();
        String qqStr = data.getAliOSSProperties();
        var aliOSSProperties = objectMapper.readValue(qqStr, new TypeReference<ApiKey.AliOSSProperties>() {
        });
        return AliOSSProperties.builder()
                .accessKeyId(aliOSSProperties.getAccessKeyId())
                .accessKeySecret(aliOSSProperties.getAccessKeySecret())
                .bucketName(aliOSSProperties.getBucketName())
                .endpoint(aliOSSProperties.getEndpoint())
                .region(aliOSSProperties.getRegion())
                .build();
    }
    @SneakyThrows
    @Bean
    public AliSMSProperties aliSMSProperties() {
        ApiKey data = Objects.requireNonNull(apiKeyServiceApi.findApiKeyById().block()).getData();
        String qqStr = data.getAliOSSProperties();
        var aliOSSProperties = objectMapper.readValue(qqStr, new TypeReference<ApiKey.AliSMSProperties>() {
        });
        return AliSMSProperties.builder()
                .accessKeyId(aliOSSProperties.getAccessKeyId())
                .accessKeySecret(aliOSSProperties.getAccessKeySecret())
                .signName(aliOSSProperties.getSignName())
                .endpoint(aliOSSProperties.getEndpoint())
                .region(aliOSSProperties.getRegion())
                .templateCode(aliOSSProperties.getTemplateCode())
                .build();
    }
}
