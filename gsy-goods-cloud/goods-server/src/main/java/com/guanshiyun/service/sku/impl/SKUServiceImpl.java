package com.guanshiyun.service.sku.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.db.dbnumber.ConstNumber;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.controller.sku.vo.*;
import com.guanshiyun.product.Product;
import com.guanshiyun.repository.sku.SKURepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.sku.SKUService;
import com.guanshiyun.service.utils.UtilsService;
import com.guanshiyun.sku.SKU;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SKUServiceImpl implements SKUService {
    private final SKURepository skuRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final UtilsService utilsService;
    private final SnowflakePermanent snowflakePermanent;
    private final TransactionalOperator transactionalOperator;

    /**
     * 解析 picList 字段（数据库里存的是 JSON 字符串）
     */
    private List<String> parsePicList(Object picListObj) {
        if (picListObj == null) {
            return List.of();
        }

        // 如果已经是 String（数据库读出来的就是 String）
        if (picListObj instanceof String s) {
            return JSONObject.parseObject(s, List.class);
        }

        // 其他类型（防止未来改成其他类型）
        String json = JSONObject.toJSONString(picListObj);
        return JSONObject.parseObject(json, List.class);
    }

    @Override
    public Mono<Long> save(SKUSaveVO skuVO) {

        SKU sku = BeanUtil.toBean(skuVO, SKU.class)
                .setPicList(JSONObject.toJSONString(skuVO.getPicList()));
        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY))
                return Mono.error(new RuntimeException("用户未登录"));
            Long userId = ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
            Integer stockVO = skuVO.getStock();
            Integer addStockVO = skuVO.getAddStock();
            int stock = 0;
            if (stockVO != null) {
                stock = stockVO;
            }
            if (addStockVO != null) {
                stock += addStockVO;
            }
            if (Objects.isNull(sku.getId())) {
                String code = snowflakePermanent.stringNextId();
                sku.setSkuCode(code)
                        .setStock(stock)
                        .setCreator(userId);
                sku.setCreateTime(LocalDateTime.now());
                return skuRepository.save(sku)
                        .flatMap(skuSave -> skuRepository.findAllByProductId(skuSave.getProductId()).collectList())
                        .flatMap(skuList -> {
                            //获取最低价
                            BigDecimal minPrice = skuList.stream()
                                    .map(SKU::getPrice)
                                    .filter(Objects::nonNull)
                                    .min(BigDecimal::compareTo)
                                    .orElse(BigDecimal.ZERO);
                            BigDecimal maxPrice = skuList.stream()
                                    .map(SKU::getPrice)
                                    .filter(Objects::nonNull)
                                    .max(BigDecimal::compareTo)
                                    .orElse(BigDecimal.ZERO);
                            Product product = Product.builder()
                                    .id(sku.getProductId())
                                    .minPrice(minPrice)
                                    .maxPrice(maxPrice)
                                    .updateTime(LocalDateTime.now())
                                    .updater(userId)
                                    .build();
                            return r2dbcUpdateHelper.updateIgnoreNull(
                                    EntityTableNameUtils.getName(Product.class),
                                    product,
                                    Product.Fields.id
                            ).thenReturn(sku.getId());
                        })
                        .transform(transactionalOperator::transactional)
                        .onErrorResume(throwable -> {
                            log.error("保存SKU失败", throwable);
                            return Mono.error(new Throwable(throwable));
                        });
            }

            sku.setUpdater(userId)
                .setUpdateTime(LocalDateTime.now());
            final int stockFinal = stock;
            return skuRepository.findById(sku.getId())
                            .flatMap(skuUpdate -> {
                                Integer skuStock = skuUpdate.getStock();

                                if(Objects.nonNull(skuStock)){
                                    sku.setStock(skuStock + stockFinal);
                                }
                                else {
                                    sku.setStock(stockFinal);
                                }
                                return   r2dbcUpdateHelper.updateIgnoreNull(
                                        EntityTableNameUtils.getName(SKU.class),
                                        sku,
                                        SKU.Fields.id
                                );
                            })
                    .flatMap(skuId -> skuRepository.findAllByProductId(sku.getProductId()).collectList())
                    .flatMap(skuList -> {
                        //获取最低价
                        BigDecimal minPrice = skuList.stream()
                                .map(SKU::getPrice)
                                .filter(Objects::nonNull)
                                .min(BigDecimal::compareTo)
                                .orElse(BigDecimal.ZERO);
                        BigDecimal maxPrice = skuList.stream()
                                .map(SKU::getPrice)
                                .filter(Objects::nonNull)
                                .max(BigDecimal::compareTo)
                                .orElse(BigDecimal.ZERO);
                        Product product = Product.builder()
                                .id(sku.getProductId())
                                .minPrice(minPrice)
                                .maxPrice(maxPrice)
                                .updateTime(LocalDateTime.now())
                                .updater(userId)
                                .build();
                        return r2dbcUpdateHelper.updateIgnoreNull(
                                EntityTableNameUtils.getName(Product.class),
                                product,
                                Product.Fields.id
                        ).thenReturn(sku.getId());
                    })
                    .transform(transactionalOperator::transactional);
        });
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return skuRepository.deleteById(id);
    }

    @Override
    public Mono<SKUVO> findById(Long id) {
        return skuRepository.findById(id)
                .map(sku -> BeanUtil.toBean(sku, SKUVO.class)
                        .setPicList(parsePicList(sku.getPicList()))
                );
    }

    //
    @Override
    public Mono<PageResultT<List<SKUGroupByProductIdVO>>> findAllByPage(
            RequestPage<SKUFindVO> requestPage) {

        Long pageNum = requestPage.getPageNum();
        int pageSize = requestPage.getPageSize();
        long offset = (pageNum-1) * pageSize;

        // 1. 分页查 productId
        Mono<List<Long>> productIdPageMono =
                r2dbcEntityTemplate.getDatabaseClient()
                        .sql("""
                                    SELECT DISTINCT product_id
                                    FROM sku
                                    WHERE del_flag = 0
                                    ORDER BY product_id
                                    LIMIT :limit OFFSET :offset
                                """)
                        .bind("limit", pageSize)
                        .bind("offset", offset)
                        .map((row, meta) -> row.get("product_id", Long.class))
                        .all()
                        .collectList();

        // 2. 查 productId 总数
        Mono<Long> totalMono =
                r2dbcEntityTemplate.getDatabaseClient()
                        .sql("""
                                    SELECT COUNT(DISTINCT product_id)
                                    FROM sku
                                    WHERE del_flag = 0
                                """)
                        .map((row, meta) -> row.get(ConstNumber.INT_ZERO, Long.class))
                        .one();

        return Mono.zip(productIdPageMono, totalMono)
                .flatMap(tuple -> {
                    List<Long> productIds = tuple.getT1();
                    long total = tuple.getT2();

                    if (productIds.isEmpty()) {
                        return Mono.just(PageResultT.<List<SKUGroupByProductIdVO>>builder()
                                .pageNum(pageNum)
                                .pageSize(pageSize)
                                .total(total)
                                .rows(List.of())
                                .build());
                    }

                    // 3. 批量查 SKU
                    Mono<List<SKU>> skuMono =
                            r2dbcEntityTemplate.select(SKU.class)
                                    .matching(Query.query(
                                            Criteria.where(SKU.Fields.productId).in(productIds)
                                    ))
                                    .all()
                                    .collectList();

                    // 4. 批量查 Product
                    Mono<List<Product>> productMono =
                            utilsService.findProductByProductId(productIds);

                    return Mono.zip(skuMono, productMono)
                            .map(data -> {
                                List<SKU> skuList = data.getT1();
                                log.info("skuList: {}", skuList);
                                List<Product> productList = data.getT2();

                                Map<Long, List<SKUVO>> skuByProductId =
                                        skuList.stream()
                                                .map(sku ->
                                                        BeanUtil.toBean(sku, SKUVO.class)
                                                                .setPicList(parsePicList(sku.getPicList()))
                                                )
                                                .collect(Collectors.groupingBy(SKUVO::getProductId));

                                Map<Long, Product> productMap =
                                        productList.stream()
                                                .collect(Collectors.toMap(Product::getId, p -> p));

                                List<SKUGroupByProductIdVO> rows = productIds.stream()
                                        .map(pid -> SKUGroupByProductIdVO.builder()
                                                .productId(pid)
                                                .productName(
                                                        Optional.ofNullable(productMap.get(pid))
                                                                .map(Product::getName)
                                                                .orElse("")
                                                )
                                                .skuList(skuByProductId.getOrDefault(pid, List.of()))
                                                .build()
                                        )
                                        .toList();

                                return PageResultT.<List<SKUGroupByProductIdVO>>builder()
                                        .pageNum(pageNum)
                                        .pageSize(pageSize)
                                        .total(total)
                                        .rows(rows)
                                        .build();
                            });
                })
                .onErrorResume(e -> {
                    log.error("分页查询 SKU 失败", e);
                    return Mono.empty();
                });
    }

