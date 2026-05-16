package com.guanshiyun.rpc.qq;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.config.UploadWebClientRpc;
import com.guanshiyun.rpc.qqCode.QQCode;
import com.guanshiyun.uploadUrlEnum.QQEnumUrlApi;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class QQEmailApiImpl implements QQEmailApi{
    private final UploadWebClientRpc uploadWebClientRpc;
    @Override
    public Mono<ResultT<Boolean>> sendVerificationCode(QQCode qqCode) {
        return uploadWebClientRpc.webClient()
                .post()
                .uri(QQEnumUrlApi.QQ_EMAIL_CODE_SEND.getValue())
                .bodyValue(qqCode)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultT<Boolean>>() {});
    }
}
