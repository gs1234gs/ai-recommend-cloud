package com.guanshiyun.controller.service.impl;

import com.aliyun.oss.AliOSSUtils;
import com.guanshiyun.controller.service.UploadService;
import com.guanshiyun.responsepojo.ResultT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePartEvent;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UploadServiceImpl implements UploadService {
    private final AliOSSUtils aliOSSUtils;
    @Override
    public Mono<ResultT<List<String>>> uploadFile(Flux<PartEvent> partEventFlux) {
      return partEventFlux.windowUntil(PartEvent::isLast)
                .concatMap(p->p.switchOnFirst((signal,partEvents)->{
                    if (signal.hasValue()) {
                        PartEvent event = signal.get();
                        if (event instanceof FilePartEvent filePart) {
                            String fileName = filePart.filename();
                            if (fileName.isEmpty()) {
                                return Mono.error(new IllegalArgumentException("文件名不能为空"));
                            }
                            log.info("文件名: {}", fileName);
                            //转化为 Flux<DataBuffer>
                            return aliOSSUtils.uploadReactive(fileName, partEvents.map(PartEvent::content));

                        } else {
                            return Mono.error(new RuntimeException("文件上传失败"));
                        }
                    } else {
                       return Mono.error(new RuntimeException("文件上传失败"));
                    }
                }))
              .collectList()
              .flatMap(list->{
                  log.info("上传成功: {}", list);
                  return Mono.just(
                          ResultT.<List<String>>builder()
                          .code(HttpStatus.OK.value())
                          .msg("上传成功")
                          .data(list)
                          .build()
                  );
              })
              .switchIfEmpty( Mono.defer(() -> {
                  log.error("上传失败：未接收到任何文件");
                  return Mono.just(
                          ResultT.<List<String>>builder()
                                      .code(HttpStatus.BAD_REQUEST.value()) // 建议用 400，不是 500
                                  .msg("上传失败：未接收到文件")
                                  .data(Collections.emptyList())
                                  .build()
                  );
              }))
              .onErrorResume(throwable ->{
                  log.error("上传失败：", throwable);
                  return Mono.just(
                          ResultT.<List<String>>builder()
                                  .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                  .msg("上传失败：" + throwable.getMessage())
                                  .data(Collections.emptyList())
                                  .build()
                  );
              });
    }

    @Override
    public Flux<ResultT<String>> deleteFile(String url) {
        return null;
    }
}
