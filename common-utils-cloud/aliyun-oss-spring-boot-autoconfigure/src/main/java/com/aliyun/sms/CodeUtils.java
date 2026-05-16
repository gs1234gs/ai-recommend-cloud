package com.aliyun.sms;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.ICredentialProvider;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20180501.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20180501.models.BatchSendMessageToGlobeRequest;
import com.aliyun.sdk.service.dysmsapi20180501.models.BatchSendMessageToGlobeResponse;
import darabonba.core.client.ClientOverrideConfiguration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeUtils {

    private AsyncClient asyncClient;
    private AliSMSProperties smsProperties;


    @PostConstruct
    public void init() {
        ICredentialProvider fixedProvider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(smsProperties.getAccessKeyId())
                .accessKeySecret(smsProperties.getAccessKeySecret())
                .build());

        // 修复变量名错误：provider -> fixedProvider
        this.asyncClient = AsyncClient.builder()
                .credentialsProvider(fixedProvider)
                .region(smsProperties.getRegion())
                .overrideConfiguration(ClientOverrideConfiguration.create()
                        .setEndpointOverride(smsProperties.getEndpoint())
                        .setConnectTimeout(Duration.ofSeconds(30)))
                .build();
        log.info("阿里云短信 AsyncClient 初始化成功");
    }

    @PreDestroy
    public void destroy() throws Exception {
        if (this.asyncClient != null) {
            this.asyncClient.close();
            log.info("阿里云短信 AsyncClient 已关闭");
        }
    }
    public Mono<BatchSendMessageToGlobeResponse> sendCode(String number) {
        // 2. 修正请求参数
        BatchSendMessageToGlobeRequest request = BatchSendMessageToGlobeRequest.builder()
                .to(number)
                .from(smsProperties.getSignName())
                .message("您的验证码为：123456，5分钟内有效。")
                .build();

        // 3. 直接使用成员变量的 asyncClient，去掉 try-with-resources
        return Mono.fromFuture(asyncClient.batchSendMessageToGlobe(request))
                .doOnSuccess(resp -> {
                    log.info("阿里云短信发送成功: {}", new com.google.gson.Gson().toJson(resp));
                })
                .doOnError(e -> {
                    log.error("阿里云短信异步调用失败: ", e);
                });
    }
}
