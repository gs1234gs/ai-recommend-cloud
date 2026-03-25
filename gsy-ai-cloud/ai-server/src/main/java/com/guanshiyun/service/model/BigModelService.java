package com.guanshiyun.service.model;

import com.guanshiyun.bigmodel.BigModel;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Mono;


import java.util.List;

public interface BigModelService {
    //添加大模型
    Mono<Long> sava(BigModel bigModel);

    Mono< Long> deleteById(Long id);

    Mono<BigModel> findById(Long id);

    Mono<PageResultT<List<BigModel>>> findPage(RequestPage<BigModel> requestPage);
}