//    @Override
//    public Mono<PageResultT<List<Map<Long, SKUVO>>>> findAllPage(RequestPage<ProductSearchVO> requestPage) {
//        Long pageNum = PageUtils.pageNum(requestPage.getPageNum());
//        Integer pageSize = PageUtils.pageSize(requestPage.getPageSize());
//        ProductSearchVO condition = requestPage.getCondition();
//        if (condition == null) {
//            condition = ProductSearchVO.builder().build();
//        }
//        return utilsService.findProductPage(pageNum, pageSize, condition);
//    }

    @Override
    public Flux<SKUVO> findByProductId(Long productId) {
        return skuRepository.findAllByProductId(productId)
                .map(sku -> BeanUtil.toBean(sku, SKUVO.class)
                        .setPicList(parsePicList(sku.getPicList()))
                );
    }

    @Override
    public Mono<Void> deleteAllById(List<Long> ids) {
        return skuRepository.deleteAllById(ids);
    }

    @Override
    public Mono<Boolean> reduceStockById(Long id, Integer count) {
        return skuRepository.reduceStockById(id, count)
                .map(rows -> rows > 0);
    }

    @Override
    public Mono<Boolean> addStockById(Long id, Integer count) {
        return skuRepository.addStockById(id, count)
                .map(rows -> rows > 0);
    }

    @Override
    public Mono<List<SKUVO>> findAllByIds(List<Long> skuIds) {
        return skuRepository.findAllById(skuIds)
                .mapNotNull(item -> BeanUtil.toBean(item, SKUVO.class))
                .collect(Collectors.toList())
                .onErrorResume(Mono::error);
    }

    @Override
    public Mono<Boolean> addSalesById(Long id, Integer count) {
        return skuRepository.findById(id)
                .flatMap(sku->{
                    Integer salesVolume = sku.getSalesVolume();
                    salesVolume += count;
                    sku.setSalesVolume(salesVolume);
                    Integer skuStock = sku.getStock();
                    skuStock += count;
                    sku.setStock(skuStock);
                    return skuRepository.save(sku)
                            .thenReturn(Boolean.TRUE);
                })
                .onErrorResume(e->{
                    log.error("增加销量失败", e);
                    return Mono.just(Boolean.FALSE);
                });
    }

    @Override
    public Mono<SkuStatisticsVO> totalStatistics() {
       Mono<Long> totalSales =  skuRepository.countTotalSales();
       return null;
    }
}
