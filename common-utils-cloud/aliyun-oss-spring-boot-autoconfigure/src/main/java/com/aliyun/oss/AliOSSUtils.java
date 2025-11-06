package com.aliyun.oss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云 OSS 工具类 - 支持响应式 Flux<DataBuffer> 文件上传
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
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
        String fileName = UUID.randomUUID().toString() + suffix;

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


    @Override
    public String toString() {
        return "AliOSSUtils{aliyunOSSProperties = " + aliyunOSSProperties + "}";
    }
}