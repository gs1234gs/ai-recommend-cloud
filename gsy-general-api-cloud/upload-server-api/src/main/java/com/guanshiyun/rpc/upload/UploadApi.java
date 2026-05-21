package com.guanshiyun.rpc.upload;

import com.guanshiyun.responsepojo.ResultT;
import org.springframework.http.codec.multipart.PartEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UploadApi {

    Mono<ResultT<List<String>>> uploadImage(Flux<PartEvent> partEventFlux);
    Mono<ResultT<String>> uploadFile(Flux<PartEvent> partEventFlux);
}
