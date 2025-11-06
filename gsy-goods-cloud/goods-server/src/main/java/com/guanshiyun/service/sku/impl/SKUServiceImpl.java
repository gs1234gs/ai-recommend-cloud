package com.guanshiyun.service.sku.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.cursorQuery.ReactivePageQuery;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.controller.sku.vo.SKUFindVO;
import com.guanshiyun.controller.sku.vo.SKUVO;
import com.guanshiyun.repository.sku.SKURepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.sku.SKUService;
import com.guanshiyun.sku.SKU;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SKUServiceImpl implements SKUService {
    private final SKURepository skuRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;

    @Override
    public Mono<BigInteger> save(SKUVO skuVO) {
        SKU sku = BeanUtil.toBean(skuVO, SKU.class);
       return Mono.deferContextual(ctx->{
            if(!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY))
                return Mono.error(new RuntimeException("用户未登录"));
            BigInteger userId =
                    ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
            if(Objects.isNull(sku.getId())){
                sku.setCreator(userId);
                sku.setCreateTime(LocalDateTime.now());
                return skuRepository.save(sku)
                        .map(SKU::getId)
                        .onErrorResume(throwable -> {
                            log.error("保存SKU失败", throwable);
                            return Mono.error(new Throwable(throwable));
                        });
            }
            sku.setUpdater(userId);
            sku.setUpdateTime(LocalDateTime.now());
            return r2dbcUpdateHelper.updateIgnoreNull(
                    EntityTableNameUtils.getName(SKU.class),
                    sku,
                    SKU.Fields.id
            );
       });
    }

    @Override
    public Mono<Void> deleteById(BigInteger id) {
        return skuRepository.deleteById(id);
    }

    @Override
    public Mono<SKUFindVO> findById(BigInteger id) {
        return skuRepository.findById(id)
                .map(sku -> BeanUtil.toBean(sku, SKUFindVO.class));
    }

    @Override
    public Mono<PageResultT<List<SKUFindVO>>> findAllByPage(RequestPage<SKUFindVO> requestPage) {
        SKU sku = BeanUtil.toBean(requestPage.getCondition(), SKU.class);
        ;
        return ReactivePageQuery.of(r2dbcEntityTemplate,
                SKU.class,
                RequestPage.<SKU>builder()
                        .pageNum(requestPage.getPageNum())
                .pageSize(requestPage.getPageSize())
                .condition(sku)
                .build())
                .page()
                .map(pageResultT -> PageResultT.<List<SKUFindVO>>builder()
                        .pageNum(pageResultT.getPageNum())
                        .pageSize(pageResultT.getPageSize())
                        .total(pageResultT.getTotal())
                        .rows(pageResultT.getRows()
                                .stream().map(
                                        sku1 ->
                                                BeanUtil.toBean(sku1,
                                                SKUFindVO.class))
                                .toList())
                        .build())
                .onErrorResume(throwable -> {
                    log.error("查询SKU失败", throwable);
                    return Mono.empty();
                });
    }

    @Override
    public Flux<SKUVO> findByProductId(BigInteger productId) {
       return skuRepository.findAllByProductId(productId)
                .map(sku1-> BeanUtil.toBean(sku1, SKUVO.class));
    }

    @Override
    public Mono<Void> deleteAllById(List<BigInteger> ids) {
        return skuRepository.deleteAllById(ids);
    }

    @Override
    public Mono<Boolean> reduceStockById(BigInteger id, Integer count) {
        return skuRepository.reduceStockById(id, count)
                .map(rows -> rows > 0);
    }

    @Override
    public Mono<Boolean> addStockById(BigInteger id, Integer count) {
        return skuRepository.addStockById(id, count)
                .map(rows -> rows > 0);
    }
}
