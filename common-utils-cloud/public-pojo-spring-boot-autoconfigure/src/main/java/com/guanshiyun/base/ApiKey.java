package com.guanshiyun.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "api_key")
public class ApiKey {

    @Id
    private Long id;
    private String aliOSSProperties;
    private String aliSMSProperties;
    private String qq;
    private String aiDashscope;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AliOSSProperties {

        /**
         * OSS的访问域名（Endpoint）
         * 例如：https://oss-cn-hangzhou.aliyuncs.com
         */
        private String endpoint;

        /**
         * OSS存储空间（Bucket）的名称
         */
        private String bucketName;

        /**
         * 阿里云访问密钥ID
         */
        private String accessKeyId;

        /**
         * 阿里云访问密钥Secret
         */
        private String accessKeySecret;
        /**
         * 获取
         * @return region
         */
        private String region;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AliSMSProperties {

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
    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QQ{
        private String username;
        private String password;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AiDashscope {
        private String apiKey;
    }
}
