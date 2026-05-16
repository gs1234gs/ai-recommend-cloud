package com.aliyun.sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云短信服务配置属性类
 * 对应配置文件中的 aliyun.sms 前缀
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "aliyun.sms")
public class AliSMSProperties {

    /**
     * 云服务访问密钥ID
     */
    private String accessKeyId;

    /**
     * 云服务访问密钥Secret
     */
    private String accessKeySecret;

    /**
     * 短信服务区域ID (RegionId)
     * 例如: cn-hangzhou
     */
    private String region;

    /**
     * 短信签名名称 (必须在阿里云控制台申请并通过审核)
     */
    private String signName;

    /**
     * 短信模板CODE (必须在阿里云控制台申请并通过审核)
     * 例如: SMS_123456789
     */
    private String templateCode;
    /**
     * 访问域名（Endpoint）
     */
    private String endpoint;
}
