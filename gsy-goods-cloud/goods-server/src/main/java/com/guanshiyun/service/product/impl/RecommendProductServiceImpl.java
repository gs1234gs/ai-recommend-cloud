package com.guanshiyun.service.product.impl;

import com.db.cursorQuery.CursorQuery;
import com.db.dbnumber.ConstNumber;
import com.db.page.CursorPageUtil;
import com.db.query.SafeCriteria;
import com.guanshiyun.behaviorenums.GuestEnum;
import com.guanshiyun.controller.product.vo.ProductCustomerDetailVO;
import com.guanshiyun.controller.product.vo.ProductCustomerVO;
import com.guanshiyun.controller.product.vo.ProductSearchSaveVO;
import com.guanshiyun.controller.sku.vo.SKUVO;
import com.guanshiyun.controller.tag.vo.TagVO;
import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.embedding.RequestBodyProductForEmbeddingApVO;
import com.guanshiyun.gorseenum.GorseFeedbackEnum;
import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.product.Product;
import com.guanshiyun.profile.CategoryApiVO;
import com.guanshiyun.profile.ProductApiVO;
import com.guanshiyun.profile.SKUApiVO;
import com.guanshiyun.profile.TagApiVO;
import com.guanshiyun.reactiveredis.ReactiveRedisUtil;
import com.guanshiyun.relationship.ProductCategory;
import com.guanshiyun.repository.category.CategoryRepository;
import com.guanshiyun.repository.product.ProductRepository;
import com.guanshiyun.repository.relation.ProductCategoryRepository;
import com.guanshiyun.repository.sku.SKURepository;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.apisave.UserClickSaveApiVO;
import com.guanshiyun.rpc.behaviorapi.browse.UserBrowseServiceApi;
import com.guanshiyun.rpc.behaviorapi.click.UserClickServiceApi;
import com.guanshiyun.rpc.behaviorapi.collect.UserCollectServiceApi;
import com.guanshiyun.rpc.behaviorapi.search.UserSearchServiceApi;
import com.guanshiyun.rpc.chatrecommend.AiChatClientRecommendServiceApi;
import com.guanshiyun.rpc.order.PurchaseOrderServiceApi;
import com.guanshiyun.rpc.order.vo.PurchaseOrderVOApi;
import com.guanshiyun.rpc.profile.BrowseProfileApi;
import com.guanshiyun.rpc.profile.ClickProfileApi;
import com.guanshiyun.rpc.profile.CollectProfileApi;
import com.guanshiyun.rpc.profile.SearchContentApi;
import com.guanshiyun.service.product.RecommendProductService;
import com.guanshiyun.service.utils.UtilsService;
import com.guanshiyun.sku.SKU;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 商品推荐服务实现类。
 * <p>
 * 提供基于用户行为（点击、收藏、搜索）和 Gorse 协同过滤的个性化商品推荐逻辑，
 * 并支持分页查询、详情获取等基础商品服务。
 * </p>
 *
 * @author guanshiyun
 * @since 2025-12-20 10:13
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendProductServiceImpl implements RecommendProductService {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final GorseClient gorseClient;
    private final AiChatClientRecommendServiceApi aiChatClientRecommendServiceApi;
    private final UserClickServiceApi userClickServiceApi;
    private final UserCollectServiceApi userCollectServiceApi;
    private final UserSearchServiceApi userSearchServiceApi;
    private final PurchaseOrderServiceApi purchaseOrderServiceApi;
    private final UserBrowseServiceApi userBrowseServiceApi;
    private final ProductRepository productRepository;
    private final MyLong myLong;
    private final UtilsService utilsService;
    private final SKURepository sKURepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ReactiveRedisUtil reactiveRedisUtil;


    /**
     * 根据搜索条件分页查询商品列表（仅限已登录用户）。
     * <p>
     * 支持按商品名称模糊匹配、价格区间筛选，并记录用户的搜索行为。
     * 使用游标分页（cursor-based pagination），避免传统 offset 性能问题。
     * </p>
     *
     * @param requestCursorPage 分页请求对象，包含搜索条件、最后 ID、每页数量、排序方式
     * @return {@link Mono} 包含分页结果 {@link CursorPageResult<List<ProductCustomerVO>>}
     * @author guanshiyun
     * @since 2025-12-20 10:13
     */
    @Override
    public Mono<CursorPageResult<List<ProductCustomerVO>>> searchProduct(
            RequestCursorPage<ProductSearchSaveVO> requestCursorPage) {

        RequestCursorPage<ProductSearchSaveVO> validate = CursorPageUtil.validate(requestCursorPage, ProductSearchSaveVO.class);
        ProductSearchSaveVO condition = validate.getCondition();
        String searchContent = condition.getSearchContent();

        return Mono.deferContextual(ctx -> {

            // 登录校验
            if (!myLong.hasKey(ctx)) {
                //先走gorse
                return gorseClient.getRecommend(GuestEnum.GUEST_USER_ID.getValue(), 20)
                        .map(Flux::fromIterable)
                        .flatMapMany(Function.identity())
                        .mapNotNull(myLong::longOrNull)
                        .collectList()
                        .flatMap(ids ->
                                buildResultFromAiProductIds(ids, validate, condition)
                        )
                        .onErrorResume(e -> {
                            log.error("gorse", e);
                            return Mono.just(
                                    CursorPageResult.<List<ProductCustomerVO>>builder()
                                            .rows(Collections.emptyList())
                                            .cursor(ConstNumber.LONG_ZERO)
                                            .hasNext(false)
                                            .build()
                            );
                        });
            }
            // 如果没有搜索内容，直接走传统查询（AI 不适用）
            Long userId = myLong.findUserId(ctx);
            if (!StringUtils.hasText(searchContent)) {
                //先走gorse
                return gorseClient.getRecommend(userId.toString(), 20)
                        .map(Flux::fromIterable)
                        .flatMapMany(Function.identity())
                        .mapNotNull(myLong::longOrNull)
                        .collectList()
                        .flatMap(ids ->
                                buildResultFromAiProductIds(ids, validate, condition)
                        )
                        .onErrorResume(e -> {
                            log.error("gorse", e);
                            return Mono.just(
                                    CursorPageResult.<List<ProductCustomerVO>>builder()
                                            .rows(Collections.emptyList())
                                            .cursor(ConstNumber.LONG_ZERO)
                                            .hasNext(false)
                                            .build()
                            );
                        });
            }
            // 优先使用 AI 推荐的商品 ID 列表
            return aiChatClientRecommendServiceApi
                    .searchByKeyword(searchContent.trim(), 20) // 获取最多 20 个候选 ID
                    .map(result -> {
                                List<Long> data = result.getData();
                                log.info("AI 搜索服务返回结果：{}", result);
                                return data;
                            }
                    )
                    .filter(Objects::nonNull)
                    .flatMapMany(Flux::fromIterable)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collectList()
                    .onErrorResume(e -> {
                        log.warn("AI 搜索服务异常，回退到传统数据库查询。关键词: {}", searchContent, e);
                        return Mono.empty(); // 触发 switchIfEmpty
                    })
                    .flatMap(aiProductIds -> {
                        if (aiProductIds.isEmpty()) {
                            return executeTraditionalQuery(validate, condition);
                        }
                        return buildResultFromAiProductIds(aiProductIds, validate, condition);
                    })
                    .switchIfEmpty(Mono.defer(() -> executeTraditionalQuery(validate, condition)))
                    .doOnSuccess(result -> {
                        // 异步记录搜索行为（无论 AI 还是传统）
                        if (StringUtils.hasText(searchContent)) {
                            userSearchServiceApi.saveUserSearchRecord(
                                            BeanConvertUtil.toBean(condition, SearchContentApi.class)
                                                    .setSearchTime(LocalDateTime.now())
                                    )
                                    .contextWrite(ctxs -> ctxs.put(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, userId))
                                    .subscribeOn(Schedulers.boundedElastic()).subscribe();
                        }
                    });
        });
    }

// ------------------ 辅助方法 ------------------

    /**
     * 执行传统数据库查询（基于 name 模糊 + 价格区间 + 游标分页）
     */
    private Mono<CursorPageResult<List<ProductCustomerVO>>> executeTraditionalQuery(
            RequestCursorPage<ProductSearchSaveVO> validate,
            ProductSearchSaveVO condition) {

        Long lastId = validate.getLastId();
        Integer pageSize = validate.getPageSize();
        BigDecimal maxPrice = condition.getMaxPrice();
        BigDecimal minPrice = condition.getMinPrice();
        String searchContent = condition.getSearchContent();

        SafeCriteria safeCriteria = SafeCriteria.safeCriteria();
        Criteria criteria = safeCriteria
                .likeIfNotEmpty(Product.Fields.name, searchContent)
                .geIfNotNull(Product.Fields.maxPrice, minPrice)
                .leIfNotNull(Product.Fields.minPrice, maxPrice)
                .gtIfNotNull(Product.Fields.id, lastId)
                .criteria();

        Query query = Query.query(criteria)
                .limit(pageSize + 1) // 多查一条判断 hasNext
                .sort(Sort.by(Sort.Order.asc(Product.Fields.id)));

        return r2dbcEntityTemplate
                .select(Product.class)
                .matching(query)
                .all()
                .map(ProductCustomerVO::toVO)
                .collectList()
                .map(list -> {
                    boolean hasNext = list.size() > pageSize;
                    List<ProductCustomerVO> rows = hasNext ? list.subList(0, pageSize) : list;
                    Long cursor = rows.isEmpty() ? ConstNumber.LONG_ZERO : rows.getLast().getId();
                    return CursorPageResult.<List<ProductCustomerVO>>builder()
                            .rows(rows)
                            .cursor(cursor)
                            .hasNext(hasNext)
                            .build();
                });
    }

    /**
     * 根据 AI 返回的商品 ID 列表 + 游标分页参数，构建最终结果
     */
    private Mono<CursorPageResult<List<ProductCustomerVO>>> buildResultFromAiProductIds(
            List<Long> aiProductIds,
            RequestCursorPage<ProductSearchSaveVO> validate,
            ProductSearchSaveVO condition) {

        if (aiProductIds.isEmpty()) {
            return Mono.just(
                    CursorPageResult.<List<ProductCustomerVO>>builder()
                            .rows(Collections.emptyList())
                            .cursor(ConstNumber.LONG_ZERO)
                            .hasNext(false)
                            .build()
            );
        }

        Long lastId = validate.getLastId();
        Integer pageSize = validate.getPageSize();
        BigDecimal maxPrice = condition.getMaxPrice();
        BigDecimal minPrice = condition.getMinPrice();
        int startIndex = 0;
        if (lastId != null) {
            for (int i = 0; i < aiProductIds.size(); i++) {
                if (aiProductIds.get(i).equals(lastId)) {
                    startIndex = i + 1;
                    break;
                }
            }
        }
        // 过滤掉 <= lastId 的 ID（游标分页要求）
        List<Long> filteredIds = aiProductIds.stream()
                .skip(startIndex)
                .limit(pageSize + 1L)
                .toList();

        if (filteredIds.isEmpty()) {
            return Mono.just(
                    CursorPageResult.<List<ProductCustomerVO>>builder()
                            .rows(Collections.emptyList())
                            .cursor(ConstNumber.LONG_ZERO)
                            .hasNext(false)
                            .build()
            );
        }

        // 查询这些 ID 对应的商品详情（带价格过滤）
        SafeCriteria safeCriteria = SafeCriteria.safeCriteria();
        Criteria criteria = safeCriteria
                .in(Product.Fields.id, filteredIds)
                .geIfNotNull(Product.Fields.maxPrice, minPrice)
                .leIfNotNull(Product.Fields.minPrice, maxPrice)
                .criteria();

        Query query = Query.query(criteria);
//                .sort(Sort.by(Sort.Order.asc(Product.Fields.id)));

        return r2dbcEntityTemplate
                .select(Product.class)
                .matching(query)
                .all()
                .map(ProductCustomerVO::toVO)
                .collectList()
                .map(list -> {
                    // 按 filteredIds 顺序排序（保持 AI 推荐顺序）
                    Map<Long, ProductCustomerVO> map = list.stream()
                            .collect(Collectors.toMap(ProductCustomerVO::getId, Function.identity()));
                    List<ProductCustomerVO> ordered = filteredIds.stream()
                            .map(map::get)
                            .filter(Objects::nonNull)
                            .toList();

                    boolean hasNext = ordered.size() > pageSize;
                    List<ProductCustomerVO> rows = hasNext ? ordered.subList(0, pageSize) : ordered;
                    Long cursor = rows.isEmpty() ? ConstNumber.LONG_ZERO : rows.getLast().getId();
                    return CursorPageResult.<List<ProductCustomerVO>>builder()
                            .rows(rows)
                            .cursor(cursor)
                            .hasNext(hasNext)
                            .build();
                });
    }


    /**
     * 基于 Gorse 协同过滤引擎获取用户喜欢的商品推荐。
     * <p>
     * 直接调用 Gorse 的推荐接口，获取用户 ID 对应的推荐商品 ID 列表，
     * 再从数据库加载完整商品信息返回。
     * </p>
     *
     * @return {@link Mono<List<ProductCustomerVO>>} 推荐的商品列表
     * @author guanshiyun
     * @since 2025-12-20 10:13
     */
    @Override
    public Mono<List<ProductCustomerVO>> like() {
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new Exception("请先登陆"));
            }
            Long userId = myLong.findUserId(ctx);

            // 封装默认返回方法
            Supplier<Mono<List<ProductCustomerVO>>> defaultProducts = () ->
                    productRepository.findAll()
                            .take(20)
                            .map(ProductCustomerVO::toVO)
                            .collectList();

            return gorseClient.getRecommend(userId.toString(), 20)
                    .flatMap(productIds -> {
                        List<Long> productIdList = productIds.stream()
                                .filter(id -> StringUtils.hasText(id) && id.matches("\\d+"))
                                .map(myLong::myLong)
                                .toList();

                        if (productIdList.isEmpty()) {
                            return defaultProducts.get();
                        }

                        return productRepository.findAllById(productIdList)
                                .collectList()
                                .flatMap(list -> {
                                    if (list.isEmpty()) {
                                        return defaultProducts.get();
                                    }
                                    // 保持 AI 推荐顺序
                                    Map<Long, Product> map = list.stream()
                                            .collect(Collectors.toMap(Product::getId, Function.identity()));

                                    List<ProductCustomerVO> ordered = productIdList.stream()
                                            .map(map::get)
                                            .filter(Objects::nonNull)
                                            .map(ProductCustomerVO::toVO)
                                            .toList();
                                    return Mono.just(ordered);
                                });
                    });
        });
    }

    /**
     * 获取用户喜欢的商品推荐 (支持分页下拉)
     *
     * @param offset 偏移量 (前端传递，第一页为 0)
     * @param limit  每页数量 (前端传递，例如 20)
     * @return 推荐商品列表
     */
    @Override
    public Mono<List<ProductCustomerVO>> likePool(Integer offset, int limit, Boolean refresh) {
        return Mono.deferContextual(ctx -> {
            // 1. 用户鉴权
            if (!myLong.hasKey(ctx)) {
                log.info("用户未登录，返回热门商品兜底");
                return getFallbackProducts(limit);
            }

            Long userId = myLong.findUserId(ctx);
            String redisKey = ProductKey.REC_LIKE_KEY_PREFIX + userId;

            // 如果 refresh 为 true，强制重建候选池
            if (Boolean.TRUE.equals(refresh)) {
                log.info("用户 {} 请求刷新推荐列表，强制重建候选池...", userId);
                return rebuildCandidatePool(userId, redisKey, limit)
                        .flatMap(this::loadProductsAndSort);
            }

            // 2. 尝试从 Redis 获取当前页的 ID 列表 (非刷新模式)
            long start = offset;
            long end = offset + limit - 1;

            return reactiveRedisUtil.lRange(redisKey, start, end)
                    .flatMap(pageIds -> {
                        // 3. 处理空数据情况
                        if (pageIds == null || pageIds.isEmpty()) {
                            if (offset == 0) {
                                // 第一页为空 -> 说明缓存未建立或已过期，需要重新生成候选池
                                log.info("用户 {} 推荐缓存缺失，正在重建候选池...", userId);
                                return rebuildCandidatePool(userId, redisKey, limit)
                                        .flatMap(this::loadProductsAndSort);
                            } else {
                                // 非第一页为空 -> 说明没有更多数据了
                                log.info("用户 {} 推荐数据已加载完毕 (offset={})", userId, offset);
                                return Mono.just(Collections.<ProductCustomerVO>emptyList());
                            }
                        }

                        // 4. 正常情况：根据 Redis 中的 ID 查库
                        return loadProductsAndSort(pageIds);
                    })
                    .onErrorResume(e -> {
                        log.error("推荐服务异常，降级返回热门商品", e);
                        return getFallbackProducts(limit);
                    });
        });
    }

    /**
     * 重建候选池：调用 Gorse -> 写入 Redis -> 返回第一页数据
     */
    private Mono<List<String>> rebuildCandidatePool(Long userId, String redisKey, int limit) {
        return gorseClient.getRecommend(userId.toString(), ProductKey.PRE_LOAD_SIZE)
                .flatMap(productIds -> {
                    // 过滤非法 ID
                    List<String> validIds = productIds.stream()
                            .filter(id -> StringUtils.hasText(id) && id.matches("\\d+"))
                            .toList();

                    if (validIds.isEmpty()) {
                        log.warn("Gorse 返回空推荐列表，用户：{}", userId);
                        return Mono.empty();
                    }

                    // 写入 Redis 并设置过期时间
                    return reactiveRedisUtil.refreshList(redisKey, validIds)
                            .then(reactiveRedisUtil.expire(redisKey, ProductKey.PAGE_SIZE))
                            .thenReturn(validIds.subList(0, Math.min(limit, validIds.size()))); // 只返回第一页需要的 ID
                })
                .onErrorResume(e -> {
                    log.error("调用 Gorse 重建候选池失败", e);
                    return Mono.empty();
                });
    }

    /**
     * 根据 ID 列表加载商品，并严格保持 ID 列表的顺序
     */
    private Mono<List<ProductCustomerVO>> loadProductsAndSort(List<String> idStrings) {
        if (idStrings == null || idStrings.isEmpty()) {
            return Mono.just(Collections.emptyList());
        }

        List<Long> productIdList = idStrings.stream()
                .map(myLong::myLong)
                .toList();

        return productRepository.findAllById(productIdList)
                .collectList()
                .map(dbList -> {
                    // 构建 Map 以便快速查找
                    Map<Long, Product> productMap = dbList.stream()
                            .collect(Collectors.toMap(Product::getId, Function.identity()));

                    // 按照原始 ID 顺序重组，过滤掉数据库中不存在的商品（如下架商品）
                    return productIdList.stream()
                            .map(productMap::get)
                            .filter(Objects::nonNull)
                            .map(ProductCustomerVO::toVO)
                            .toList();
                });
    }

    /**
     * 兜底策略：返回热门商品
     */
    private Mono<List<ProductCustomerVO>> getFallbackProducts(int limit) {
        return productRepository.findAll(Sort.by(Sort.Direction.DESC, Product.Fields.publishTime))// 假设 repository 有此方法
                .take(limit)
                .map(ProductCustomerVO::toVO)
                .collectList();
    }

    /**
     * 获取商品详情，包括关联的标签和 SKU 列表。
     * <p>
     * 同时在成功获取后异步记录用户的点击行为。
     * </p>
     *
     * @param id 商品 ID
     * @return {@link Mono<ProductCustomerDetailVO>} 商品详情视图对象
     * @author guanshiyun
     * @since 2025-12-20 10:13
     */
    @Override
    public Mono<ProductCustomerDetailVO> detail(Long id) {
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new Exception("请先登陆"));
            }
            Mono<Product> productMono = productRepository.findById(id);
            Mono<List<TagVO>> tagListMono = utilsService.findTagByProductId(id);
            Mono<List<SKU>> skuListMono = sKURepository.findAllByProductId(id).collectList();
            Long userId = myLong.findUserId(ctx);
            return Mono.zip(productMono, tagListMono, skuListMono)
                    .flatMap(tuple -> {
                        Product product = tuple.getT1();
                        List<TagVO> tagList = tuple.getT2();
                        List<SKU> skuList = tuple.getT3();
                        ProductCustomerDetailVO detailVO = ProductCustomerDetailVO.builder()
                                .id(product.getId())
                                .level(product.getLevel())
                                .name(product.getName())
                                .maxPrice(product.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                                .minPrice(product.getMinPrice().setScale(2, RoundingMode.HALF_UP))
                                .offlineTime(product.getOfflineTime())
                                .placeOfOrigin(product.getPlaceOfOrigin())
                                .publishTime(product.getPublishTime())
                                .brand(product.getBrand())
                                .status(product.getStatus())
                                .tagList(tagList)
                                .originalPrice(product.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                                .discountPrice(
                                        Optional.ofNullable(product.getMinPrice())
                                                .map(price -> price.multiply(new BigDecimal("0.7")))
                                                .map(price -> price.setScale(2, RoundingMode.HALF_UP))
                                                .orElse(BigDecimal.ZERO)
                                )
                                .skuList(BeanConvertUtil.toBeanList(skuList, SKUVO.class))
                                .build();
                        return Mono.just(
                                        detailVO
                                )
                                .publishOn(Schedulers.boundedElastic())
                                .doOnSuccess(ok -> {
                                    // 异步记录点击行为
                                    userClickServiceApi.saveUserClickRecord(UserClickSaveApiVO
                                                    .builder()
                                                    .product(BeanConvertUtil.toBean(product, ProductApiVO.class))
                                                    .clickTime(LocalDateTime.now())
                                                    .build())
                                            .contextWrite(ctxs -> ctxs.put(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, userId))
                                            .subscribe(
                                                    o -> log.info("记录用户点击行为成功"),
                                                    e -> log.error("记录用户点击行为失败", e)
                                            );
                                })
                                .onErrorResume(e -> {
                                    log.error("获取商品详情失败", e);
                                    return Mono.error(e);
                                });
                    })
                    .onErrorResume(e -> {
                        log.error("获取商品详情失败", e);
                        return Mono.error(e);
                    });
        });
    }

    @Override
    public Mono<List<ProductCustomerVO>> findByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Mono.just(Collections.emptyList());
        }
        return productRepository.findAllById(ids)
                .map(ProductCustomerVO::toVO)
                .collectList();
    }

    /**
     * 获取购买记录并融合商品信息和分类信息
     * 购买记录从远程API获取，商品和分类信息从本地数据库查询，然后组合
     */
    private Mono<List<PurchaseOrderVOApi>> findPurchaseOrdersWithProductInfo() {
        return purchaseOrderServiceApi.findByRows(ConstNumber.INT_TWO)
                .flatMap(r -> {
                    List<PurchaseOrderVOApi> purchaseOrderVOApis = Optional.ofNullable(r.getData()).orElse(new ArrayList<>());
                    if (purchaseOrderVOApis.isEmpty()) {
                        return Mono.just(new ArrayList<PurchaseOrderVOApi>());
                    }

                    // 提取所有商品ID（去重）
                    List<Long> productIds = purchaseOrderVOApis.stream()
                            .map(PurchaseOrderVOApi::getProductId)
                            .filter(Objects::nonNull)
                            .distinct()
                            .toList();

                    if (productIds.isEmpty()) {
                        return Mono.just(purchaseOrderVOApis);
                    }

                    // 查询商品信息
                    Mono<List<Product>> productMono = productRepository.findAllById(productIds)
                            .collectList();

                    // 查询商品 - 分类关联关系
                    Mono<List<ProductCategory>> productCategoryMono = productCategoryRepository.findByProductIdIn(productIds)
                            .collectList();

                    // 同时获取商品和商品 - 分类关联关系
                    return Mono.zip(productMono, productCategoryMono)
                            .flatMap(tuple -> {
                                List<Product> products = tuple.getT1();
                                List<ProductCategory> productCategories = tuple.getT2();

                                // 提取所有分类ID
                                List<Long> categoryIds = productCategories.stream()
                                        .map(ProductCategory::getCategoryId)
                                        .filter(Objects::nonNull)
                                        .distinct()
                                        .toList();

                                if (categoryIds.isEmpty()) {
                                    // 没有分类信息，只融合商品信息
                                    return Mono.just(combineProductAndOrder(purchaseOrderVOApis, products, new ArrayList<>()));
                                }

                                // 查询分类详情
                                return categoryRepository.findAllById(categoryIds)
                                        .map(category -> BeanConvertUtil.toBean(category, CategoryApiVO.class))
                                        .collectList()
                                        .map(categories -> combineProductAndOrder(purchaseOrderVOApis, products, productCategories, categories));
                            });
                })
                .onErrorResume(e -> {
                    log.warn("获取购买记录融合商品信息失败", e);
                    return Mono.just(new ArrayList<PurchaseOrderVOApi>());
                });
    }

    /**
     * 融合购买记录、商品信息（无分类）
     */
    private List<PurchaseOrderVOApi> combineProductAndOrder(List<PurchaseOrderVOApi> orders,
                                                            List<Product> products,
                                                            List<CategoryApiVO> categories) {
        return combineProductAndOrder(orders, products, new ArrayList<>(), categories);
    }

    /**
     * 融合购买记录、商品信息和分类信息
     */
    private List<PurchaseOrderVOApi> combineProductAndOrder(List<PurchaseOrderVOApi> orders,
                                                            List<Product> products,
                                                            List<ProductCategory> productCategories,
                                                            List<CategoryApiVO> categories) {

        // 构建商品ID到商品对象的映射
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity(), (v1, v2) -> v1));

        // 构建商品ID到分类列表的映射
        Map<Long, List<CategoryApiVO>> productCategoryMap = productCategories.stream()
                .collect(Collectors.groupingBy(
                        ProductCategory::getProductId,
                        Collectors.mapping(
                                pc -> categories.stream()
                                        .filter(c -> c.getId().equals(pc.getCategoryId()))
                                        .findFirst()
                                        .orElse(null),
                                Collectors.filtering(Objects::nonNull, Collectors.toList())
                        )
                ));

        // 融合购买记录、商品信息和分类信息
        return orders.stream()
                .peek(order -> {
                    Long productId = order.getProductId();

                    // 融合商品信息
                    Product product = productMap.get(productId);
                    if (product != null) {
                        order.setName(product.getName());
                        order.setImage(product.getImage());
                    }

                    // 融合分类信息
                    List<CategoryApiVO> categoryList = productCategoryMap.get(productId);
                    if (categoryList != null && !categoryList.isEmpty()) {
                        order.setCategoryApiVOList(categoryList);
                    }
                })
                .toList();
    }

    /**
     * 从点击记录构建推荐输入对象
     */
    private ProductForEmbeddingApVO buildProductForEmbeddingFromClick(ClickProfileApi clickProfileApi,
                                                                      Map<Long, Double> ratioMap) {
        Long productId = clickProfileApi.getProduct().getId();

        // 则默认给 1.0 (最高权重)
        Double score = 1.0;
        if (ratioMap != null && ratioMap.containsKey(productId)) {
            score = ratioMap.get(productId);
        }
        return ProductForEmbeddingApVO.builder()
                .id(productId)
                .title(clickProfileApi.getProduct().getName())
                .brand(clickProfileApi.getProduct().getBrand())
                .score(score)
                .tagNames(clickProfileApi.getTagList().stream()
                        .map(TagApiVO::getName)
                        .collect(Collectors.toList()))
                .skuList(clickProfileApi.getSkuList().stream()
                        .map(skuApiVO -> ProductForEmbeddingApVO.SkuItem.builder()
                                .name(skuApiVO.getName())
                                .price(skuApiVO.getPrice().toString())
                                .id(skuApiVO.getId().toString())
                                .skuCode(skuApiVO.getSkuCode())
                                .build())
                        .collect(Collectors.toList()))
                .categoryNames(clickProfileApi.getCategoryList().stream()
                        .map(CategoryApiVO::getName)
                        .collect(Collectors.toList()))
                .placeOfOrigin(clickProfileApi.getProduct().getPlaceOfOrigin())
                .description(clickProfileApi.getProduct().getDescription())
                .build();
    }

    /**
     * 从收藏记录构建推荐输入对象
     */
    private ProductForEmbeddingApVO buildProductForEmbeddingFromCollect(CollectProfileApi collectProfileApi,
                                                                        Map<Long, Double> ratioMap) {

        Long productId =collectProfileApi.getProduct().getId();

        // 则默认给 1.0 (最高权重)
        Double score = 1.0;
        if (ratioMap != null && ratioMap.containsKey(productId)) {
            score = ratioMap.get(productId);
        }

        return ProductForEmbeddingApVO.builder()
                .id(productId)
                .title(collectProfileApi.getProduct().getName())
                .brand(collectProfileApi.getProduct().getBrand())
                .score(score)
                .description(collectProfileApi.getProduct().getDescription())
                .placeOfOrigin(collectProfileApi.getProduct().getPlaceOfOrigin())
                .categoryNames(collectProfileApi.getCategoryList().stream()
                        .map(CategoryApiVO::getName)
                        .collect(Collectors.toList()))
                .tagNames(collectProfileApi.getTagList().stream()
                        .map(TagApiVO::getName)
                        .collect(Collectors.toList()))
                .skuList(collectProfileApi.getSkuList().stream()
                        .map(skuApiVO -> ProductForEmbeddingApVO.SkuItem.builder()
                                .name(skuApiVO.getName())
                                .price(skuApiVO.getPrice().toString())
                                .id(skuApiVO.getId().toString())
                                .skuCode(skuApiVO.getSkuCode())
                                .build())
                        .toList())
                .build();
    }

    /**
     * 从购买记录构建推荐输入对象（新增）
     */
    private ProductForEmbeddingApVO buildProductForEmbeddingFromPurchase(PurchaseOrderVOApi order,
                                                                         Map<Long, Double> ratioMap) {
        Long productId = order.getProductId();

        // 则默认给 1.0 (最高权重)
        Double score = 1.0;
        if (ratioMap != null && ratioMap.containsKey(productId)) {
            score = ratioMap.get(productId);
        }
        return ProductForEmbeddingApVO.builder()
                .id(productId)
                .title(order.getName())
                .score(score)
                .categoryNames(Optional.ofNullable(order.getCategoryApiVOList()).orElse(List.of()).stream()
                        .map(CategoryApiVO::getName)
                        .collect(Collectors.toList()))
                .tagNames(Optional.ofNullable(order.getTags()).orElse(List.of()).stream()
                        .map(TagApiVO::getName)
                        .collect(Collectors.toList()))
                .skuList(Optional.ofNullable(order.getSkuApi()).orElse(List.of()).stream()
                        .map(skuApiVO -> ProductForEmbeddingApVO.SkuItem.builder()
                                .name(skuApiVO.getName())
                                .price(skuApiVO.getPrice().toString())
                                .id(skuApiVO.getId().toString())
                                .skuCode(skuApiVO.getSkuCode())
                                .build())
                        .toList())
                .build();
    }

    /**
     * 从浏览记录构建推荐输入对象
     *
     * @param productApiVO 商品详情对象 (从 BrowseProfileApi.product 列表中提取)
     * @param categoryList 浏览记录中的分类列表
     * @param tagList      浏览记录中的标签列表
     * @param skuList      浏览记录中的SKU列表
     * @param ratioMap     商品权重地图
     * @return 用于 Embedding 的商品 VO
     */
    private ProductForEmbeddingApVO buildProductForEmbeddingFromBrowse(
            ProductApiVO productApiVO,
            List<CategoryApiVO> categoryList,
            List<TagApiVO> tagList,
            List<SKUApiVO> skuList,
            Map<Long, Double> ratioMap) {

        if (productApiVO == null || productApiVO.getId() == null) {
            return null;
        }

        Long productId = productApiVO.getId();

        // 则默认给 1.0 (最高权重)
        Double score = 1.0;
        if (ratioMap != null && ratioMap.containsKey(productId)) {
            score = ratioMap.get(productId);
        }

        return ProductForEmbeddingApVO.builder()
                .id(productId)
                // 假设 ProductApiVO 中有 getName()，如果没有可能需要 getTitle()，请根据实际字段调整
                .title(productApiVO.getName())
                .brand(productApiVO.getBrand())
                // 获取该商品的权重分数
                .score(score)
                .description(productApiVO.getDescription())
                .placeOfOrigin(productApiVO.getPlaceOfOrigin())
                // 处理分类名称
                .categoryNames(Optional.ofNullable(categoryList).orElse(List.of()).stream()
                        .map(CategoryApiVO::getName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                // 处理标签名称
                .tagNames(Optional.ofNullable(tagList).orElse(List.of()).stream()
                        .map(TagApiVO::getName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                // 处理 SKU 列表
                .skuList(Optional.ofNullable(skuList).orElse(List.of()).stream()
                        .map(skuApiVO -> ProductForEmbeddingApVO.SkuItem.builder()
                                .name(skuApiVO.getName())
                                .price(skuApiVO.getPrice() != null ? skuApiVO.getPrice().toString() : null)
                                .id(skuApiVO.getId() != null ? skuApiVO.getId().toString() : null)
                                .skuCode(skuApiVO.getSkuCode())
                                .build())
                        .toList())
                .build();
    }

    /**
     * 基于用户近期行为（点击、收藏、搜索）生成个性化商品推荐。
     * <p>
     * 融合用户最近 10 条点击/收藏记录和搜索关键词，提取 Top3 高频商品，
     * 构造嵌入向量请求，调用大模型推荐接口，最终返回推荐商品列表。
     * </p>
     *
     * @return {@link Mono < List < ProductCustomerVO >>} 推荐的商品列表
     * @author guanshiyun
     * @since 2025-12-20 10:13
     */
    @Override
    public Mono<List<ProductCustomerVO>> recommend() {
        // 获取用户行为记录
        Mono<ResultT<List<ClickProfileApi>>> apiUserClickRecord = userClickServiceApi.findUserClickRecord(ConstNumber.INT_TWO);
        Mono<ResultT<List<CollectProfileApi>>> apiUserCollectRecord = userCollectServiceApi.findUserCollectRecord(ConstNumber.INT_TWO);
        Mono<ResultT<List<SearchContentApi>>> apiUserSearchRecord = userSearchServiceApi.findUserSearchRecord(ConstNumber.INT_TWO);
        Mono<ResultT<List<BrowseProfileApi>>> apiUserBrowseRecord = userBrowseServiceApi.findUserBrowseRecord(ConstNumber.INT_TWO);
        // 使用融合后的购买记录
        Mono<List<PurchaseOrderVOApi>> apiOrderRecord = findPurchaseOrdersWithProductInfo();

        return Mono.zip(apiUserClickRecord, apiUserCollectRecord, apiUserSearchRecord, apiOrderRecord, apiUserBrowseRecord)
                .flatMap(tuple -> {
                    // 处理搜索记录，按时间倒序
                    List<SearchContentApi> searchContentApiList = Optional.ofNullable(tuple.getT3().getData())
                            .orElse(List.of())
                            .stream()
                            .filter(Objects::nonNull)
                            .sorted(Comparator.comparing(SearchContentApi::getSearchTime, Comparator.reverseOrder()))
                            .toList();

                    List<ClickProfileApi> clickProfileApiList = Optional.ofNullable(tuple.getT1().getData()).orElse(List.of());
                    List<CollectProfileApi> collectProfileApiList = Optional.ofNullable(tuple.getT2().getData()).orElse(List.of());
                    // 购买记录已经是融合后的数据
                    List<PurchaseOrderVOApi> purchaseOrderList = Optional.of(tuple.getT4()).orElse(List.of());
                    // 解析浏览记录
                    List<BrowseProfileApi> browseProfileApiList = Optional.ofNullable(tuple.getT5().getData()).orElse(List.of());

                    // === 合并所有行为的商品 ID 列表 (点击 + 收藏 + 购买 + 浏览) ===
                    List<Long> productIdList = Stream.concat(
                                    Stream.concat(
                                            Stream.concat(
                                                    // 1. 点击商品 ID
                                                    Optional.of(clickProfileApiList).orElse(List.of()).stream()
                                                            .map(c -> c.getProduct().getId()),
                                                    // 2. 收藏商品 ID
                                                    Optional.of(collectProfileApiList).orElse(List.of()).stream()
                                                            .map(c -> c.getProduct().getId())
                                            ),
                                            // 3. 购买商品 ID
                                            purchaseOrderList.stream()
                                                    .map(PurchaseOrderVOApi::getProductId)
                                                    .filter(Objects::nonNull)
                                    ),
                                    // 4. 浏览商品 ID
                                    browseProfileApiList.stream()
                                            .filter(Objects::nonNull)
                                            .map(BrowseProfileApi::getProduct)
                                            .map(ProductApiVO::getId)
                                            .filter(Objects::nonNull)
                            )
                            .filter(Objects::nonNull)
                            .distinct()
                            .toList();

                    int totalProductId = productIdList.size();
                    final int totalIds = totalProductId == ConstNumber.INT_ZERO ? ConstNumber.INT_ONE : totalProductId;

                    // 计算每个商品 ID 的占比（点击+收藏+购买+浏览）
                    Map<Long, Double> productRatioMap = productIdList.stream()
                            .collect(Collectors.groupingBy(
                                    Function.identity(),
                                    Collectors.collectingAndThen(
                                            Collectors.counting(),
                                            count -> count * ConstNumber.DOUBLE_ONE / totalIds
                                    )
                            ));


                    // 取占比排名前三的商品
                    Map<Long, Double> top3ProductRatioMap = productRatioMap.entrySet().stream()
                            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                            .limit(ConstNumber.INT_THREE)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (v1, v2) -> v1,
                                    LinkedHashMap::new
                            ));

                    Set<Long> top3ProductIdSet = top3ProductRatioMap.keySet();

                    // 构造用于大模型推荐的输入数据
                    List<ProductForEmbeddingApVO> productForEmbeddingApVOList = new ArrayList<>();

                    // === 将最新3条搜索内容转为虚拟商品（仅含关键词） ===
                    Optional.of(searchContentApiList).orElse(List.of()).stream()
                            .limit(ConstNumber.INT_THREE)
                            .filter(Objects::nonNull)
                            .forEach(searchContentApi ->
                                    productForEmbeddingApVOList.add(
                                            ProductForEmbeddingApVO.builder()
                                                    .brand(searchContentApi.getSearchContent())
                                                    .title(searchContentApi.getSearchContent())
                                                    .categoryNames(List.of(searchContentApi.getSearchContent()))
                                                    .skuList(List.of(
                                                            ProductForEmbeddingApVO.SkuItem.builder()
                                                                    .name(searchContentApi.getSearchContent())
                                                                    .price(
                                                                            searchContentApi.getMinPrice() == null ? null : searchContentApi.getMinPrice().toString()
                                                                    )
                                                                    .build()
                                                    ))
                                                    .tagNames(List.of(searchContentApi.getSearchContent()))
                                                    .build())
                            );

                    // === 将 Top3 点击商品转为结构化推荐输入 ===
                    Optional.of(clickProfileApiList).orElse(List.of()).stream()
                            .filter(Objects::nonNull)
                            .filter(clickProfileApi -> top3ProductIdSet.contains(clickProfileApi.getProduct().getId()))
                            .forEach(clickProfileApi ->
                                    productForEmbeddingApVOList.add(buildProductForEmbeddingFromClick(clickProfileApi, top3ProductRatioMap))
                            );

                    // === 将 Top3 收藏商品转为结构化推荐输入 ===
                    Optional.of(collectProfileApiList).orElse(List.of()).stream()
                            .filter(Objects::nonNull)
                            .filter(collect -> top3ProductIdSet.contains(collect.getProduct().getId()))
                            .forEach(collectProfileApi ->
                                    productForEmbeddingApVOList.add(buildProductForEmbeddingFromCollect(collectProfileApi, top3ProductRatioMap))
                            );

                    // === 将购买记录商品转为结构化推荐输入 ===
                    purchaseOrderList.stream()
                            .filter(Objects::nonNull)
                            .filter(order -> top3ProductIdSet.contains(order.getProductId()))
                            .forEach(order ->
                                    productForEmbeddingApVOList.add(buildProductForEmbeddingFromPurchase(order, top3ProductRatioMap))
                            );
                    // ===【新增】将 Top3 浏览商品转为结构化推荐输入 ===
                    browseProfileApiList.stream()
                            .filter(Objects::nonNull)
                            // 关键步骤：展开商品列表，但保留父对象 (BrowseProfileApi) 的引用以获取分类/标签/SKU
                            .flatMap(browse -> {
                                ProductApiVO product = browse.getProduct();
                                return Stream.of(product)
                                        // 先过滤出 Top3 的商品，减少后续处理
                                        .filter(Objects::nonNull)
                                        .filter(p -> top3ProductIdSet.contains(p.getId()))
                                        // 将 (商品, 浏览记录) 打包，方便后续取值
                                        .map(p -> new AbstractMap.SimpleEntry<>(p, browse));
                            })
                            .forEach(entry -> {
                                ProductApiVO product = entry.getKey();
                                BrowseProfileApi browseRecord = entry.getValue();

                                // 调用完整的构建方法，传入所有必要的列表数据
                                ProductForEmbeddingApVO vo = buildProductForEmbeddingFromBrowse(
                                        product,
                                        browseRecord.getCategoryList(), // 从父对象获取
                                        browseRecord.getTagList(),      // 从父对象获取
                                        browseRecord.getSkuList(),      // 从父对象获取
                                        top3ProductRatioMap
                                );

                                if (vo != null) {
                                    productForEmbeddingApVOList.add(vo);
                                }
                            });
                    RequestBodyProductForEmbeddingApVO<List<ProductForEmbeddingApVO>> requestBodyProductForEmbeddingApVO =
                            RequestBodyProductForEmbeddingApVO.<List<ProductForEmbeddingApVO>>builder()
                                    .topK(20)
                                    .data(productForEmbeddingApVOList)
                                    .build();

                    return aiChatClientRecommendServiceApi.recommendProduct(requestBodyProductForEmbeddingApVO)
                            .flatMap(recommendProductIds -> {
                                if (Objects.isNull(recommendProductIds)
                                        || Objects.isNull(recommendProductIds.getData())
                                        || recommendProductIds.getData().isEmpty()) {
                                    // 默认返回最新的5条
                                    return productRepository.findAll().take(20)
                                            .map(ProductCustomerVO::toVO)
                                            .collectList();
                                }
                                List<Long> recommendIds = recommendProductIds.getData();

                                return productRepository.findAllById(recommendIds)
                                        .collectMap(Product::getId)
                                        .flatMapMany(productMap ->
                                                Flux.fromIterable(recommendIds)
                                                        .map(productMap::get)
                                                        .filter(Objects::nonNull)
                                        )
                                        .map(ProductCustomerVO::toVO)
                                        .collectList();
                            })
                            .onErrorResume(Mono::error);
                })
                .onErrorResume(e -> {
                    log.error("获取推荐商品失败", e);
                    return Mono.empty();
                });
    }
    @Override
    public Mono<List<ProductCustomerVO>> hot() {
        return utilsService
                .findProductIdsByTotalSalesGreaterThan(ConstNumber.INT_ONE)
                .flatMap(ids -> {
                    if (ids.isEmpty()) {
                        return Mono.just(Collections.emptyList());
                    }

                    List<Long> idList = ids.subList(0, Math.min(ids.size(), ConstNumber.INT_FOUR));

                    // 查询这些 ID 对应的商品
                    return productRepository.findAllById(idList)
                            .map(ProductCustomerVO::toVO)
                            .collectList()
                            // 保持输入 ID 顺序
                            .map(list -> {
                                Map<Long, ProductCustomerVO> map = list.stream()
                                        .collect(Collectors.toMap(ProductCustomerVO::getId, Function.identity()));
                                return idList.stream()
                                        .map(map::get)
                                        .filter(Objects::nonNull)
                                        .toList();
                            });
                });
    }

    @Override
    public Mono<List<ProductCustomerVO>> mostNew() {
        @Data
        @AllArgsConstructor
        class ScoredProduct {
            ProductCustomerVO product;
            double score;
        }
        // 1. 构建查询条件
        Query query = Query.empty()
                // 按发布时间倒序排列 (最新的在前面)
                .sort(Sort.by(Sort.Direction.DESC, Product.Fields.publishTime))
                // 限制返回 20 条
                .limit(ConstNumber.INT_FIFTY);
        return r2dbcEntityTemplate.select(query, Product.class)
                .map(ProductCustomerVO::toVO)
                .collectList()
                .map(list -> {
                    if (list.size() <= ConstNumber.INT_FOUR) return list;

                    // 给每个商品计算一个“混合分数”
                    List<ScoredProduct> scoredList = new ArrayList<>();
                    Random random = new Random();

                    for (int i = 0; i < list.size(); i++) {
                        // 基础分：倒序排名，第 1 名得 100 分，第 20 名得 1 分
                        double timeScore = (list.size() - i) * 10.0;

                        // 随机分：0 ~ 20 之间的随机波动
                        // 波动幅度越大，多样性越强；幅度越小，越保最新
                        double randomScore = random.nextDouble() * 20.0;

                        scoredList.add(new ScoredProduct(list.get(i), timeScore + randomScore));
                    }

                    // 按混合分数降序排列
                    scoredList.sort((a, b) -> Double.compare(b.score, a.score));

                    // 取前 4
                    return scoredList.stream()
                            .limit(ConstNumber.INT_FOUR)
                            .map(sp -> sp.product)
                            .collect(Collectors.toList());
                });
    }

    @Override
    public Mono<CursorPageResult<List<ProductCustomerVO>>> findCursorEnd(RequestCursorPage<ProductCustomerVO> requestCursorPage) {
        // 1. 参数校验与转换
        // 校验请求参数 (pageSize, order, lastId 等)
        RequestCursorPage<ProductCustomerVO> validatedVoPage = CursorPageUtil.validate(requestCursorPage, ProductCustomerVO.class);

        // 将 VO 类型的请求参数转换为 Entity 类型的请求参数 (为了适配 CursorQuery<Product>)
        // 注意：这里只转换了分页参数和查询条件字段，确保 Product 类里有对应的字段
        RequestCursorPage<Product> productRequestPage = BeanConvertUtil.toBean(validatedVoPage, Product.class);

        // 2. 构建并执行查询
        return CursorQuery.of(r2dbcEntityTemplate, Product.class, productRequestPage)
                .list() // 返回 Flux<Product>，内部已自动处理游标条件和 limit(pageSize + 1)
                .collectList() // 收集为 List<Product>
                .map(products -> {
                    // 3. 处理分页逻辑 (判断 hasNext)
                    int pageSize = CursorPageUtil.pageSize(requestCursorPage.getPageSize());
                    boolean hasNext = products.size() > pageSize;

                    // 如果多查了一条，去掉最后一条
                    if (hasNext) {
                        products = products.subList(0, pageSize);
                    }

                    // 4. 实体转 VO
                    List<ProductCustomerVO> voList = products.stream()
                            .map(ProductCustomerVO::toVO) // 假设你的 VO 有静态方法 toVO(Product p)
                            .toList();

                    // 5. 确定下一个游标 (nextCursor)
                    // 如果有下一页，取当前列表最后一个元素的 ID 作为下一次查询的 lastId
                    Long nextCursor = ConstNumber.LONG_ZERO;
                    if (!voList.isEmpty()) {
                        // 假设 VO 或 Product 有 getId() 方法
                        nextCursor = voList.getLast().getId();
                    }

                    // 如果是倒序查询且还有下一页，通常直接返回最后一条ID即可
                    // 如果是正序，逻辑类似，取决于前端怎么传 lastId

                    // 6. 构建返回结果
                    return CursorPageResult.<List<ProductCustomerVO>>builder()
                            .rows(voList)
                            .cursor(nextCursor) // 下游取这个值作为下次的 lastId
                            .hasNext(hasNext)
                            .build();
                });
    }

    @Override
    public Mono<CursorPageResult<List<ProductCustomerVO>>> recommendByPool(Integer pageSize, Boolean refresh) {
        int size = (Objects.isNull(pageSize) || pageSize <= ConstNumber.INT_ZERO) ? ConstNumber.INT_TEN : Math.min(pageSize, ConstNumber.INT_TEN);
        boolean needRefresh = Boolean.TRUE.equals(refresh);

        return Mono.deferContextual(ctx -> {
            // 【未登录用户】：直接走 Gorse，不经过池化
            if (!myLong.hasKey(ctx)) {
                log.debug("用户未登录，直接请求 Gorse 推荐");
                return gorseClient.getRecommend(GuestEnum.GUEST_USER_ID.getValue(), size)
                        .map(Flux::fromIterable)
                        .flatMapMany(Function.identity())
                        .mapNotNull(myLong::longOrNull)
                        .collectList()
                        .flatMap(this::buildResultFromAiProductIds)
                        .onErrorResume(e -> {
                            log.error("未登录用户 Gorse 推荐失败", e);
                            return loadDefaultProducts(size)
                                    .map(list -> CursorPageResult.<List<ProductCustomerVO>>builder()
                                            .rows(list)
                                            .cursor(ConstNumber.LONG_ZERO)
                                            .hasNext(false)
                                            .build());
                        });
            }

            // 【已登录用户】
            Long userId = myLong.findUserId(ctx);
            String userKey = userId.toString();
            String poolKey = ProductKey.RECOMMEND_POOL_KEY_PREFIX + userKey;

            if (needRefresh) {
                return reactiveRedisUtil.del(poolKey)
                        .doOnError(e -> log.warn("清理旧推荐池失败: key={}，{}", poolKey, e.getMessage()))
                        .onErrorReturn(true)
                        .then(refillPoolWithAiLogic(poolKey))
                        .then(fetchFromPool(poolKey, size));
            } else {
                return fetchFromPool(poolKey, size);
            }
        });
    }

    /**
     * 从 Redis 池获取数据
     * 【修改点】：在最终返回前，再次对结果列表进行随机打乱，确保无序
     */
    private Mono<CursorPageResult<List<ProductCustomerVO>>> fetchFromPool(String poolKey, int pageSize) {

        return reactiveRedisUtil.lLen(poolKey)
                .defaultIfEmpty(0L)
                .flatMap(len -> {
                    if (len <= 0) {
                        log.info("推荐池为空，触发懒加载填充: key={}", poolKey);
                        return refillPoolWithAiLogic(poolKey)
                                .then(reactiveRedisUtil.expire(poolKey, 3600))
                                .then(reactiveRedisUtil.lLen(poolKey).defaultIfEmpty(0L));
                    }
                    return Mono.just(len);
                })
                .flatMap(lenAfterFill -> {
                    if (lenAfterFill <= 0) {
                        return loadDefaultProducts(pageSize)
                                .map(list -> CursorPageResult.<List<ProductCustomerVO>>builder()
                                        .rows(list)
                                        .cursor(list.isEmpty() ? ConstNumber.LONG_ZERO : list.getLast().getId())
                                        .hasNext(false)
                                        .build());
                    }

                    // 从 Redis 弹出 ID
                    return Flux.range(0, pageSize)
                            .concatMap(i -> reactiveRedisUtil.rPop(poolKey))
                            .mapNotNull(myLong::longOrNull)
                            .collectList()
                            .flatMap(ids -> {
                                if (ids.isEmpty()) {
                                    return loadDefaultProducts(pageSize)
                                            .map(list -> CursorPageResult.<List<ProductCustomerVO>>builder()
                                                    .rows(list)
                                                    .cursor(list.isEmpty() ? ConstNumber.LONG_ZERO : list.getLast().getId())
                                                    .hasNext(false)
                                                    .build());
                                }

                                return productRepository.findAllById(ids)
                                        .collectList()
                                        .flatMap(list -> {
                                            if (list.isEmpty()) {
                                                return loadDefaultProducts(pageSize)
                                                        .map(defaultList -> CursorPageResult.<List<ProductCustomerVO>>builder()
                                                                .rows(defaultList)
                                                                .cursor(defaultList.isEmpty() ? ConstNumber.LONG_ZERO : defaultList.getLast().getId())
                                                                .hasNext(false)
                                                                .build());
                                            }

                                            // 1. 保持 ID 顺序映射 VO (暂时)
                                            Map<Long, Product> map = list.stream()
                                                    .collect(Collectors.toMap(Product::getId, Function.identity()));

                                            // 使用 ArrayList 以便后续 shuffle
                                            List<ProductCustomerVO> ordered = new ArrayList<>(ids.stream()
                                                    .map(map::get)
                                                    .filter(Objects::nonNull)
                                                    .map(ProductCustomerVO::toVO)
                                                    .toList());
                                            // 这样即使 Redis 里的顺序有规律，用户看到的也是随机的
//                                            Collections.shuffle(ordered);

                                            return reactiveRedisUtil.lLen(poolKey)
                                                    .defaultIfEmpty(0L)
                                                    .map(remaining -> {
//                                                        Long cursorVal =
//                                                                ordered.isEmpty() ?
//                                                                        ConstNumber.LONG_ZERO :
//                                                                        ordered.getLast().getId();
                                                        Long cursorVal = ConstNumber.LONG_ZERO;
                                                        if (!ordered.isEmpty()) {
                                                            // 获取最后一个元素的 ID 作为游标
                                                            cursorVal = ordered.getLast().getId();
                                                        }
                                                        boolean hasNext = remaining > 0;

                                                        return CursorPageResult.<List<ProductCustomerVO>>builder()
                                                                .rows(ordered)
                                                                .cursor(cursorVal)
                                                                .hasNext(hasNext)
                                                                .build();
                                                    });
                                        });
                            });
                });
    }

    /**
     * 填充池子逻辑
     */

    private Mono<Void> refillPoolWithAiLogic(String poolKey) {
        Mono<ResultT<List<ClickProfileApi>>> clickMono = userClickServiceApi.findUserClickRecord(ConstNumber.INT_TWO);
        Mono<ResultT<List<CollectProfileApi>>> collectMono = userCollectServiceApi.findUserCollectRecord(ConstNumber.INT_TWO);
        Mono<ResultT<List<SearchContentApi>>> searchMono = userSearchServiceApi.findUserSearchRecord(ConstNumber.INT_TWO);
        Mono<List<PurchaseOrderVOApi>> orderMono = findPurchaseOrdersWithProductInfo();
        Mono<ResultT<List<BrowseProfileApi>>> apiUserBrowseRecord = userBrowseServiceApi.findUserBrowseRecord(ConstNumber.INT_TWO);

        return Mono.zip(clickMono, collectMono, searchMono, orderMono, apiUserBrowseRecord)
                .flatMap(tuple -> {
                    //点击
                    List<ClickProfileApi> clickList = Optional.ofNullable(tuple.getT1().getData()).orElse(List.of());
                    //收藏
                    List<CollectProfileApi> collectList = Optional.ofNullable(tuple.getT2().getData()).orElse(List.of());
                    //搜索
                    List<SearchContentApi> searchList = Optional.ofNullable(tuple.getT3().getData())
                            .orElse(List.of())
                            .stream()
                            .sorted(Comparator.comparing(SearchContentApi::getSearchTime, Comparator.reverseOrder()))
                            .toList();
                    List<PurchaseOrderVOApi> purchaseList = tuple.getT4();
                    // 解析浏览记录
                    List<BrowseProfileApi> browseList = Optional.ofNullable(tuple.getT5().getData()).orElse(List.of());

                    List<BehaviorRecord> allBehavior = new ArrayList<>();

                    // 1. 点击数据
                    clickList.forEach(c -> allBehavior.add(new BehaviorRecord(
                            c.getClickTime(),
                            c.getProduct().getId(),
                            GorseFeedbackEnum.CLICK.getValue(),
                            c
                    )));

                    // 2. 收藏数据
                    collectList.forEach(c ->

                            allBehavior.add(new BehaviorRecord(c.getCollectTime(),
                                    c.getProduct().getId(),
                                    GorseFeedbackEnum.COLLECT.getValue(), c))

                    );

                    // 3. 购买数据
                    purchaseList.forEach(o -> {
                        LocalDateTime orderTime = o.getOrderPlacementTime();
                        if (Objects.nonNull(orderTime)) {
                            allBehavior.add(new BehaviorRecord(orderTime, o.getProductId(), GorseFeedbackEnum.PURCHASE.getValue(), o));
                        }
                    });

                    // 4. 浏览数据
                    browseList.forEach(b -> {
                        LocalDateTime browseTime = b.getBrowseStartTime();
                        if (Objects.nonNull(browseTime)) {
                            allBehavior.add(new BehaviorRecord(browseTime, b.getProduct().getId(), GorseFeedbackEnum.BROWSE.getValue(), b));

                        }
                    });

                    // 5. 搜索数据
                    searchList.forEach(s -> allBehavior.add(new BehaviorRecord(
                            s.getSearchTime(),
                            null,  // 搜索无商品ID
                            GorseFeedbackEnum.SEARCH.getValue(),
                            s
                    )));

                    // 按时间倒序排序，取前2条
                    List<BehaviorRecord> top2Behavior = allBehavior.stream()
                            .sorted(Comparator.comparing(BehaviorRecord::getTime, Comparator.reverseOrder()))
                            .limit(2)
                            .toList();

                    // ====== 构造Embedding输入 ======
                    List<ProductForEmbeddingApVO> embeddingInput = new ArrayList<>();

                    // 1. 前2条（按时间取的）-> 作为虚拟商品
                    top2Behavior.forEach(br -> {
                        if (GorseFeedbackEnum.SEARCH.getValue().equals(br.getType())) {
                            SearchContentApi search = (SearchContentApi) br.getOrigin();
                            // 搜索词作为虚拟商品
                            embeddingInput.add(ProductForEmbeddingApVO.builder()
                                    .title(search.getSearchContent())
                                    .brand(search.getSearchContent())
                                    .categoryNames(List.of(search.getSearchContent()))
                                    .tagNames(List.of(search.getSearchContent()))
                                    .build());
                        } else {
                            // 其他类型用商品ID构造
                            Long productId = br.getProductId();
                            // 从原数据中获取商品详情（此处简化，实际需根据类型构造）
                            ProductForEmbeddingApVO vo = buildProductForEmbeddingFromBehavior(br);
                            if (vo != null) embeddingInput.add(vo);
                        }
                    });

                    // 2. 从剩余数据中按权重取Top3（排除前2条的商品ID）
                    List<Long> excludedIds = top2Behavior.stream()
                            .map(BehaviorRecord::getProductId)
                            .filter(Objects::nonNull)
                            .toList();

                    // 重新计算权重（排除前2条的商品ID）
                    List<Long> allProductIds = Stream.of(
                                    // 1. 点击流
                                    clickList.stream().map(c -> Objects.nonNull(c.getProduct()) ? c.getProduct().getId() : null),
                                    // 2. 收藏流
                                    collectList.stream().map(c -> Objects.nonNull(c.getProduct()) ? c.getProduct().getId() : null),
                                    // 3. 购买流
                                    purchaseList.stream().map(PurchaseOrderVOApi::getProductId),
                                    // 4. 浏览流 (需要先 flatMap 展开产品列表)
                                    browseList.stream()
                                            .filter(Objects::nonNull)
                                            .map(b -> b.getProduct().getId())
                            )
                            .flatMap(s -> s) // 将 Stream<Stream<Long>> 展平为 Stream<Long>
                            .filter(Objects::nonNull)
                            .filter(id -> !excludedIds.contains(id))
                            .distinct()
                            .toList();

                    int totalIds = allProductIds.isEmpty() ? 1 : allProductIds.size();
                    Map<Long, Double> ratioMap = allProductIds.stream()
                            .collect(Collectors.groupingBy(Function.identity(),
                                    Collectors.collectingAndThen(Collectors.counting(), c -> c * 1.0 / totalIds)));

                    Map<Long, Double> top3RatioMap = ratioMap.entrySet().stream()
                            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                            .limit(3)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (v1, v2) -> v1,
                                    LinkedHashMap::new)
                            );

                    Set<Long> top3Ids = top3RatioMap.keySet();

                    // 3. 后3条（按权重取的）-> 从剩余数据中取Top3
                    // 点击
                    clickList.stream().filter(c -> top3Ids.contains(c.getProduct().getId()))
                            .forEach(c -> embeddingInput.add(buildProductForEmbeddingFromClick(c, top3RatioMap)));
                    // 收藏
                    collectList.stream().filter(c -> top3Ids.contains(c.getProduct().getId()))
                            .forEach(c -> embeddingInput.add(buildProductForEmbeddingFromCollect(c, top3RatioMap)));
                    // 购买
                    purchaseList.stream().filter(o -> top3Ids.contains(o.getProductId()))
                            .forEach(o -> embeddingInput.add(buildProductForEmbeddingFromPurchase(o, top3RatioMap)));
                    // 浏览处理逻辑
                    browseList.stream()
                            .filter(Objects::nonNull) // 1. 过滤掉 browse 记录本身为 null 的情况
                            .filter(browse -> browse.getProduct() != null) // 2. 【关键】过滤掉 product 为 null 的记录，防止 NPE
                            .filter(browse -> {
                                // 3. 安全地检查 ID 是否在 Top3 中
                                Long id = browse.getProduct().getId();
                                return id != null && top3Ids.contains(id);
                            })
                            .forEach(browse -> {
                                // 4. 直接使用当前的 browse 对象，无需再次查找
                                ProductApiVO p = browse.getProduct();

                                ProductForEmbeddingApVO vo = buildProductForEmbeddingFromBrowse(
                                        p,
                                        browse.getCategoryList(),
                                        browse.getTagList(),
                                        browse.getSkuList(),
                                        top3RatioMap
                                );

                                if (vo != null) {
                                    embeddingInput.add(vo);
                                }
                            });
                    RequestBodyProductForEmbeddingApVO<List<ProductForEmbeddingApVO>> requestBody =
                            RequestBodyProductForEmbeddingApVO.<List<ProductForEmbeddingApVO>>builder()
                                    .topK(ProductKey.RECOMMEND_POOL_SIZE)
                                    .data(embeddingInput)
                                    .build();

                    return aiChatClientRecommendServiceApi.recommendProduct(requestBody)
                            .map(resp -> Optional.ofNullable(resp.getData()).orElse(Collections.emptyList()))
                            .onErrorResume(e -> {
                                log.warn("AI 推荐服务调用失败，降级为纯热门/新品策略", e);
                                return Mono.just(Collections.emptyList());
                            })
                            .flatMap(aiIds -> {
                                int totalSize = ProductKey.RECOMMEND_POOL_SIZE;
                                int aiCount = (int) (totalSize * 0.65);
                                int hotCount = (int) (totalSize * 0.25);
                                int newCount = totalSize - aiCount - hotCount;

                                List<Long> validAiIds = aiIds.stream().limit(aiCount).toList();
                                List<Long> finalPoolIds = new ArrayList<>(validAiIds);

                                if (finalPoolIds.size() < totalSize) {
                                    // 补充热门商品
                                    Mono<List<Long>> hotMono = utilsService
                                            .findProductIdsByTotalSalesGreaterThan(ConstNumber.INT_HUNDRED)
                                            .defaultIfEmpty(Collections.emptyList())
                                            .map(list -> list.stream()
                                                    .filter(id -> !finalPoolIds
                                                            .contains(id)).limit(hotCount)
                                                    .toList()
                                            );

                                    Mono<List<Long>> newMono = productRepository.findAll()
                                            .sort(Comparator.comparing(Product::getPublishTime).reversed())
                                            .map(Product::getId)
                                            .collectList()
                                            .map(list -> list
                                                    .stream()
                                                    .filter(id -> !finalPoolIds
                                                            .contains(id))
                                                    .limit(newCount)
                                                    .toList()
                                            );

                                    return Mono.zip(hotMono, newMono)
                                            .map(t -> {
                                                finalPoolIds.addAll(t.getT1());
                                                finalPoolIds.addAll(t.getT2());
                                                return finalPoolIds;
                                            });
                                }
                                return Mono.just(finalPoolIds);
                            })
                            .flatMap(ids -> writeIdsToPoolInternal(poolKey, ids));
                });
    }


    /**
     * 【修改点 2】：写入 Redis 前，再次确保彻底打乱
     */
    private Mono<Void> writeIdsToPoolInternal(String poolKey, List<Long> rawIds) {
        if (rawIds == null || rawIds.isEmpty()) {
            log.warn("尝试写入空的推荐列表到池子: {}", poolKey);
            return Mono.empty();
        }

        // 去重
        List<Long> distinctIds = new ArrayList<>(new LinkedHashSet<>(rawIds));

//        Collections.shuffle(distinctIds);
//        shuffleInGroups(distinctIds);

        if (distinctIds.size() > ProductKey.RECOMMEND_POOL_SIZE) {
            distinctIds = distinctIds.subList(0, ProductKey.RECOMMEND_POOL_SIZE);
        }

        log.info("正在填充推荐池 (已打乱): key={}, 数量={}", poolKey, distinctIds.size());

        return reactiveRedisUtil.del(poolKey)
                .onErrorResume(e -> {
                    log.warn("清理旧池失败: {}", e.getMessage());
                    return Mono.just(false);
                })
                .thenMany(Flux.fromIterable(distinctIds)
                        .flatMap(id -> reactiveRedisUtil.lPush(poolKey, id.toString())))
                .then(reactiveRedisUtil.expire(poolKey, 3600))
                .then();
    }

    private Mono<List<ProductCustomerVO>> loadDefaultProducts(int limit) {
        return utilsService.findProductIdsByTotalSalesGreaterThan(ConstNumber.INT_HUNDRED)
                .defaultIfEmpty(Collections.emptyList())
                .flatMap(ids -> {
                    if (ids.isEmpty()) {
                        return productRepository.findAll()
                                .take(limit)
                                .map(ProductCustomerVO::toVO)
                                .collectList();
                    }
                    List<Long> limitedIds = ids.stream().limit(limit).toList();
                    return productRepository.findAllById(limitedIds)
                            .collectList()
                            .map(list -> {
                                Map<Long, ProductCustomerVO> map = list.stream()
                                        .map(ProductCustomerVO::toVO)
                                        .collect(Collectors.toMap(ProductCustomerVO::getId, Function.identity(), (v1, v2) -> v1));
                                return limitedIds.stream().map(map::get).filter(Objects::nonNull).toList();
                            });
                })
                .onErrorResume(e -> {
                    log.error("加载默认商品失败", e);
                    return productRepository.findAll().take(limit).map(ProductCustomerVO::toVO).collectList();
                });
    }

    private Mono<CursorPageResult<List<ProductCustomerVO>>> buildResultFromAiProductIds(List<Long> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return Mono.just(CursorPageResult.<List<ProductCustomerVO>>builder()
                    .rows(Collections.emptyList())
                    .cursor(ConstNumber.LONG_ZERO)
                    .hasNext(false)
                    .build());
        }

        return productRepository.findAllById(ids)
                .collectList()
                .map(list -> {
                    Map<Long, Product> map = list.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
                    List<ProductCustomerVO> vos = new ArrayList<>(ids.stream()
                            .map(map::get)
                            .filter(Objects::nonNull)
                            .map(ProductCustomerVO::toVO)
                            .toList());
                    return CursorPageResult.<List<ProductCustomerVO>>builder()
                            .rows(vos)
                            .cursor(vos.isEmpty() ? ConstNumber.LONG_ZERO : vos.getLast().getId())
                            .hasNext(false)
                            .build();
                });
    }

    private ProductForEmbeddingApVO buildProductForEmbeddingFromBehavior(BehaviorRecord br) {
        // 根据类型从原始数据获取商品详情（示例简化）
        if (GorseFeedbackEnum.CLICK.getValue().equals(br.getType())) {
            ClickProfileApi click = (ClickProfileApi) br.getOrigin();
            return buildProductForEmbeddingFromClick(click, null); // 权重map这里不需要
        } else if (GorseFeedbackEnum.COLLECT.getValue().equals(br.getType())) {
            CollectProfileApi collect = (CollectProfileApi) br.getOrigin();
            return buildProductForEmbeddingFromCollect(collect, null);
        } else if (GorseFeedbackEnum.PURCHASE.getValue().equals(br.getType())) {
            PurchaseOrderVOApi order = (PurchaseOrderVOApi) br.getOrigin();
            return buildProductForEmbeddingFromPurchase(order, null);
        } else if (GorseFeedbackEnum.BROWSE.getValue().equals(br.getType())) {
            BrowseProfileApi browse = (BrowseProfileApi) br.getOrigin();
            // 需要额外处理（因为browse记录包含多个商品）
            // 这里简化：假设只处理第一条商品
            return buildProductForEmbeddingFromBrowse(
                    browse.getProduct(),
                    browse.getCategoryList(),
                    browse.getTagList(),
                    browse.getSkuList(),
                    null
            );
        }
        return null;
    }

    private void shuffleInGroups(List<Long> list) {
        int groupSize = ConstNumber.INT_FIVE;
        if (list == null || list.size() <= 1 || groupSize <= 1) {
            return;
        }

        int size = list.size();
        for (int i = 0; i < size; i += groupSize) {
            // 计算当前组的结束位置
            int end = Math.min(i + groupSize, size);

            // 如果组内元素大于 1 个，则打乱该子列表
            if (end - i > 1) {
                // subList 返回的是视图，直接 shuffle 会影响原 list
                Collections.shuffle(list.subList(i, end));
            }
        }
    }
}