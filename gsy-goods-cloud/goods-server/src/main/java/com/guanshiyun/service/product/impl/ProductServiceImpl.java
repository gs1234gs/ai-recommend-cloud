package com.guanshiyun.service.product.impl;

import com.db.cursorQuery.CursorQuery;
import com.db.cursorQuery.ReactiveQuery;
import com.db.page.PageUtils;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.guanshiyun.category.Category;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.controller.product.vo.ProductSaveVO;
import com.guanshiyun.controller.product.vo.ProductVO;
import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.items.Item;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.product.Product;
import com.guanshiyun.relationship.ProductCategory;
import com.guanshiyun.relationship.ProductTag;
import com.guanshiyun.repository.category.CategoryRepository;
import com.guanshiyun.repository.product.ProductRepository;
import com.guanshiyun.repository.relation.ProductCategoryRepository;
import com.guanshiyun.repository.relation.ProductTagRepository;
import com.guanshiyun.repository.sku.SKURepository;
import com.guanshiyun.repository.tag.TagRepository;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rowAffected.RowAffected;
import com.guanshiyun.rpc.chatrecommend.AiChatClientRecommendServiceApi;
import com.guanshiyun.service.product.ProductService;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final ProductRepository productRepository;
    private final MyLong myLong;
    private final DatabaseClient databaseClient;
    private final ProductTagRepository productTagRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TransactionalOperator transactionalOperator;
    private final SKURepository skuRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final AiChatClientRecommendServiceApi aiChatClientRecommendServiceApi;
    private final ReactiveQuery reactiveQuery;
