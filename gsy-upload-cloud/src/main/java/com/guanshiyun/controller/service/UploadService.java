package com.guanshiyun.controller.service;

import com.guanshiyun.responsepojo.ResultT;
import org.springframework.http.codec.multipart.PartEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UploadService {
    Mono<ResultT<List<String>>> uploadFile(Flux<PartEvent> partEventFlux);
    Mono<ResultT< Void>> deleteFile(String url);

    Mono<ResultT<String>> uploadImage(Flux<PartEvent> partEventFlux);
}
