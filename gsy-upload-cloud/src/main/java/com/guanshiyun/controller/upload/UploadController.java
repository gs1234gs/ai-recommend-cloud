package com.guanshiyun.controller.upload;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.upload.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/upload/")
public class UploadController {
    private final UploadService uploadService;
    //上传单个文件
    @PostMapping("/image")
    public Mono<ResultT<List<String>>> uploadImage(@RequestBody Flux<PartEvent> partEventFlux) {
        return uploadService.uploadFile(partEventFlux);
    }
    @PostMapping("/upload")
    public Mono<ResultT<String>> uploadFile(@RequestBody Flux<PartEvent> partEventFlux) {
        return uploadService.uploadImage(partEventFlux);
    }

}
