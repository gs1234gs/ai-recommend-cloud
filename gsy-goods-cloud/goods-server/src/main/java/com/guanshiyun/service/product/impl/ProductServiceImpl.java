package com.guanshiyun.service.product.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.constsql.SqlConst;
import com.db.cursorQuery.CursorQuery;
import com.db.page.PageUtils;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.category.Category;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.controller.product.vo.ProductSaveVO;
import com.guanshiyun.controller.product.vo.ProductVO;
import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.product.Product;
import com.guanshiyun.relationship.ProductCategory;
import com.guanshiyun.relationship.ProductWarehouse;
import com.guanshiyun.repository.category.CategoryRepository;
import com.guanshiyun.repository.product.ProductRepository;
import com.guanshiyun.repository.relation.ProductCategoryRepository;
import com.guanshiyun.repository.relation.ProductTagRepository;
import com.guanshiyun.repository.relation.ProductWarehouseRepository;
import com.guanshiyun.repository.sku.SKURepository;
import com.guanshiyun.repository.tag.TagRepository;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.rpc.chatrecommend.AiChatClientRecommendServiceApi;
import com.guanshiyun.service.product.ProductService;
import com.guanshiyun.sku.SKU;
import com.guanshiyun.tag.Tag;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final ProductCategoryRepository productCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final AiChatClientRecommendServiceApi aiChatClientRecommendServiceApi;

    @Override
    public Mono<BigInteger> save(ProductSaveVO productSaveVO) {
        Product product = BeanUtil.toBean(productSaveVO, Product.class);
        List<BigInteger> warehouseIdList = productSaveVO.getWarehouseId();
        List<BigInteger> tagIds = productSaveVO.getTagId();
        LocalDateTime now = LocalDateTime.now();
        List<BigInteger> categoryIds = productSaveVO.getCategoryId();
        List<BigInteger> skuList = productSaveVO.getSkuList();
        return Mono.deferContextual(ctx -> {
                    BigInteger useId = myBigInteger.bigInteger(
                            ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
                    );
                    if (Objects.isNull(useId))
                        return Mono.error(new RuntimeException("用户不存在"));
                    if (warehouseIdList.isEmpty())
                        return Mono.error(new RuntimeException("仓库不存在"));
//                    if (Objects.isNull(tagIds))
//                        return Mono.error(new RuntimeException("标签不存在"));
                    if (Objects.isNull(categoryIds))
                        return Mono.error(new RuntimeException("分类不存在"));
                    if (Objects.isNull(product.getId())) {
                        product.setCreateTime(now);
                        product.setCreator(useId);
                        //对插入的商品进行向量化
                        //创建，同时创建仓库关系
                        return productRepository.save(product)
                                .map(Product::getId)
                                .flatMap(productId -> {
                                            // 保存仓库关系
                                            Flux<ProductWarehouse> productIdWarehouseFlux = productWarehouseRepository.saveAll(
                                                    warehouseIdList.stream()
                                                            .map(warehouseId -> ProductWarehouse.builder()
                                                                    .productId(productId)
                                                                    .creator(useId)
                                                                    .createTime(now)
                                                                    .warehouseId(warehouseId)
                                                                    .build())
                                                            .collect(Collectors.toList())
                                            );

                                            Flux<ProductCategory> productIdCategoryFlux = productCategoryRepository.saveAll(
                                                    categoryIds.stream()
                                                            .map(categoryId ->
                                                                    ProductCategory.builder()
                                                                            .id(null)
                                                                            .productId(productId)
                                                                            .creator(useId)
                                                                            .createTime(now)
                                                                            .categoryId(categoryId)
                                                                            .build()
                                                            )
                                                            .collect(Collectors.toList())
                                            );
                                            return Flux.merge(productIdWarehouseFlux, productIdCategoryFlux)
                                                    .then(Mono.just(productId))
                                                    .onErrorResume(throwable -> {
                                                        log.error("保存商品仓库关系失败：", throwable);
                                                        return Mono.error(new Exception("保存商品仓库关系失败"));
                                                    });
                                        }
                                )
                                .flatMap(productId -> bigIntegerMono(productId, skuList, categoryIds, tagIds, product))
                                .onErrorResume(throwable -> {
                                    log.error("保存商品信息失败：", throwable);
                                    return Mono.error(new Exception("保存商品信息失败"));
                                });
                    }
                    product.setUpdater(useId);
                    product.setUpdateTime(now);
                    return r2dbcUpdateHelper.updateIgnoreNull(
                                    EntityTableNameUtils.getName(Product.class),
                                    product,
                                    Product.Fields.id
                            )
                            .flatMap(productId -> {
                                        // --- 查询旧关系 ---
                                        Mono<List<ProductWarehouse>> existingWarehouses = productWarehouseRepository.findByProductId(productId).collectList();
                                        Mono<List<ProductCategory>> existingCategories = productCategoryRepository.findByProductId(productId).collectList();
                                        return Mono.zip(existingWarehouses, existingCategories)
                                                .flatMap(tuple -> {
                                                    List<ProductWarehouse> oldWarehouseList = tuple.getT1();
                                                    List<ProductCategory> oldCategoryList = tuple.getT2();
                                                    // --- 计算新增和删除的仓库关系 ---
                                                    List<BigInteger> oldWarehouseIds = oldWarehouseList.stream()
                                                            .map(ProductWarehouse::getWarehouseId)
                                                            .toList();
                                                    List<ProductWarehouse> toAddWarehouse = warehouseIdList.stream()
                                                            .filter(id -> !oldWarehouseIds.contains(id))
                                                            .map(id -> ProductWarehouse.builder()
                                                                    .productId(productId)
                                                                    .creator(useId)
                                                                    .createTime(now)
                                                                    .warehouseId(id)
                                                                    .build())
                                                            .collect(Collectors.toList());
                                                    List<ProductWarehouse> toDeleteWarehouse = oldWarehouseList.stream()
                                                            .filter(pw -> !warehouseIdList.contains(pw.getWarehouseId()))
                                                            .toList();
                                                    Mono<Void> deleteWarehouseMono = productWarehouseRepository.deleteAll(toDeleteWarehouse).then();
                                                    Flux<ProductWarehouse> insertWarehouseFlux = productWarehouseRepository.saveAll(toAddWarehouse);  // --- 计算新增和删除的分类关系 ---
                                                    List<BigInteger> oldCategoryIds = oldCategoryList.stream()
                                                            .map(ProductCategory::getCategoryId).toList();
                                                    List<ProductCategory> toAddCategory = categoryIds.stream()
                                                            .filter(id -> !oldCategoryIds.contains(id))
                                                            .map(id -> ProductCategory.builder()
                                                                    .productId(productId)
                                                                    .creator(useId)
                                                                    .createTime(now)
                                                                    .categoryId(id)
                                                                    .build())
                                                            .collect(Collectors.toList());
                                                    List<ProductCategory> toDeleteCategory = oldCategoryList.stream()
                                                            .filter(pc -> !categoryIds.contains(pc.getCategoryId()))
                                                            .toList();

                                                    Mono<Void> deleteCategoryMono = productCategoryRepository.deleteAll(toDeleteCategory).then();
                                                    Flux<ProductCategory> insertCategoryFlux = productCategoryRepository.saveAll(toAddCategory);

                                                    // --- 合并执行所有关系更新 ---
                                                    return Mono.when(
                                                            deleteWarehouseMono, insertWarehouseFlux.then(),
                                                            deleteCategoryMono, insertCategoryFlux.then()
                                                    ).then(Mono.just(productId));
                                                })
                                                .flatMap(productIdSave -> bigIntegerMono(productIdSave, skuList, categoryIds, tagIds, product)
                                                )
                                                .onErrorResume(throwable -> Mono.error(new Exception("保存商品信息失败")));

                                    }

                            );
                })
                .transform(transactionalOperator::transactional)
                .onErrorResume(throwable -> {
                    log.error("保存失败：", throwable);
                    return Mono.error(new Exception("保存商品信息失败"));
                });
        /**
         * 删除
         * */
    }

    @NonNull
    private Mono<BigInteger> bigIntegerMono(BigInteger productIdSave, List<BigInteger> skuList, List<BigInteger> categoryIds, List<BigInteger> tagIds, Product product) {
        log.info("更新商品信息成功：{}", productIdSave);
        Mono<List<SKU>> skuFlux = Flux.fromIterable(skuList)
                .flatMap(skuRepository::findById)
                .collectList();
        Mono<List<Category>> categoryFlux = Flux.fromIterable(categoryIds)
                .flatMap(categoryRepository::findById)
                .collectList();
        Mono<List<Tag>> tagMono = tagRepository.findAllById(tagIds).collectList();
        return Mono.zip(skuFlux, categoryFlux, tagMono)
                .flatMap(tuple -> {
                    List<SKU> t1 = tuple.getT1();
                    List<Category> t2 = tuple.getT2();
                    List<Tag> t3 = tuple.getT3();
                    ProductForEmbeddingApVO productForEmbeddingApVO = ProductForEmbeddingApVO
                            .builder()
                            .id(productIdSave)
                            .skuList(
                                    t1.stream()
                                            .map(sku ->
                                                    ProductForEmbeddingApVO.SkuItem.builder()
                                                            .id(sku.getId().toString())
                                                            .name(sku.getName())
                                                            .price(sku.getPrice().toString())
                                                            .skuCode(sku.getSkuCode())
                                                            .build()
                                            )
                                            .collect(Collectors.toList())
                            )
                            .title(product.getName())
                            .tagNames(
                                    t3.stream()
                                            .map(Tag::getName)
                                            .collect(Collectors.toList())
                            )
                            .placeOfOrigin(product.getPlaceOfOrigin())
                            .categoryNames(
                                    t2.stream()
                                            .map(Category::getName)
                                            .collect(Collectors.toList())
                            )
                            .brand(product.getBrand())
                            .description(product.getDescription())
                            .build();
                    return aiChatClientRecommendServiceApi.embeddingProduct(
                                    List.of(productForEmbeddingApVO)
                            )
                            .thenReturn(productIdSave);
                });
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
        List<BigInteger>  categoryIds = condition.getCategoryId();
        //标签id
        List<BigInteger> tagIds = condition.getTagId();
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
            if (Objects.nonNull(tagIds)) {
                return productTagRepository.findByTagIds(tagIds)
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
//                            if (Objects.nonNull(categoryId)) {
//                                criteria = criteria.and(Product.Fields.categoryId).is(categoryId);
//                            }
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
            }
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
//                if (Objects.nonNull(categoryId)) {
//                    criteria = criteria.and(Product.Fields.categoryId).is(categoryId);
//                }
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
        });
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
                                                        .then(Mono.just(item))
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

    @Override
    public Mono<CursorPageResult<List<ProductVO>>> findCursorListProductVO(RequestCursorPage<ProductVO> requestCursorPage) {
        RequestCursorPage<Product> page = BeanConvertUtil.toBean(requestCursorPage, Product.class);
        return CursorQuery.of(r2dbcEntityTemplate, Product.class, page)

                .list()
                .collectList()
                .map(products -> {
                    // 判断是否有下一页
                    boolean hasNext = products.size() > requestCursorPage.getPageSize();
                    // 截取真实需要的数据
                    List<Product> data = hasNext ? products.subList(0, requestCursorPage.getPageSize()) : products;
                    List<ProductVO> toListProductVO = BeanConvertUtil.toBeanList(data, ProductVO.class);
                    BigInteger nextCursor = null;
                    if (!data.isEmpty()) {
                        Product lastProduct = data.getLast();
                        nextCursor = lastProduct.getId(); // 假设 getId() 返回主键
                    }
                    return CursorPageResult.<List<ProductVO>>builder()
                            .cursor(nextCursor)
                            .rows(toListProductVO)
                            .hasNext(hasNext)
                            .build();
                });
    }

}
