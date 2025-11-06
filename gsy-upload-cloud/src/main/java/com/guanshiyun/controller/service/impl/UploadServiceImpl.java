package com.guanshiyun.upload.impl;

import com.aliyun.oss.AliOSSUtils;
import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.upload.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                            System.out.println("接收文件: " + fileName);
                            return Mono.just(fileName);

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
                          .code(HttpCodeConst.OK)
                          .msg("上传成功")
                          .data(list)
                          .build()
                  );
              })
              .switchIfEmpty( Mono.defer(() -> {
                  log.error("上传失败：未接收到任何文件");
                  return Mono.just(
                          ResultT.<List<String>>builder()
                                  .code(HttpCodeConst.BAD_REQUEST) // 建议用 400，不是 500
                                  .msg("上传失败：未接收到文件")
                                  .data(Collections.emptyList())
                                  .build()
                  );
              }))
              .onErrorResume(throwable ->{
                  log.error("上传失败：", throwable);
                  return Mono.just(
                          ResultT.<List<String>>builder()
                                  .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
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
