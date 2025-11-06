package com.guanshiyun.service.model;

import com.guanshiyun.bigmodel.BigModel;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface BigModelService {
    //添加大模型
    Mono<BigInteger> sava(BigModel bigModel);

    Mono< BigInteger> deleteById(BigInteger id);

    Mono<BigModel> findById(BigInteger id);

    Mono<PageResultT<List<BigModel>>> findPage(RequestPage<BigModel> requestPage);
}
