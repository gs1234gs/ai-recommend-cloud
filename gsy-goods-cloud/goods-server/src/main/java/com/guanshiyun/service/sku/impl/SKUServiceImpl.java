package com.guanshiyun.service.sku.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.dbnumber.ConstNumber;
import com.db.dbsqlconst.SqlConst;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanshiyun.controller.sku.vo.SKUFindVO;
import com.guanshiyun.controller.sku.vo.SKUGroupByProductIdVO;
import com.guanshiyun.controller.sku.vo.SKUSaveVO;
import com.guanshiyun.controller.sku.vo.SKUVO;
import com.guanshiyun.controller.warehouse.vo.WarehouseVO;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.product.Product;
import com.guanshiyun.relationship.SKUWarehouse;
import com.guanshiyun.repository.product.ProductRepository;
import com.guanshiyun.repository.relation.SKUWarehouseRepository;
import com.guanshiyun.repository.sku.SKURepository;
import com.guanshiyun.repository.warehouse.WarehouseRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.sku.SKUService;
import com.guanshiyun.sku.SKU;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    private final SnowflakePermanent snowflakePermanent;
    private final TransactionalOperator transactionalOperator;
    private final MyLong myLong;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final SKUWarehouseRepository skuWarehouseRepository;
    private final WarehouseRepository warehouseRepository;
    /**
     * 解析 picList 字段（数据库里存的是 JSON 字符串）
     */
    @SneakyThrows
    private List<String> parsePicList(Object picListObj) {
        if (picListObj == null) {
            return List.of();
        }

        // 如果已经是 String（数据库读出来的就是 String）
        if (picListObj instanceof String s) {
//            return JSONObject.parseObject(s, List.class);
            //noinspection Convert2Diamond
            return objectMapper.readValue(s, new TypeReference<List<String>>() {
            });
        }

        // 其他类型（防止未来改成其他类型）
        String json = objectMapper.writeValueAsString(picListObj);
        //noinspection Convert2Diamond
        return objectMapper.readValue(json, new TypeReference<List<String>>() {
        });
    }

    @SneakyThrows
    @Override
    public Mono<Long> save(SKUSaveVO skuVO) {
        List<Long> warehouseIds = skuVO.getWarehouseIds();
        SKU sku = BeanUtil.toBean(skuVO, SKU.class)
                .setPicList(objectMapper.writeValueAsString(skuVO.getPicList()));
        return Mono.deferContextual(ctx -> {
            // 判断用户是否登录
            if (!myLong.hasKey(ctx))
                return Mono.error(new RuntimeException("用户未登录"));
            // 获取用户 ID 和租户 ID
            Long userId = myLong.findUserId(ctx);
            Long tenantId = myLong.findTenantId(ctx);
            // 获取库存
            Integer stockVO = skuVO.getStock(); // 库存
            Integer addStockVO = skuVO.getAddStock(); // 增加库存
            int stock = 0;
            if (stockVO != null) {
                stock = stockVO;
            }
            if (addStockVO != null) {
                stock += addStockVO;
            }
            LocalDateTime now = LocalDateTime.now();
            // 新增
            if (Objects.isNull(sku.getId())) {
                String code = snowflakePermanent.stringNextId();
                sku.setSkuCode(code)
                        .setStock(stock)
                        .setTenantId(tenantId)
                        .setCreator(userId)
                        .setCreateTime(now);
                return skuRepository.save(sku)
                        .flatMap(skuSave -> {
                            sku.setId(skuSave.getId());
                            return skuRepository
                                    .findAllByProductId(skuSave.getProductId()).collectList();
                        })
                        .flatMap(skuList -> {
                            //获取最低价
                            BigDecimal minPrice = skuList.stream()
                                    .map(SKU::getPrice)
                                    .filter(Objects::nonNull)
                                    .min(BigDecimal::compareTo)
                                    .orElse(BigDecimal.ZERO);
                            // 获取最高价
                            BigDecimal maxPrice = skuList.stream()
                                    .map(SKU::getPrice)
                                    .filter(Objects::nonNull)
                                    .max(BigDecimal::compareTo)
                                    .orElse(BigDecimal.ZERO);
                            // 构建 Product 对象
                            Product product = Product.builder()
                                    .id(sku.getProductId())
                                    .minPrice(minPrice)
                                    .maxPrice(maxPrice)
                                    .updateTime(now)
                                    .updater(userId)
                                    .build();
                            List<SKUWarehouse> warehouseListSave = warehouseIds.stream().map(warehouseId ->
                                    SKUWarehouse.builder()
                                            .skuId(sku.getId())
                                            .warehouseId(warehouseId)
                                            .build()
                            ).collect(Collectors.toList());
                            // 更新 Product 表
                            Mono<Long> updateIgnoreNullMono = r2dbcUpdateHelper.updateIgnoreNull(
                                    Product.class,
                                    product,
                                    Product.Fields.id
                            );
                            Mono<List<SKUWarehouse>> skuWarehouseSaveMono = skuWarehouseRepository.saveAll(warehouseListSave).collectList();
                            return Mono.zip(updateIgnoreNullMono, skuWarehouseSaveMono)
                                    .thenReturn(sku.getId());
                        })
                        .transform(transactionalOperator::transactional)
                        .onErrorResume(throwable -> {
                            log.error("保存SKU失败", throwable);
                            return Mono.error(new Throwable(throwable));
                        });
            }

            // 更新
            sku.setUpdater(userId)
                    .setUpdateTime(now);
            final int stockFinal = stock;
            // 更新 SKU 表
            return skuRepository.findById(sku.getId())
                    .flatMap(skuUpdate -> {
                        Integer skuStock = skuUpdate.getStock();

                        // 原始库存不为空，则增加库存
                        if (Objects.nonNull(skuStock)) {
                            sku.setStock(skuStock + stockFinal);
                        }
                        // 原始库存为空，则直接设置库存
                        else {
                            sku.setStock(stockFinal);
                        }
                        return r2dbcUpdateHelper.updateIgnoreNull(
                                SKU.class,
                                sku,
                                SKU.Fields.id
                        );
                    })
                    .flatMap(skuId -> {
                        Mono<List<SKU>> skuListMono = skuRepository.findAllByProductId(sku.getProductId()).collectList();
                        Mono<List<SKUWarehouse>> skuWarehouseMono = skuWarehouseRepository.findAllBySkuId(skuId).collectList();
                        return Mono.zip(skuListMono, skuWarehouseMono);
                    })
                    .flatMap(tuple -> {
                        List<SKU> skuList = tuple.getT1();
                        List<SKUWarehouse> skuWarehouseList = tuple.getT2();
                        //获取已保存的
                        List<Long> skuWarehouseIdList = skuWarehouseList
                                .stream()
                                .map(SKUWarehouse::getWarehouseId)
                                .toList();
                        //获取需要删除的，即VO有，DB没有的，得到要删除的id列表
                        List<Long> deleteSKUWarehouse = skuWarehouseList.stream()
                                .filter(skuWarehouse -> !warehouseIds.contains(skuWarehouse.getWarehouseId()))
                                .map(SKUWarehouse::getId)
                                .toList();
                        //过滤出要保存的，即VO有，DB没有的，得到要保存的
                        List<SKUWarehouse> saveSKUWarehouse = warehouseIds.stream()
                                .filter(warehouseId -> !skuWarehouseIdList.contains(warehouseId))
                                .map(warehouseId -> SKUWarehouse.builder()
                                        .skuId(sku.getId())
                                        .warehouseId(warehouseId)
                                        .build()).collect(Collectors.toList());
                        //获取最低价
                        BigDecimal minPrice = skuList.stream()
                                .map(SKU::getPrice)
                                .filter(Objects::nonNull)
                                .min(BigDecimal::compareTo)
                                .orElse(BigDecimal.ZERO);
                        // 获取最高价
                        BigDecimal maxPrice = skuList.stream()
                                .map(SKU::getPrice)
                                .filter(Objects::nonNull)
                                .max(BigDecimal::compareTo)
                                .orElse(BigDecimal.ZERO);
                        // 构建 Product 对象
                        Product product = Product.builder()
                                .id(sku.getProductId())
                                .minPrice(minPrice)
                                .maxPrice(maxPrice)
                                .updateTime(now)
                                .updater(userId)
                                .build();
                        // 更新 Product 表
                        Mono<Void> voidMono = skuWarehouseRepository.deleteAllById(deleteSKUWarehouse);
                        Mono<List<SKUWarehouse>> skuWarehouseSaveMono = skuWarehouseRepository.saveAll(saveSKUWarehouse).collectList();
                        Mono<Long> updateIgnoreNullMono = r2dbcUpdateHelper.updateIgnoreNull(
                                Product.class,
                                product,
                                Product.Fields.id
                        );
                        return Mono.when(voidMono, updateIgnoreNullMono, skuWarehouseSaveMono)
                                .thenReturn(sku.getId());
                    })
                    .transform(transactionalOperator::transactional);
        });
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        Mono<Void> deleteAllBySkuId = skuWarehouseRepository.deleteAllBySkuId(id);
        Mono<Void> deletedById = skuRepository.deleteById(id);
        return Mono.when(deleteAllBySkuId, deletedById)
                .transform(transactionalOperator::transactional);
    }

    @Override
    public Mono<SKUVO> findById(Long id) {
        return skuWarehouseRepository.findAllBySkuId(id)
                .collectList()
                .flatMap(skuWarehouseList -> {
                    return skuRepository.findById(id)
                            .flatMap(sku -> {
                                        return warehouseRepository
                                                .findAllById(
                                                        skuWarehouseList
                                                                .stream()
                                                                .map(SKUWarehouse::getWarehouseId)
                                                                .toList()
                                                )
                                                .collectList()
                                                .map(warehouse -> {
                                                    List<WarehouseVO> warehouseVOS =
                                                            BeanConvertUtil.toBeanList(warehouse, WarehouseVO.class);
                                                    return BeanUtil.toBean(sku, SKUVO.class)
                                                            .setPicList(parsePicList(sku.getPicList())
                                                            )
                                                            .setWarehouseList(warehouseVOS);
                                                });
                                    }
                            );
                });
    }

    //
    @Override
    public Mono<PageResultT<List<SKUGroupByProductIdVO>>> findAllByPage(
            RequestPage<SKUFindVO> requestPage) {
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) return Mono.error(new Throwable("登陆已经过期"));
//            Long userId = myLong.findUserId(ctx);
            Long tenantId = myLong.findTenantId(ctx);

