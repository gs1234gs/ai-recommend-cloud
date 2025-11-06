package com.guanshiyun.service.model.impl;

import com.db.cursorQuery.ReactivePageQuery;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.bigmodel.BigModel;
import com.guanshiyun.repository.model.BigModelRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.model.BigModelService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BigModelServiceImpl implements BigModelService {
    private final BigModelRepository bigModelRepository;
    private final MyBigInteger myBigInteger;
    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    @Override
    public Mono<BigInteger> sava(BigModel bigModel) {
        return Mono.deferContextual(ctx ->{
            BigInteger userId = myBigInteger.bigInteger(
                    ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
            if(Objects.isNull(bigModel.getId()))
                return Mono.just(BigInteger.ZERO);
           if(Objects.isNull(userId)){
               bigModel.setCreator(userId);
               bigModel.setCreateTime(LocalDateTime.now());
           }else{
               bigModel.setUpdater(userId);
               bigModel.setUpdateTime(LocalDateTime.now());
           }
          return bigModelRepository.save(bigModel)
                    .flatMap(model->{
                        log.info("保存成功");
                        return Mono.just(model.getId());
                    })
                    .switchIfEmpty( Mono.fromRunnable(() -> log.info("保存失败"))
                            .then(Mono.just(BigInteger.ZERO))
                    )
                    .onErrorResume(throwable ->{
                        log.info("保存失败", throwable);
                        return Mono.just(BigInteger.ZERO);
                    });
        });
    }

    @Override
    public Mono<BigInteger> deleteById(BigInteger id) {
        if(Objects.isNull(id))
            return Mono.just(BigInteger.ZERO);
        return databaseClient.sql("delete from big_model where id = :id")
                .bind(BigModel.Fields.id, id)
                .fetch()
                .rowsUpdated()
                .flatMap(rowsUpdated ->{
                    log.info("删除成功");
                    return Mono.just(myBigInteger
                            .bigInteger(rowsUpdated));
                });
    }

    @Override
    public Mono<BigModel> findById(BigInteger id) {
        return bigModelRepository.findById(id);
    }

    @Override
    public Mono<PageResultT<List<BigModel>>> findPage(RequestPage<BigModel> requestPage) {
        return ReactivePageQuery.of(r2dbcEntityTemplate,BigModel.class,requestPage)
                .page();
    }
}