//    private final GorseClient gorseClient;

    @Override
    public Mono<Long> saveProduct(ProductSaveVO productSaveVO) {
        Product product = BeanConvertUtil.toBean(productSaveVO, Product.class);
        List<Long> tagIds = productSaveVO.getTagId();
        LocalDateTime now = LocalDateTime.now();
        List<Long> categoryIds = productSaveVO.getCategoryId();

        // 参数校验提前做
        if (Objects.isNull(tagIds)) {
            return Mono.error(new RuntimeException("标签不存在"));
        }
        if (Objects.isNull(categoryIds)) {
            return Mono.error(new RuntimeException("分类不存在"));
        }

        return Mono.deferContextual(ctx -> {
                    Long userId = myLong.findUserId(ctx);
                    Long tenantId = myLong.findTenantId(ctx);
                    if (Objects.isNull(userId)) {
                        return Mono.error(new RuntimeException("用户不存在"));
                    }
                    //如果id为null则保存
                    if (Objects.isNull(product.getId())) {
                        // 新增商品
                        product.setCreateTime(now);
                        product.setCreator(userId);
                        product.setTenantId(tenantId);
                        return r2dbcEntityTemplate.insert(product)
                                .map(Product::getId)
                                .flatMap(productId -> {
                                    // 保存分类关系
                                    Flux<ProductCategory> categoryFlux = productCategoryRepository.saveAll(
                                            categoryIds.stream()
                                                    .map(categoryId ->
                                                            ProductCategory.builder()
                                                                    .productId(productId)
                                                                    .creator(userId)
                                                                    .createTime(now)
                                                                    .tenantId(tenantId)
                                                                    .categoryId(categoryId)
                                                                    .build()
                                                    )
                                                    .collect(Collectors.toList())
                                    );
                                    Flux<ProductTag> fluxTag = productTagRepository.saveAll(
                                            tagIds.stream()
                                                    .map(tagId ->
                                                            ProductTag.builder()
                                                                    .id(null)
                                                                    .productId(productId)
                                                                    .creator(userId)
                                                                    .createTime(now)
                                                                    .tenantId(tenantId)
                                                                    .tagId(tagId)
                                                                    .build()
                                                    )
                                                    .collect(Collectors.toList())
                                    );
                                    return Flux.merge(categoryFlux, fluxTag)
                                            .then(Mono.just(productId))
                                            .onErrorResume(throwable -> {
                                                log.error("保存商品分类关系失败：", throwable);
                                                return Mono.error(new Exception("保存商品分类关系失败"));
                                            });
                                })
                                .transform(transactionalOperator::transactional)
                                .publishOn(Schedulers.boundedElastic())
                                .doOnSuccess(productId -> {
                                    // 异步向量化处理（不阻塞主流程）
                                    embedding(productId, categoryIds, tagIds, product)
                                            .contextWrite(ctxb -> ctxb.put(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, userId))
                                            .subscribeOn(Schedulers.parallel())
                                            .subscribe(
                                                    ignored -> log.info("异步处理完成: {}", productId),
                                                    error -> log.error("异步处理失败: {}", productId, error)
                                            );
                                })
                                .onErrorResume(throwable -> {
                                    log.error("保存商品信息失败：", throwable);
                                    return Mono.error(new Exception("保存商品信息失败"));
                                });
                    } else {
                        // 更新商品
                        product.setUpdater(userId);
                        product.setUpdateTime(now);
                        return r2dbcUpdateHelper.updateIgnoreNull(
                                        Product.class,
                                        product,
                                        Product.Fields.id
                                )
                                .flatMap(productId -> {
                                    // 查询旧的分类关系
                                    Mono<List<Long>> tagIdsMono = productTagRepository
                                            .findTagIdByProductId(productId).collectList();
                                    Mono<List<Long>> categoryIdsMono = productCategoryRepository
                                            .findByProductId(productId)
                                            .map(ProductCategory::getCategoryId)
                                            .collectList();
                                    return Mono.zip(tagIdsMono, categoryIdsMono)
                                            .flatMap(tuple -> {
                                                List<Long> oldCategoryIds = tuple.getT2();
                                                List<Long> oldTagIds = tuple.getT1();
                                                Set<Long> oldCategorySet = new HashSet<>(oldCategoryIds);
                                                Set<Long> oldTagSet = new HashSet<>(oldTagIds);

                                                //  新增的分类：新ID中不在旧ID里的
                                                List<ProductCategory> toAddCategories = categoryIds.stream()
                                                        .filter(id -> !oldCategorySet.contains(id))
                                                        .map(id -> ProductCategory.builder()
                                                                .productId(productId)
                                                                .categoryId(id)
                                                                .creator(userId)
                                                                .updater(userId)
                                                                .tenantId(tenantId)
                                                                .createTime(now)
                                                                .build())
                                                        .collect(Collectors.toList());
                                                // 新增的标签：新ID中不在旧ID里的
                                                List<ProductTag> toAddTags = tagIds.stream()
                                                        .filter(id -> !oldTagSet.contains(id))
                                                        .map(id -> ProductTag.builder()
                                                                .productId(productId)
                                                                .tagId(id)
                                                                .creator(userId)
                                                                .updater(userId)
                                                                .tenantId(tenantId)
                                                                .createTime(now)
                                                                .build())
                                                        .collect(Collectors.toList());

                                                // 删除的分类：旧ID中不在新ID里的
                                                Set<Long> newCategorySet = new HashSet<>(categoryIds);
                                                List<Long> toDeleteCategories = oldCategoryIds.stream()
                                                        .filter(id -> !newCategorySet.contains(id))
                                                        .toList();
                                                // 删除的标签：旧ID中不在新ID里的
                                                Set<Long> newTagSet = new HashSet<>(tagIds);
                                                List<Long> toDeleteTags = oldTagIds.stream()
                                                        .filter(id -> !newTagSet.contains(id))
                                                        .toList();
                                                Mono<Void> deleteCategories = toDeleteCategories.isEmpty()
                                                        ? Mono.empty()
                                                        : productCategoryRepository.deleteAllByProductIds(toDeleteCategories);

                                                Mono<Void> deleteTags = toDeleteTags.isEmpty()
                                                        ? Mono.empty()
                                                        : productTagRepository.deleteAllByTagIds(toDeleteTags);
                                                Flux<ProductCategory> insertCategories = productCategoryRepository.saveAll(toAddCategories);
                                                Flux<ProductTag> insertTags = productTagRepository.saveAll(toAddTags);

                                                return Mono.when(
                                                        deleteCategories,
                                                        deleteTags,
                                                        insertCategories.then(),
                                                        insertTags.then()
                                                ).thenReturn(productId);
                                            });
                                })
                                .transform(transactionalOperator::transactional)
                                .publishOn(Schedulers.boundedElastic())
                                .doOnSuccess(productId -> {
                                    embedding(productId, categoryIds, tagIds, product)
                                            .contextWrite(ctxb -> ctxb.put(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, userId))
                                            .subscribeOn(Schedulers.parallel())
                                            .subscribe(
                                                    ignored -> log.info("异步处理完成: {}", productId),
                                                    error -> log.error("异步处理失败: {}", productId, error)
                                            );
                                })
                                .onErrorResume(throwable -> {
                                    log.error("更新商品信息失败：", throwable);
                                    return Mono.error(new Exception("更新商品信息失败"));
                                });
                    }
                })
                .onErrorResume(throwable -> {
                    log.error("保存商品整体失败：", throwable);
                    return Mono.error(new Exception("保存商品失败"));
                });
    }

    @NonNull
    private Mono<Long> embedding(Long productIdSave, List<Long> categoryIds, List<Long> tagIds, Product product) {
        log.info("更新商品信息成功：{}", productIdSave);

        if (Objects.isNull(productIdSave) || productIdSave.equals(ConstNumber.LONG_ZERO)) {
            log.warn("商品ID为空，跳过关联数据保存");
            return Mono.empty(); // 或抛异常，根据业务需求
        }

        // 移除了 skuFlux
        Mono<List<Category>> categoryFlux = Flux.fromIterable(Objects.requireNonNullElse(categoryIds, Collections.emptyList()))
                .flatMap(categoryRepository::findById)
                .collectList();
        Mono<List<Tag>> tagMono = tagRepository.findAllById(tagIds).collectList();

        return Mono.zip(categoryFlux, tagMono)
                .flatMap(tuple -> {
                    List<Category> categories = tuple.getT1();
                    List<Tag> tags = tuple.getT2();

                    ProductForEmbeddingApVO productForEmbeddingApVO = ProductForEmbeddingApVO
                            .builder()
                            .id(productIdSave)
                            .title(product.getName())
                            .tagNames(
                                    tags.stream()
                                            .map(Tag::getName)
                                            .collect(Collectors.toList())
                            )
                            .placeOfOrigin(product.getPlaceOfOrigin())
                            .categoryNames(
                                    categories.stream()
                                            .map(Category::getName)
                                            .collect(Collectors.toList())
                            )
                            .brand(product.getBrand())
                            .description(product.getDescription())
                            // 注意：不再设置 skuList
                            .build();
                    // 拼接 comment：用于调外部 embedding
                    String comment = Stream.of(
                                    product.getName(),
                                    product.getBrand(),
                                    product.getDescription(),
                                    product.getPlaceOfOrigin()
                            ).filter(Objects::nonNull)
                            .filter(s -> !s.trim().isEmpty())
                            .collect(Collectors.joining(" | "));
                    // Labels：标签名列表
                    List<String> labelList = tags.stream()
                            .map(Tag::getName)
                            .filter(Objects::nonNull)
                            .toList();
                    // Categories：类目名列表（从粗到细）
                    List<String> categoryList = categories.stream()
                            .map(Category::getName)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    if (categoryList.isEmpty()) {
                        return Mono.error(new Throwable("类目列表为空"));
                    }
                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
                    ZonedDateTime time = ZonedDateTime.now(ZoneId.of("+08:00"));
                    String timestamp = time.format(outputFormatter);
                    Item gorseItem = Item.builder()
                            .itemId(productIdSave.toString())
                            .isHidden(Boolean.FALSE) // 根据业务逻辑可动态设置
                            .labels(Map.of("topics", labelList))      // 注意：Item.labels 是 Object，但传 List<String>
                            .categories(categoryList)
                            .timestamp(timestamp)
                            .comment(comment)
                            .build();
                    Mono<RowAffected> saveMono = aiChatClientRecommendServiceApi.gorse(gorseItem).map(ResultT::getData);
                    Mono<ResultT<List<String>>> embedMono =
                            aiChatClientRecommendServiceApi.embeddingProduct(
                                    List.of(productForEmbeddingApVO)
                            );
                    return Mono.zip(saveMono, embedMono)
                            .thenReturn(productIdSave)
                            .onErrorResume(ex -> {
                                log.warn("保存 Gorse 商品失败，但继续主流程", ex);
                                return Mono.just(productIdSave);
                            });
                });
    }

    @Override
    public Mono<Long> deleteById(Long id) {
        return databaseClient.sql("delete from product where id = :id")
                .bind(Product.Fields.id, id)
                .fetch()
                .rowsUpdated()
                //删除商品标签
                .flatMap(deleteCount ->
                        productTagRepository.deleteAllByProductId(id)
                                .thenReturn(deleteCount)
                )
                .flatMap(deleteCount ->
                        skuRepository.deleteAllByProductId(id)
                                .thenReturn(deleteCount)
                )
                .flatMap(deleteCount ->
                        productCategoryRepository.deleteByProductId(id)
                                .thenReturn(deleteCount)
                )
                .flatMap(count -> {
                    //删除ai
                    return aiChatClientRecommendServiceApi.
                            embeddingDeleteProduct(id)
                            .thenReturn(count);
                })
                .flatMap(count -> aiChatClientRecommendServiceApi.deleteGorse(id.toString())
                        .thenReturn(count))
                //事务
                .transform(transactionalOperator::transactional);
    }

    @Override
    public Mono<PageResultT<List<ProductVO>>> findPage(RequestPage<ProductVO> requestPage) {
        //校验参数
        RequestPage<ProductVO> recordRequestPage = PageUtils.pageValidation(requestPage, ProductVO.class);
        ProductVO condition = recordRequestPage.getCondition();
        RequestPage<Product> productRequestPage = BeanConvertUtil.toBean(recordRequestPage, Product.class);
        List<Long> categoryIds = condition.getCategoryId();
        return Mono.deferContextual(ctx -> {
            Long userId = myLong.findUserId(ctx);
            Long tenantId = myLong.findTenantId(ctx);
            if (Objects.isNull(userId))
                return Mono.error(new Throwable("用户未登录"));
            if (Objects.nonNull(categoryIds) && !categoryIds.isEmpty()) {
                return productCategoryRepository.findByCategoryId(categoryIds.getFirst())
                        .map(ProductCategory::getProductId)
                        .collectList()
                        .flatMap(productIds -> reactiveQuery
                                .createQuery(Product.class, productRequestPage)
                                .like(Product.Fields.name, condition.getName())
                                .in(Product.Fields.id, productIds)
                                .eq(Product.Fields.tenantId, tenantId)
                                .page()
                                .map(pageResultT ->
                                        BeanConvertUtil.toBean(pageResultT, ProductVO.class)
                                )
                        );
            }
            return reactiveQuery
                    .createQuery(Product.class, productRequestPage)
                    .like(Product.Fields.name, condition.getName())
                    .eq(Product.Fields.tenantId, tenantId)
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
            Long userId = myLong.findUserId(ctx);
            Long tenantId = myLong.findTenantId(ctx);
            if (Objects.isNull(userId))
                return Mono.error(new Throwable("用户未登录"));
            return Flux.fromIterable(productSaveVOList)
                    .concatMap(productSaveVO -> {
                        Product product = BeanConvertUtil.toBean(productSaveVO, Product.class);
                        product.setTenantId(tenantId);
                        return Objects.isNull(productSaveVO.getId()) ?
                                r2dbcEntityTemplate.insert(Product.class)
                                        .using(product)
                                :
                                r2dbcUpdateHelper.updateIgnoreNull(
                                        Product.class,
                                        product,
                                        Product.Fields.id
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
    public Mono<Void> deleteAllById(List<Long> ids) {
        return productRepository.deleteAllById(ids);
    }

    @Override
    public Mono<ProductVO> findById(Long id) {
        return productRepository.findById(id)
                .flatMap(product -> productCategoryRepository.findByProductId(id)
                        .map(ProductCategory::getCategoryId)
                        .collectList()
                        .flatMap(categoryIdList -> {
                            Mono<List<Tag>> tagNameMono = productTagRepository.findTagIdByProductId(id)
                                    .collectList()
                                    .flatMap(tadIds -> tagRepository.findAllById(tadIds)
                                            .collectList());
                            Mono<List<String>> categoryNameMono = categoryRepository.findAllById(categoryIdList)
                                    .map(Category::getName)
                                    .collectList();
                            return Mono.zip(tagNameMono, categoryNameMono)
                                    .map(tuple -> {
                                        List<Tag> tags = tuple.getT1();
                                        List<String> categoryNames = tuple.getT2();
                                        List<String> tagNames =
                                                tags.stream().map(Tag::getName)
                                                        .collect(Collectors.toList());
                                        List<Long> tagIds = tags.stream().map(Tag::getId)
                                                .toList();
                                        ProductVO productVO = BeanConvertUtil.toBean(product, ProductVO.class);
                                        productVO.setCategoryId(categoryIdList)
                                                .setTagId(tagIds)
                                                .setCategoryName(categoryNames)
                                                .setTagNames(tagNames);
                                        return productVO;
                                    });
                        }));
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
                    Long nextCursor = null;
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
