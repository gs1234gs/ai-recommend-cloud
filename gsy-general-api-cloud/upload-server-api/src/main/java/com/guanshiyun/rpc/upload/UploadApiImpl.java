package com.guanshiyun.rpc.upload;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.config.UploadWebClientRpc;
import com.guanshiyun.uploadUrlEnum.UploadEnumUrlApi;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UploadApiImpl implements UploadApi{
    private final UploadWebClientRpc uploadWebClientRpc;

    @Override
    public Mono<ResultT<List<String>>> uploadImage(Flux<PartEvent> partEventFlux) {
        return uploadWebClientRpc.webClient()
                .post()
                .uri(UploadEnumUrlApi.UPLOAD_IMAGE_URL.getValue())
                .bodyValue(partEventFlux)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<List<String>>>() {});
    }

    @Override
    public Mono<ResultT<String>> uploadFile(Flux<PartEvent> partEventFlux) {
        return uploadWebClientRpc.webClient()
                .post()
                .uri(UploadEnumUrlApi.UPLOAD_URL.getValue())
                .bodyValue(partEventFlux)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<String>>() {});
    }
}
