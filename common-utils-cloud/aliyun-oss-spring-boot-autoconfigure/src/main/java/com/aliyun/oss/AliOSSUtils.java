package com.aliyun.oss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云 OSS 工具类 - 支持响应式 Flux<DataBuffer> 文件上传
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AliOSSUtils {

    private AliOSSProperties aliyunOSSProperties;

    /**
     * 通过响应式流上传文件到 OSS，返回访问 URL
     * @param originalFilename 原始文件名，用于生成 OSS 上的文件名和后缀
     * @param dataBufferFlux Flux<DataBuffer> 文件内容流
     * @return Mono<String> 文件访问URL
     */
    public Mono<String> uploadReactive(String originalFilename, Flux<DataBuffer> dataBufferFlux) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return Mono.error(new IllegalArgumentException("文件名不能为空"));
        }

        // 生成唯一文件名，避免覆盖
        String suffix = "";
        int idx = originalFilename.lastIndexOf(".");
        if (idx >= 0) {
            suffix = originalFilename.substring(idx);
        }
        String fileName = UUID.randomUUID() + suffix;

        String endpoint = aliyunOSSProperties.getEndpoint();
        String accessKeyId = aliyunOSSProperties.getAccessKeyId();
        String accessKeySecret = aliyunOSSProperties.getAccessKeySecret();
        String bucketName = aliyunOSSProperties.getBucketName();

        // 把 Flux<DataBuffer> 拼成一个完整的 DataBuffer，转换为字节数组
        return DataBufferUtils.join(dataBufferFlux)
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
                        ossClient.putObject(bucketName, fileName, inputStream);
                        ossClient.shutdown();

                        // 构造访问URL，endpoint去除https://或http://，根据你的endpoint格式调整
                        String cleanEndpoint = endpoint.replaceFirst("^https?://", "");
                        String url = "https://" + bucketName + "." + cleanEndpoint + "/" + fileName;
                        System.out.println("文件上传到 OSS 的文件路径：" + url);

                        return Mono.just(url);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("文件上传到 OSS 失败", e));
                    }
                });
    }

    /**
     * 根据 OSS 上的 Object Key（文件路径）删除文件
     * @param objectKey 文件在 OSS 中的路径，如 images/2025/04/15/abc123.jpg
     * @return Mono<Void> 删除成功或失败
     */
    public Mono<Void> deleteByObjectKey(String objectKey) {
        // 参数校验
        if (objectKey == null || objectKey.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Object Key 不能为空"));
        }
        if (aliyunOSSProperties == null) {
            return Mono.error(new IllegalStateException("AliOSSProperties 未配置"));
        }

        String endpoint = aliyunOSSProperties.getEndpoint();
        String accessKeyId = aliyunOSSProperties.getAccessKeyId();
        String accessKeySecret = aliyunOSSProperties.getAccessKeySecret();
        String bucketName = aliyunOSSProperties.getBucketName();

        //使用 fromCallable + subscribeOn
        return Mono.fromCallable(() -> {
                    OSS ossClient = null;
                    try {
                        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
                        ossClient.deleteObject(bucketName, objectKey);
                        log.info("OSS 文件删除成功: bucket={}, objectKey={}", bucketName, objectKey);
                        return true;
                    } catch (Exception e) {
                        log.error("删除 OSS 文件失败: bucket={}, objectKey={}", bucketName, objectKey, e);
                        throw new RuntimeException("删除 OSS 文件失败: " + objectKey, e);
                    } finally {
                        if (ossClient != null) {
                            ossClient.shutdown();
                        }
                    }
                })
                .subscribeOn(Schedulers.boundedElastic()) // ✅ 关键：切换到非事件循环线程
                .then(); // 转为 Mono<Void>
    }
    public void deleteByObjectKeys(String objectKey) {



        String endpoint = aliyunOSSProperties.getEndpoint();
        String accessKeyId = aliyunOSSProperties.getAccessKeyId();
        String accessKeySecret = aliyunOSSProperties.getAccessKeySecret();
        String bucketName = aliyunOSSProperties.getBucketName();


                    OSS ossClient = null;
                    try {
                        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
                        ossClient.deleteObject(bucketName, objectKey);
                        log.info("OSS 文件删除成功: bucket={}, objectKey={}", bucketName, objectKey);
                    } catch (Exception e) {
                        log.error("删除 OSS 文件失败: bucket={}, objectKey={}", bucketName, objectKey, e);
                        throw new RuntimeException("删除 OSS 文件失败: " + objectKey, e);
                    } finally {
                        if (ossClient != null) {
                            ossClient.shutdown();
                        }
                    }
    }

    /**
     * 根据 OSS 文件的访问 URL 删除文件
     * @param fileUrl 文件的完整访问 URL，如 <a href="https://bucket.oss-cn-beijing.aliyuncs.com/images/abc.jpg">...</a>
     * @return Mono<Void> 删除成功或失败
     */
    public Mono<Void> deleteByUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("文件 URL 不能为空"));
        }

        try {
            java.net.URI uri = java.net.URI.create(fileUrl); // 安全解析
            java.net.URL url = uri.toURL(); // 转换为 URL
            String host = url.getHost(); // bucket.oss-cn-beijing.aliyuncs.com
            String path = url.getPath();  // /images/abc.jpg

            // 提取 bucketName（假设配置中的 bucketName 是正确的）
            String bucketName = aliyunOSSProperties.getBucketName();

            // 验证 host 是否匹配（可选）
            if (!host.startsWith(bucketName + ".")) {
                log.warn("URL 中的 bucket 可能不匹配配置: expected={}, actual={}", bucketName, host.split("\\.")[0]);
            }

            // 处理路径：去除开头的 '/'
            String objectKey = path.startsWith("/") ? path.substring(1) : path;
            if (objectKey.isEmpty()) {
                return Mono.error(new IllegalArgumentException("URL 中未包含有效的文件路径"));
            }

            // 调用 deleteByObjectKey
            return deleteByObjectKey(objectKey);
        } catch (Exception e) {
            return Mono.error(new IllegalArgumentException("解析文件 URL 失败: " + fileUrl, e));
        }
    }
}