//            RequestPage<SKU> skuRequestPage = BeanConvertUtil.toBean(requestPage, SKU.class);
//            return ReactivePageQuery.of(r2dbcEntityTemplate, SKU.class, skuRequestPage)

            Long pageNum = requestPage.getPageNum();
            int pageSize = requestPage.getPageSize();
            long offset = (pageNum - 1) * pageSize;


            // 1. 分页查 productId
            Mono<List<Long>> productIdPageMono =
                    r2dbcEntityTemplate.getDatabaseClient()
                            .sql("""
                                        SELECT DISTINCT product_id
                                        FROM sku
                                        WHERE del_flag = 0
                                        and tenant_id = :tenantId
                                        ORDER BY product_id
                                        LIMIT :limit OFFSET :offset
                                    """)
                            .bind(SqlConst.SQL_LIMIT.trim(), pageSize)
                            .bind(SqlConst.SQL_OFFSET.trim(), offset)
                            .bind(SKU.Fields.tenantId, tenantId)
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
                                productRepository.findAllById(productIds).collectList();

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
        });
    }

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
                .flatMap(sku -> {
                    Integer salesVolume = sku.getSalesVolume();
                    salesVolume += count;
                    sku.setSalesVolume(salesVolume);
                    Integer skuStock = sku.getStock();
                    skuStock += count;
                    sku.setStock(skuStock);
                    return skuRepository.save(sku)
                            .thenReturn(Boolean.TRUE);
                })
                .onErrorResume(e -> {
                    log.error("增加销量失败", e);
                    return Mono.just(Boolean.FALSE);
                });
    }

    @Override
    public Mono<Long> findTenantIdById(Long id) {
        return skuRepository.findTenantIdById(id);
    }
}
