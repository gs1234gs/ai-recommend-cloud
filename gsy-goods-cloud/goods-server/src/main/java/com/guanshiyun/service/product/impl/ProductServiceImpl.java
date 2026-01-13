package com.guanshiyun.service.product.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.cursorQuery.CursorQuery;
import com.db.cursorQuery.ReactivePageQuery;
import com.db.page.PageUtils;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
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
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.lang.NonNull;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
                                .publishOn(Schedulers.boundedElastic())
                                .doOnSuccess(productId -> {
                                    // 异步调用 bigIntegerMono，但不阻塞主链
                                    bigIntegerMono(productId, skuList, categoryIds, tagIds, product)
                                            .subscribe(
                                                    ignored -> log.info("异步处理完成: {}", productId),
                                                    error -> log.error("异步处理失败: {}", productId, error)
                                            );
                                })
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
                                                .publishOn(Schedulers.boundedElastic())
                                                .doOnSuccess(OK -> {
                                                    // 异步调用 bigIntegerMono，但不阻塞主链
                                                    bigIntegerMono(productId, skuList, categoryIds, tagIds, product)
                                                            .subscribe(
                                                                    ignored -> log.info("异步处理完成: {}", productId),
                                                                    error -> log.error("异步处理失败: {}", productId, error)
                                                            );
                                                })
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
        Mono<List<SKU>> skuFlux = Flux.fromIterable(Objects.requireNonNullElse(skuList, Collections.emptyList()))
                .flatMap(skuRepository::findById)
                .collectList();
        Mono<List<Category>> categoryFlux = Flux.fromIterable(Objects.requireNonNullElse(categoryIds, Collections.emptyList()))
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
    }

    @Override
    public Mono<PageResultT<List<ProductVO>>> findPage(RequestPage<ProductVO> requestPage) {
        //校验参数
        RequestPage<ProductVO> recordRequestPage = PageUtils.pageValidation(requestPage, ProductVO.class);
        ProductVO condition = recordRequestPage.getCondition();
        RequestPage<Product> productRequestPage = BeanConvertUtil.toBean(recordRequestPage, Product.class);
        List<BigInteger> categoryIds = condition.getCategoryId();
        return Mono.deferContextual(ctx -> {
            BigInteger userId = myBigInteger
                    .bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
            if (Objects.isNull(userId))
                return Mono.just(PageResultT.<List<ProductVO>>builder()
                        .total(ConstNumber.INT_ZERO)
                        .rows(Collections.emptyList())
                        .build());
            if (Objects.nonNull(categoryIds) && !categoryIds.isEmpty()) {
                return productCategoryRepository.findByCategoryId(categoryIds.getFirst())
                        .map(ProductCategory::getProductId)
                        .collectList()
                        .flatMap(productIds -> ReactivePageQuery
                                .of(r2dbcEntityTemplate, Product.class, productRequestPage)
                                .like(Product.Fields.name, condition.getName())
                                .in(Product.Fields.id, productIds)
                                .page()
                                .map(pageResultT ->
                                        BeanConvertUtil.toBean(pageResultT, ProductVO.class)
                                )
                        );
            }
            return ReactivePageQuery
                    .of(r2dbcEntityTemplate, Product.class, productRequestPage)
                    .like(Product.Fields.name, condition.getName())
                    .page()
                    .map(pageResultT ->
                            BeanConvertUtil.toBean(pageResultT, ProductVO.class)
                    );
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
