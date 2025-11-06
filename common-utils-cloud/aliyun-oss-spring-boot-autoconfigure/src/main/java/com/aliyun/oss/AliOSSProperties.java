package com.aliyun.oss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 创建配置文件工具类，用于读取并获取阿里云 OSS 相关的配置信息。
 * */



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties(prefix = "aliyun.oss")//实现读取配置文件
public class AliOSSProperties {

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

    public String toString() {
        return "AliOSSProperties{endpoint = " + endpoint + ", bucketName = " + bucketName + ", accessKeyId = " + accessKeyId + ", accessKeySecret = " + accessKeySecret +  "}";
    }
}
