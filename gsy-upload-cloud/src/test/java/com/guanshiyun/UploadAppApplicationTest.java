package com.guanshiyun;

import com.aliyun.oss.AliOSSUtils;
import com.guanshiyun.controller.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.StandardOpenOption;

/**
 * Unit test for simple App.
 */
@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
public class UploadAppApplicationTest {
    @Autowired
    private UploadService uploadService;
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private AliOSSUtils aliOSSUtils;
    private final DefaultDataBufferFactory bufferFactory = new DefaultDataBufferFactory();
    @Test
    public void contextLoads() {
        File file = new File("C:\\Users\\G1528\\OneDrive\\Desktop\\龙女妹妹.png");
        Flux<PartEvent> partEventFlux = createFilePartEventFlux(file);

        Flux<DataBuffer> dataBufferFlux = partEventFlux
                .flatMap(part -> {
                    DataBuffer content = part.content();
                    return Mono.just(content);
                });

        String result = aliOSSUtils.uploadReactive("龙女妹妹.png", dataBufferFlux)
                .switchIfEmpty(Mono.just("上传失败：无返回结果"))
                .block(); // ⬅️ 关键：阻塞等待结果

        System.out.println("最终结果: " + result);
    }
    @Test
    public void testUploadFileViaHttp() {
        aliOSSUtils.deleteByObjectKey("d3eb7225-c7fe-47eb-8ec6-99a6dbaf3cc6.png")
                .doOnError(error -> {
                    log.error("删除失败：" + error.getMessage());
                })
                .doOnSuccess(result -> {
                    log.info("删除成功：" + result);
                })
                .block();


    }
    /**
     * 将文件转为 Flux<PartEvent> 流
     */
    private Flux<PartEvent> createFilePartEventFlux(File file) {
        return Flux.create(sink -> {
            System.out.println("开始处理文件: " + file.getAbsolutePath());

            if (!file.exists()) {
                sink.error(new IllegalArgumentException("文件不存在: " + file.getAbsolutePath()));
                return;
            }
            if (!file.canRead()) {
                sink.error(new IllegalArgumentException("文件不可读: " + file.getAbsolutePath()));
                return;
            }

            // 1. 发送 header event
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("file", file.getName());
            headers.setContentType(MediaType.IMAGE_PNG);

            PartEvent headerEvent = new PartEvent() {
                @Override public HttpHeaders headers() { return headers; }
                @Override public DataBuffer content() { return null; }
                @Override public boolean isLast() { return false; }
            };
            sink.next(headerEvent);

            // 2. 异步打开通道
            AsynchronousFileChannel channel;
            try {
                channel = AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.READ);
            } catch (Exception e) {
                sink.error(new RuntimeException("无法打开文件通道", e));
                return;
            }

            int bufferSize = 8192;
            long position = 0;

            // 使用递归读取（异步回调）
            readNextChunk(channel, sink, bufferFactory, file.length(), position, bufferSize);

        }); // Flux.create
    }

    // 递归异步读取
    private void readNextChunk(AsynchronousFileChannel channel,
                               FluxSink<PartEvent> sink,
                               DefaultDataBufferFactory bufferFactory,
                               long fileSize,
                               long position,
                               int bufferSize) {

        if (position >= fileSize) {
            // 文件读取完成，发送结束事件
            PartEvent lastEvent = new PartEvent() {
                @Override public HttpHeaders headers() { return null; }
                @Override public DataBuffer content() { return null; }
                @Override public boolean isLast() { return true; }
            };
            sink.next(lastEvent);
            sink.complete();
            try {
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        channel.read(buffer, position, buffer, new java.nio.channels.CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer read, ByteBuffer attachment) {
                if (read <= 0) {
                    // 读取结束
                    PartEvent lastEvent = new PartEvent() {
                        @Override public HttpHeaders headers() { return null; }
                        @Override public DataBuffer content() { return null; }
                        @Override public boolean isLast() { return true; }
                    };
                    sink.next(lastEvent);
                    sink.complete();
                    try {
                        channel.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

                // 准备发送数据
                attachment.flip();
                DataBuffer dataBuffer = bufferFactory.allocateBuffer(read);
                dataBuffer.write(attachment);

                PartEvent contentEvent = new PartEvent() {
                    @Override public HttpHeaders headers() { return null; }
                    @Override public DataBuffer content() { return dataBuffer; }
                    @Override public boolean isLast() { return false; }
                };
                sink.next(contentEvent);

                // 递归读取下一块
                readNextChunk(channel, sink, bufferFactory, fileSize, position + read, bufferSize);
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    channel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sink.error(new RuntimeException("读取文件失败: " + exc.getMessage(), exc));
            }
        });
    }
}
