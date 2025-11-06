package com.guanshiyun.service.product.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.constsql.SqlConst;
import com.db.cursorQuery.CursorQuery;
import com.db.page.PageUtils;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.controller.product.vo.ProductCustomerVO;
import com.guanshiyun.controller.product.vo.ProductSaveVO;
import com.guanshiyun.controller.product.vo.ProductVO;
import com.guanshiyun.product.Product;
import com.guanshiyun.relationship.ProductWarehouse;
import com.guanshiyun.repository.sku.SKURepository;
import com.guanshiyun.repository.product.ProductRepository;
import com.guanshiyun.repository.relation.ProductTagRepository;
import com.guanshiyun.repository.relation.ProductWarehouseRepository;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.product.ProductService;
import com.guanshiyun.sku.SKU;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final ProductRepository productRepository;
    private final MyBigInteger myBigInteger;
    private final DatabaseClient databaseClient;
    private final ProductTagRepository productTagRepository;
    private final ProductWarehouseRepository productWarehouseRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TransactionalOperator transactionalOperator;
    private final SKURepository skuRepository;

    @Override
    public Mono<BigInteger> save(ProductSaveVO productSaveVO) {
        Product product = BeanUtil.toBean(productSaveVO, Product.class);
        List<BigInteger> warehouseIdList = productSaveVO.getWarehouseId();
        BigInteger tagId = productSaveVO.getTagId();
        BigInteger categoryId = productSaveVO.getCategoryId();
        return Mono.deferContextual(ctx -> {
                    BigInteger useId = myBigInteger.bigInteger(
                            ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
                    );
                    if (Objects.isNull(useId))
                        return Mono.error(new RuntimeException("用户不存在"));
                    if (warehouseIdList.isEmpty())
                        return Mono.error(new RuntimeException("仓库不存在"));
//                    if (Objects.isNull(tagId))
//                        return Mono.error(new RuntimeException("标签不存在"));
//                    if (Objects.isNull(categoryId))
//                        return Mono.error(new RuntimeException("分类不存在"));
                    if (Objects.isNull(product.getId())) {
                        product.setCreateTime(LocalDateTime.now());
                        product.setCreator(useId);
                        //创建，同时创建仓库关系
                        return productRepository.save(product)
                                .map(Product::getId)
                                .flatMap(productId ->
                                        productWarehouseRepository.saveAll(
                                                        warehouseIdList.stream()
                                                                .map(warehouseId ->
                                                                        ProductWarehouse.builder()
                                                                                .id(null)
                                                                                .productId(productId)
                                                                                .creator(useId)
                                                                                .createTime(LocalDateTime.now())
                                                                                .warehouseId(warehouseId)
                                                                                .build()
                                                                )
                                                                .toList()
                                                ).count()
                                                .thenReturn(productId)
                                                .onErrorResume(throwable -> {
                                                    log.error("保存仓库关系失败：", throwable);
                                                    return Mono.error(new Exception("保存仓库关系失败"));
                                                })
                                )
                                .onErrorResume(throwable -> {
                                    log.error("保存商品信息失败：", throwable);
                                    return Mono.error(new Exception("保存商品信息失败"));
                                });
                    }
                    product.setUpdater(useId);
                    product.setUpdateTime(LocalDateTime.now());
                    return r2dbcUpdateHelper.updateIgnoreNull(
                                    EntityTableNameUtils.getName(Product.class),
                                    product,
                                    Product.Fields.id
                            )
                            .flatMap(id ->
                                    productWarehouseRepository.saveAll(
                                                    warehouseIdList.stream()
                                                            .map(warehouseId ->
                                                                    ProductWarehouse.builder()
                                                                            .id(null)
                                                                            .productId(id)
                                                                            .updater(useId)
                                                                            .updateTime(LocalDateTime.now())
                                                                            .warehouseId(warehouseId)
                                                                            .build()
                                                            )
                                                            .toList()
                                            ).count()
                                            .thenReturn(product.getId())
                            )
                            .map(savaProduct -> savaProduct);
                })
                .transform(transactionalOperator::transactional)
                .onErrorResume(throwable -> {
                    log.error("保存失败：", throwable);
                    return Mono.just(ConstNumber.BIG_INTEGER_ZERO);
                });
        /**
         * 删除
         * */
    }

    @Override
    public Mono<Long> deleteById(BigInteger id) {
        return databaseClient.sql("delete from product where id = :id")
                .bind(Product.Fields.id, id)
                .fetch()
                .rowsUpdated()
                //商品基础信息不存在，其对应关联的标签，仓库也没有
                .flatMap(deleteCount ->
                        productWarehouseRepository.deleteAllByProductId(id)
                                .thenReturn(deleteCount)
                )
                //删除商品标签
                .flatMap(deleteCount ->
                        productTagRepository.deleteAllByProductId(id)
                                .thenReturn(deleteCount)
                )
                .flatMap(deleteCount ->
                        skuRepository.deleteAllByProductId(id)
                                .thenReturn(deleteCount)
                )
                //事务
                .transform(transactionalOperator::transactional);
//            .as(transactional ->
//                    transactional.as(new Function<Mono<Long>, Mono<Long>>() {
//                        @Override
//                        public Mono<Long> apply(Mono<Long> source) {
//                            return transactionalOperator.transactional(source);
//                        }
//                    }));
    }

    @Override
    public Mono<PageResultT<List<ProductVO>>> findPage(RequestPage<ProductVO> requestPage) {
        //校验参数
        RequestPage<ProductVO> chatRecordRequestPage = PageUtils.pageValidation(requestPage, ProductVO.class);
        //起始页码
        BigInteger pageNum = chatRecordRequestPage.getPageNum();
        //每页数量
        Integer pageSize = PageUtils.pageSize(chatRecordRequestPage.getPageSize());
        //查询条件
        ProductVO condition = chatRecordRequestPage.getCondition();
        //分类id
        BigInteger categoryId = condition.getCategoryId();
        //标签id
        BigInteger tagId = condition.getTagId();
        //仓库id
        BigInteger warehouseId = condition.getWarehouseId();
        //名称
        String name = condition.getName();
        LocalDateTime offlineTime = condition.getOfflineTime();
        LocalDateTime publishTime = condition.getPublishTime();
        BigDecimal price = condition.getPrice();
        String brand = condition.getBrand();
        LocalDateTime startTime = condition.getStartTime();
        LocalDateTime endTime = condition.getEndTime();
        // 计算 offset
        long offset = pageNum.subtract(BigInteger.ONE)
                .multiply(BigInteger.valueOf(pageSize))
                .longValue();

        return Mono.deferContextual(ctx -> {
            BigInteger userId = myBigInteger
                    .bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
            if (Objects.isNull(userId))
                return Mono.just(PageResultT.<List<ProductVO>>builder()
                        .total(ConstNumber.INT_ZERO)
                        .rows(Collections.emptyList())
                        .build());

            // 如果tagId不为 null，则添加条件
            if (Objects.nonNull(tagId)) {
                return productTagRepository.findByTagId(tagId)
                        .collectList()
                        .flatMap(productIds -> {
                            if (!productIds.isEmpty()) {
                                // 再继续用仓库id过滤
                                return productWarehouseRepository.findByWarehouseId(warehouseId)
                                        .collectList()
                                        .flatMap(productIdsList -> {
                                            // 不为空，取交集
                                            List<BigInteger> productIdList;
                                            if (!productIdsList.isEmpty()) {
                                                productIdList = productIds.stream()
                                                        .filter(productIdsList::contains)
                                                        .toList();
                                            } else {
                                                // 为空，则返回所有 tag 匹配的
                                                productIdList = productIds;
                                            }
                                            return Mono.just(productIdList);
                                        });
                            } else {
                                // tagIds 为空，直接查 warehouse
                                return productWarehouseRepository.findByWarehouseId(warehouseId)
                                        .collectList();
                            }
                        })
                        .flatMap(productIds -> {
                            // 使用 Criteria 拼接条件
                            Criteria criteria = Criteria.empty();
                            if (!productIds.isEmpty()) {
                                criteria = criteria.and(Product.Fields.id).in(productIds);
                            } else {
                                criteria = criteria.and(Product.Fields.id).isNull(); // 无匹配数据
                            }
                            if (Objects.nonNull(name)) {
                                criteria = criteria.and(Product.Fields.name).like(SqlConst.PERCENT + name + SqlConst.PERCENT);
                            }
                            if (Objects.nonNull(categoryId)) {
                                criteria = criteria.and(Product.Fields.categoryId).is(categoryId);
                            }
                            if (Objects.nonNull(offlineTime)) {
                                criteria = criteria.and(Product.Fields.offlineTime).is(offlineTime);
                            }
                            if (Objects.nonNull(publishTime)) {
                                criteria = criteria.and(Product.Fields.publishTime).is(publishTime);
                            }
                            if (Objects.nonNull(brand)) {
                                criteria = criteria.and(Product.Fields.brand).like(SqlConst.PERCENT + brand + SqlConst.PERCENT);
                            }
                            if (Objects.nonNull(startTime)) {
                                criteria = criteria.and(BasePojo.Fields.createTime).greaterThanOrEquals(startTime);
                            }
                            if (Objects.nonNull(endTime)) {
                                criteria = criteria.and(BasePojo.Fields.createTime).lessThanOrEquals(endTime);
                            }

                            // 添加用户权限
                            criteria = criteria.and(BasePojo.Fields.creator).is(userId);

                            // 构建查询（分页）
                            Query dataQuery = Query.query(criteria)
                                    .sort(Sort.by(Sort.Order.desc(BasePojo.Fields.createTime)))
                                    .offset(offset)
                                    .limit(pageSize);

                            Query countQuery = Query.query(criteria);

                            // 执行查询
                            return r2dbcEntityTemplate.select(countQuery, Product.class)
                                    .count()
                                    .flatMap(count -> r2dbcEntityTemplate.select(dataQuery, Product.class)
                                            .collectList()
                                            .map(products -> {
                                                // 假设你有方法将 Product 转为 ProductVO
                                                List<ProductVO> voList = ProductVO.fromEntities(products);
                                                return PageResultT.<List<ProductVO>>builder()
                                                        .total(count)
                                                        .rows(voList)
                                                        .build();
                                            })
                                    );
                        });
            } else {
                // 如果 tagId 为 null，只按 warehouseId 过滤
                Mono<List<BigInteger>> productIdsMono;
                if (Objects.nonNull(warehouseId)) {
                    productIdsMono = productWarehouseRepository.findByWarehouseId(warehouseId)
                            .collectList();
                } else {
                    productIdsMono = Mono.just(Collections.emptyList());
                }

                return productIdsMono.flatMap(productIds -> {
                    Criteria criteria = Criteria.empty();
                    if (!productIds.isEmpty()) {
                        criteria = criteria.and(Product.Fields.id).in(productIds);
                    }
                    if (Objects.nonNull(name)) {
                        criteria = criteria.and(Product.Fields.name).like(SqlConst.PERCENT + name + SqlConst.PERCENT);
                    }
                    if (Objects.nonNull(categoryId)) {
                        criteria = criteria.and(Product.Fields.categoryId).is(categoryId);
                    }
                    if (Objects.nonNull(offlineTime)) {
                        criteria = criteria.and(Product.Fields.offlineTime).is(offlineTime);
                    }
                    if (Objects.nonNull(publishTime)) {
                        criteria = criteria.and(Product.Fields.publishTime).is(publishTime);
                    }
                    if (Objects.nonNull(brand)) {
                        criteria = criteria.and(Product.Fields.brand).like(SqlConst.PERCENT + brand + SqlConst.PERCENT);
                    }
                    if (Objects.nonNull(startTime)) {
                        criteria = criteria.and(BasePojo.Fields.createTime).greaterThanOrEquals(startTime);
                    }
                    if (Objects.nonNull(endTime)) {
                        criteria = criteria.and(BasePojo.Fields.createTime).lessThanOrEquals(endTime);
                    }

                    // 添加用户权限
                    criteria = criteria.and(BasePojo.Fields.creator).is(userId);

                    Query dataQuery = Query.query(criteria)
                            .sort(Sort.by(Sort.Order.desc(BasePojo.Fields.createTime)))
                            .offset(offset)
                            .limit(pageSize);
                    Query countQuery = Query.query(criteria);

                    return r2dbcEntityTemplate.select(countQuery, Product.class)
                            .count()
                            .flatMap(count -> r2dbcEntityTemplate.select(dataQuery, Product.class)
                                    .collectList()
                                    .map(products -> {
                                        List<ProductVO> voList = ProductVO.fromEntities(products);
                                        return PageResultT.<List<ProductVO>>builder()
                                                .total(count)
                                                .rows(voList)
                                                .build();
                                    })
                            );
                });
            }
        });
    }

    @Override
    public Mono<CursorPageResult<List<ProductCustomerVO>>> findCursor(RequestCursorPage<ProductVO> requestCursorPage) {
        Product product = BeanUtil.toBean(requestCursorPage.getCondition(), Product.class);
        RequestCursorPage<Product> cursorPage = RequestCursorPage.<Product>builder()
                .lastId(requestCursorPage.getLastId())
                .order(requestCursorPage.getOrder())
                .pageSize(requestCursorPage.getPageSize())
                .condition(product)
                .build();
        /**
         *
         * 这里需要使用协同过滤，大模型决策后规则来查询，暂时忽略
         * */
        return CursorQuery.of(r2dbcEntityTemplate, Product.class, cursorPage)

                .list()
                .collectList()
                .map(products -> {
                    // 判断是否有下一页
                    boolean hasNext = products.size() > requestCursorPage.getPageSize();
                    // 截取真实需要的数据
                    List<Product> data = hasNext ? products.subList(0, requestCursorPage.getPageSize()) : products;

                    return Tuples.of(data, hasNext);
                })
                .flatMapMany(tupleT -> {
                            List<Product> productList = tupleT.getT1();
                            boolean hasNext = tupleT.getT2();
                            //根据id获取最低价格sku产品返回
                            return Flux.fromIterable(productList)
                                    .flatMap(item -> {
                                        Mono<SKU> skuByProductId = skuRepository.findSKUIDByProductId(item.getId());
                                        Mono<Integer> salesByProductId = skuRepository.sumSalesByProductId(item.getId());
                                        Mono<BigInteger> tagByProductId = productTagRepository.findTagByProductId(item.getId());
                                        return Mono.zip(skuByProductId, salesByProductId, tagByProductId)
                                                .map(tuple -> {
                                                    SKU sku = tuple.getT1();
                                                    Integer salesVolume = tuple.getT2();
                                                    BigInteger tagId = tuple.getT3();
                                                    return ProductCustomerVO
                                                            .builder()
                                                            .id(item.getId())
                                                            .discountPrice(sku.getPrice())
                                                            .originalPrice(sku.getCostPrice())
                                                            .level(item.getLevel())
                                                            .image(item.getImage())
                                                            .brand(item.getBrand())
                                                            .stock(sku.getStock())
                                                            .video(item.getVideo())
                                                            .description(item.getDescription())
                                                            .salesVolume(salesVolume)
                                                            .skuId(sku.getId())
                                                            .status(sku.getStatus())
                                                            .placeOfOrigin(item.getPlaceOfOrigin())
                                                            .name(item.getName())
                                                            .offlineTime(item.getOfflineTime())
                                                            .publishTime(item.getPublishTime())
                                                            .tagId(tagId)
                                                            .build();
                                                })
                                                .onErrorResume(e -> {
                                                    log.error("查询信息失败：", e);
                                                    return Mono.error(new Exception("查询信息失败"));
                                                });
                                    })
                                    .collectList()
                                    .map(voList ->
                                            CursorPageResult.<List<ProductCustomerVO>>builder()
                                                    .hasNext(hasNext)
                                                    .rows(voList)
                                                    .cursor(!voList.isEmpty() ? voList.getLast().getId() : null)
                                                    .build()
                                    );

                        }
                ).next();
    }

    //批量添加，批量更新
    @Override
    public Mono<Long> save(List<ProductSaveVO> productSaveVOList) {
        return Mono.deferContextual(ctx -> {
            BigInteger userId = myBigInteger
                    .bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
            if (Objects.isNull(userId))
                return Mono.just(ConstNumber.LONG_ZERO);
            return Flux.fromIterable(productSaveVOList)
                    .concatMap(productSaveVO -> {
                        Product product = BeanUtil.toBean(productSaveVO, Product.class);
                        List<BigInteger> warehouseIdList = productSaveVO.getWarehouseId();
                        return Objects.isNull(productSaveVO.getId()) ?
                                r2dbcEntityTemplate.insert(Product.class)
                                        .using(product)
                                        .flatMap(item ->
                                                productWarehouseRepository.saveAll(
                                                                warehouseIdList.stream()
                                                                        .map(warehouseId ->
                                                                                ProductWarehouse.builder()
                                                                                        .id(null)
                                                                                        .productId(product.getId())
                                                                                        .warehouseId(warehouseId)
                                                                                        .build()
                                                                        )
                                                                        .toList()
                                                        )
                                                        .then(Mono.just( item))
                                        )
                                :
                                r2dbcUpdateHelper.updateIgnoreNull(
                                                EntityTableNameUtils.getName(Product.class),
                                                product,
                                                Product.Fields.id
                                        )
                                        .flatMap(savaProduct ->
                                                productWarehouseRepository.saveAll(
                                                                warehouseIdList.stream()
                                                                        .map(warehouseId ->
                                                                                ProductWarehouse.builder()
                                                                                        .id(null)
                                                                                        .productId(product.getId())
                                                                                        .warehouseId(warehouseId)
                                                                                        .build()
                                                                        )
                                                                        .toList()
                                                        ).then(Mono.just(savaProduct))
                                        );

                    })
                    .transform(transactionalOperator::transactional)
                    .count()
                    .doOnSuccess(count -> log.info("批量保存成功：{}", count))
                    .onErrorResume(ex -> {
                        log.error("批量保存失败，事务将回滚", ex);
                        return Mono.error(new Throwable(ex));
                    });
        });
    }

    @Override
    public Mono<Void> deleteAllById(List<BigInteger> ids) {
        return productRepository.deleteAllById(ids);
    }

    @Override
    public Mono<ProductVO> findById(BigInteger id) {
        return productRepository.findById(id)
                .map(product -> BeanUtil.toBean(product, ProductVO.class));
    }

}
