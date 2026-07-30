package com.guanshiyun.service.model.impl;

import com.db.cursorQuery.ReactiveQuery;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.bigmodel.BigModel;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.repository.model.BigModelRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.model.BigModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BigModelServiceImpl implements BigModelService {
    private final BigModelRepository bigModelRepository;
    private final MyLong myLong;
    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final ReactiveQuery reactiveQuery;

    @Override
    public Mono<Long> sava(BigModel bigModel) {
        return Mono.deferContextual(ctx -> {
            Long userId = myLong.findUserId(ctx);
            LocalDateTime now = LocalDateTime.now();
            if (Objects.isNull(bigModel.getId())) {
                bigModel.setCreator(userId);
                bigModel.setCreateTime(now);
                return bigModelRepository.save(bigModel)
                        .flatMap(model -> {
                            log.info("保存成功");
                            return Mono.just(model.getId());
                        })
                        .onErrorResume(throwable -> {
                            log.info("保存失败", throwable);
                            return Mono.error(new Throwable(throwable));
                        });
            }

            bigModel.setUpdater(userId);
            bigModel.setUpdateTime(now);
            return r2dbcUpdateHelper.updateIgnoreNull(
                    BigModel.class,
                    bigModel,
                    BigModel.Fields.id
            );

        });
    }

    @Override
    public Mono<Long> deleteById(Long id) {
        if (Objects.isNull(id))
            return Mono.just(ConstNumber.LONG_ZERO);
        return databaseClient.sql("UPDATE big_model SET del_flag = :delFlag where id = :id")
                .bind(BasePojo.Fields.delFlag, ConstNumber.INT_ONE)
                .bind(BigModel.Fields.id, id)
                .fetch()
                .rowsUpdated()
                .flatMap(rowsUpdated -> {
                    log.info("删除成功");
                    return Mono.just(myLong.myLong(rowsUpdated));
                });
    }

    @Override
    public Mono<BigModel> findById(Long id) {
        return bigModelRepository.findById(id);
    }

    @Override
    public Mono<PageResultT<List<BigModel>>> findPage(RequestPage<BigModel> requestPage) {
        return reactiveQuery.createQuery(BigModel.class, requestPage)
                .like(BigModel.Fields.name, requestPage.getCondition().getName())
                .page();
    }
}
