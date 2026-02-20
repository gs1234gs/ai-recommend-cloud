package com.guanshiyun.service.product.impl;

import com.db.dbnumber.ConstNumber;
import com.db.page.CursorPageUtil;
import com.db.query.SafeCriteria;
import com.guanshiyun.behaviorenums.GuestEnum;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.controller.product.vo.ProductCustomerDetailVO;
import com.guanshiyun.controller.product.vo.ProductCustomerVO;
import com.guanshiyun.controller.product.vo.ProductSearchSaveVO;
import com.guanshiyun.controller.sku.vo.SKUVO;
import com.guanshiyun.controller.tag.vo.TagVO;
import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.embedding.RequestBodyProductForEmbeddingApVO;
import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.product.Product;
import com.guanshiyun.profile.CategoryApiVO;
import com.guanshiyun.profile.ProductApiVO;
import com.guanshiyun.profile.TagApiVO;
import com.guanshiyun.relationship.ProductCategory;
import com.guanshiyun.repository.category.CategoryRepository;
import com.guanshiyun.repository.product.ProductRepository;
import com.guanshiyun.repository.relation.ProductCategoryRepository;
import com.guanshiyun.repository.sku.SKURepository;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.apisave.UserClickSaveApiVO;
import com.guanshiyun.rpc.behaviorapi.click.UserClickServiceApi;
import com.guanshiyun.rpc.behaviorapi.collect.UserCollectServiceApi;
import com.guanshiyun.rpc.behaviorapi.search.UserSearchServiceApi;
import com.guanshiyun.rpc.chatrecommend.AiChatClientRecommendServiceApi;
import com.guanshiyun.rpc.order.PurchaseOrderServiceApi;
import com.guanshiyun.rpc.order.vo.PurchaseOrderVOApi;
import com.guanshiyun.rpc.profile.ClickProfileApi;
import com.guanshiyun.rpc.profile.CollectProfileApi;
import com.guanshiyun.rpc.profile.SearchContentApi;
import com.guanshiyun.service.product.RecommendProductService;
import com.guanshiyun.service.utils.UtilsService;
import com.guanshiyun.sku.SKU;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.utils.BeanConvertUtil;
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
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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
    private final ProductRepository productRepository;
    private final MyBigInteger myBigInteger;
    private final UtilsService utilsService;
    private final SKURepository sKURepository;
    private final PurchaseOrderServiceApi purchaseOrderServiceApi;
    private final ProductCategoryRepository productCategoryRepository;
    private final CategoryRepository categoryRepository;
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
    public Mono<CursorPageResult<List<ProductCustomerVO>>> findCursor(
            RequestCursorPage<ProductSearchSaveVO> requestCursorPage) {

        RequestCursorPage<ProductSearchSaveVO> validate = CursorPageUtil.validate(requestCursorPage, ProductSearchSaveVO.class);
        ProductSearchSaveVO condition = validate.getCondition();
        String searchContent = condition.getSearchContent();

        return Mono.deferContextual(ctx -> {

            // 登录校验
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                //先走gorse
                return gorseClient.getRecommend(GuestEnum.GUEST_USER_ID.getValue(),20)
                        .map(Flux::fromIterable)
                        .flatMapMany(Function.identity())
                        .mapNotNull(myBigInteger::bigIntegerOrNull)
                        .collectList()
                        .flatMap(ids->
                           buildResultFromAiProductIds(ids,validate,condition)
                        )
                        .onErrorResume(e->{
                            log.error("gorse",e);
                            return Mono.just(
                        CursorPageResult.<List<ProductCustomerVO>>builder()
                                .rows(Collections.emptyList())
                                .cursor(BigInteger.ZERO)
                                .hasNext(false)
                                .build()
                );
                        });

//                return Mono.just(
//                        CursorPageResult.<List<ProductCustomerVO>>builder()
//                                .rows(Collections.emptyList())
//                                .cursor(BigInteger.ZERO)
//                                .hasNext(false)
//                                .build()
//                );
            }
            // 如果没有搜索内容，直接走传统查询（AI 不适用）
            BigInteger userId = myBigInteger.bigIntegerOrNull(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
            if (!StringUtils.hasText(searchContent)) {
                //先走gorse
                return gorseClient.getRecommend(userId.toString(),20)
                        .map(Flux::fromIterable)
                        .flatMapMany(Function.identity())
                        .mapNotNull(myBigInteger::bigIntegerOrNull)
                        .collectList()
                        .flatMap(ids->
                                buildResultFromAiProductIds(ids,validate,condition)
                        )
                        .onErrorResume(e->{
                            log.error("gorse",e);
                            return Mono.just(
                                    CursorPageResult.<List<ProductCustomerVO>>builder()
                                            .rows(Collections.emptyList())
                                            .cursor(BigInteger.ZERO)
                                            .hasNext(false)
                                            .build()
                            );
                        });
            }
            // 优先使用 AI 推荐的商品 ID 列表
            return aiChatClientRecommendServiceApi
                    .searchByKeyword(searchContent.trim(), 20) // 获取最多 20 个候选 ID
                    .map(result->{
                                List<BigInteger> data = result.getData();
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
                                    .contextWrite(ctxs->ctxs.put(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, userId))
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

        BigInteger lastId = validate.getLastId();
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
                .map(p -> ProductCustomerVO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .image(p.getImage())
                        .video(p.getVideo())
                        .status(p.getStatus())
                        .description(p.getDescription())
                        .publishTime(p.getPublishTime())
                        .brand(p.getBrand())
                        .level(p.getLevel())
                        .placeOfOrigin(p.getPlaceOfOrigin())
                        .minPrice(p.getMinPrice().setScale(2, RoundingMode.HALF_UP))
                        .maxPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                        .originalPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                        .discountPrice(Optional.ofNullable(p.getMinPrice())
                                .map(price -> price.multiply(new BigDecimal("0.7")))
                                .map(price -> price.setScale(2, RoundingMode.HALF_UP))
                                .orElse(BigDecimal.ZERO))
                        .build())
                .collectList()
                .map(list -> {
                    boolean hasNext = list.size() > pageSize;
                    List<ProductCustomerVO> rows = hasNext ? list.subList(0, pageSize) : list;
                    BigInteger cursor = rows.isEmpty() ? BigInteger.ZERO : rows.get(rows.size() - 1).getId();
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
            List<BigInteger> aiProductIds,
            RequestCursorPage<ProductSearchSaveVO> validate,
            ProductSearchSaveVO condition) {

        if (aiProductIds.isEmpty()) {
            return Mono.just(
                    CursorPageResult.<List<ProductCustomerVO>>builder()
                            .rows(Collections.emptyList())
                            .cursor(BigInteger.ZERO)
                            .hasNext(false)
                            .build()
            );
        }

        BigInteger lastId = validate.getLastId();
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
        List<BigInteger> filteredIds = aiProductIds.stream()
                .skip(startIndex)
                .limit(pageSize + 1L)
                .toList();

        if (filteredIds.isEmpty()) {
            return Mono.just(
                    CursorPageResult.<List<ProductCustomerVO>>builder()
                            .rows(Collections.emptyList())
                            .cursor(BigInteger.ZERO)
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
                .map(p -> ProductCustomerVO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .image(p.getImage())
                        .video(p.getVideo())
                        .status(p.getStatus())
                        .description(p.getDescription())
                        .publishTime(p.getPublishTime())
                        .brand(p.getBrand())
                        .level(p.getLevel())
                        .placeOfOrigin(p.getPlaceOfOrigin())
                        .minPrice(p.getMinPrice().setScale(2, RoundingMode.HALF_UP))
                        .maxPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                        .originalPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                        .discountPrice(Optional.ofNullable(p.getMinPrice())
                                .map(price -> price.multiply(new BigDecimal("0.7")))
                                .map(price -> price.setScale(2, RoundingMode.HALF_UP))
                                .orElse(BigDecimal.ZERO))
                        .build())
                .collectList()
                .map(list -> {
                    // 按 filteredIds 顺序排序（保持 AI 推荐顺序）
                    Map<BigInteger, ProductCustomerVO> map = list.stream()
                            .collect(Collectors.toMap(ProductCustomerVO::getId, Function.identity()));
                    List<ProductCustomerVO> ordered = filteredIds.stream()
                            .map(map::get)
                            .filter(Objects::nonNull)
                            .toList();

                    boolean hasNext = ordered.size() > pageSize;
                    List<ProductCustomerVO> rows = hasNext ? ordered.subList(0, pageSize) : ordered;
                    BigInteger cursor = rows.isEmpty() ? BigInteger.ZERO : rows.getLast().getId();
                    return CursorPageResult.<List<ProductCustomerVO>>builder()
                            .rows(rows)
                            .cursor(cursor)
                            .hasNext(hasNext)
                            .build();
                });
    }

    /**
     * 基于用户近期行为（点击、收藏、搜索）生成个性化商品推荐。
     * <p>
     * 融合用户最近 10 条点击/收藏记录和搜索关键词，提取 Top3 高频商品，
     * 构造嵌入向量请求，调用大模型推荐接口，最终返回推荐商品列表。
     * </p>
     *
     * @return {@link Mono<List<ProductCustomerVO>>} 推荐的商品列表
     * @author guanshiyun
     * @since 2025-12-20 10:13
     */
//    @Override
//    public Mono<List<ProductCustomerVO>> recommend() {
//        //已经登陆,调用推荐接口，推荐接口自动根据条件判断新用户还是老用户
//        //浏览暂时不要
////            Mono<ResultT<List<BrowseProfileApi>>> apiUserBrowseRecord = userBrowseServiceApi.findUserBrowseRecord(ConstNumber.INTEGER_TEN);
//        Mono<ResultT<List<ClickProfileApi>>> apiUserClickRecord = userClickServiceApi.findUserClickRecord(ConstNumber.INT_TWO);
//        Mono<ResultT<List<CollectProfileApi>>> apiUserCollectRecord = userCollectServiceApi.findUserCollectRecord(ConstNumber.INT_TWO);
//        Mono<ResultT<List<SearchContentApi>>> apiUserSearchRecord = userSearchServiceApi.findUserSearchRecord(ConstNumber.INT_TWO);
//        Mono<ResultT<List<PurchaseOrderVOApi>>> apiOrderRecord = purchaseOrderServiceApi.findByRows(ConstNumber.INT_TWO);
//
//        return Mono.zip(
//                        apiUserClickRecord,
//                        apiUserCollectRecord,
//                        apiUserSearchRecord,
//                        apiOrderRecord
//                )
//                .flatMap(tuple -> {
//                    //处理搜索记录，按时间倒序
//                    List<SearchContentApi> searchContentApiList = Optional.ofNullable(tuple.getT3().getData())
//                            .orElse(List.of())
//                            .stream()
//                            .filter(Objects::nonNull)
//                            .sorted(Comparator.comparing(
//                                    SearchContentApi::getSearchTime,
//                                    Comparator.reverseOrder()
//                            ))
//                            .toList();
//                    List<ClickProfileApi> clickProfileApiList =
//                            Optional.ofNullable(tuple.getT1().getData()).orElse(List.of());
//                    List<CollectProfileApi> collectProfileApiList =
//                            Optional.ofNullable(tuple.getT2().getData()).orElse(List.of());
//                    List<PurchaseOrderVOApi> purchaseOrderList =
//                            Optional.ofNullable(tuple.getT4().getData()).orElse(List.of());
//                    List<BigInteger> orderPids =
//                            purchaseOrderList.stream().map(PurchaseOrderVOApi::getProductId).toList();
//                    Flux<Product> allById = productRepository.findAllById(orderPids);
//                    // === 合并点击与收藏的商品 ID 列表 ===
//                    List<BigInteger> productIdList =
//                            Stream.concat(
//                                    Optional.of(clickProfileApiList).orElse(List.of()).stream()
//                                            .map(c -> c.getProduct().getId()),
//                                    Optional.of(collectProfileApiList).orElse(List.of()).stream()
//                                            .map(c -> c.getProduct().getId())
//                            ).toList();
//
//                    int totalProductId = productIdList.size();
//                    final int totalIds = totalProductId == ConstNumber.INT_ZERO ? ConstNumber.INT_ONE : totalProductId;
//                    //  计算每个商品 ID 的占比
//                    Map<BigInteger, Double> productRatioMap =
//                            productIdList.stream()
//                                    .collect(Collectors.groupingBy(
//                                            Function.identity(),          // 商品ID作为key
//                                            Collectors.collectingAndThen(
//                                                    Collectors.counting(), // 统计次数
//                                                    count -> count * ConstNumber.DOUBLE_ONE / totalIds// 计算占比
//                                            )
//                                    ));
//                    // 取占比排名前三的商品
//                    Map<BigInteger, Double> top3ProductRatioMap =
//                            productRatioMap.entrySet()
//                                    .stream()
//                                    // 按占比倒序排序
//                                    .sorted(Map.Entry.<BigInteger, Double>comparingByValue().reversed())
//                                    // 取前三
//                                    .limit(ConstNumber.INT_THREE)
//                                    // 收集回 Map（保持排序）
//                                    .collect(Collectors.toMap(
//                                            Map.Entry::getKey,
//                                            Map.Entry::getValue,
//                                            (v1, v2) -> v1,
//                                            LinkedHashMap::new
//                                    ));
//
//                    Set<BigInteger> top3ProductIdSet = top3ProductRatioMap.keySet();
//
//                    //构造用于大模型推荐的输入数据
//                    List<ProductForEmbeddingApVO> productForEmbeddingApVOList = new ArrayList<>();
//                    // === 将最新3条搜索内容转为虚拟商品（仅含关键词） ===
//                    Optional.of(searchContentApiList)
//                            .orElse(List.of())
//                            .stream()
//                            .limit(ConstNumber.INT_THREE)
//                            .filter(Objects::nonNull)
//                            .forEach(searchContentApi ->
//                                    productForEmbeddingApVOList.add(
//                                            ProductForEmbeddingApVO.builder()
//                                                    .brand(searchContentApi.getSearchContent())
//                                                    .title(searchContentApi.getSearchContent())
//                                                    .categoryNames(List.of(searchContentApi.getSearchContent()))
//                                                    .skuList(List.of(
//                                                                    ProductForEmbeddingApVO
//                                                                            .SkuItem
//                                                                            .builder()
//                                                                            .name(searchContentApi.getSearchContent())
//                                                                            .price(searchContentApi.getMinPrice().toString())
//                                                                            .build()
//                                                            )
//                                                    )
//                                                    .tagNames(List.of(searchContentApi.getSearchContent()))
//                                                    .build())
//                            );
//                    // === 将 Top3 点击商品转为结构化推荐输入 ===
//                    Optional.of(clickProfileApiList)
//                            .orElse(List.of())
//                            .stream()
//                            .filter(Objects::nonNull)
//                            .filter(clickProfileApi -> top3ProductIdSet.contains(clickProfileApi.getProduct().getId()))
//                            .forEach(clickProfileApi ->
//                                    productForEmbeddingApVOList.add(
//                                            ProductForEmbeddingApVO.builder()
//                                                    .id(clickProfileApi.getProduct().getId())
//                                                    .title(clickProfileApi.getProduct().getName())
//                                                    .brand(clickProfileApi.getProduct().getBrand())
//                                                    .score(top3ProductRatioMap.get(clickProfileApi.getProduct().getId()))
//                                                    .tagNames(
//                                                            clickProfileApi.getTagList()
//                                                                    .stream()
//                                                                    .map(TagApiVO::getName)
//                                                                    .collect(Collectors.toList())
//                                                    )
//                                                    .skuList(
//                                                            clickProfileApi.getSkuList()
//                                                                    .stream()
//                                                                    .map(skuApiVO ->
//                                                                            ProductForEmbeddingApVO.SkuItem.builder()
//                                                                                    .name(skuApiVO.getName())
//                                                                                    .price(skuApiVO.getPrice().toString())
//                                                                                    .id(skuApiVO.getId().toString())
//                                                                                    .skuCode(skuApiVO.getSkuCode())
//                                                                                    .build()
//                                                                    )
//                                                                    .collect(Collectors.toList())
//                                                    )
//                                                    .categoryNames(
//                                                            clickProfileApi.getCategoryList()
//                                                                    .stream()
//                                                                    .map(CategoryApiVO::getName)
//                                                                    .collect(Collectors.toList())
//                                                    )
//                                                    .placeOfOrigin(clickProfileApi.getProduct().getPlaceOfOrigin())
//                                                    .description(clickProfileApi.getProduct().getDescription())
//                                                    .build()
//                                    ));
//                    // === 将 Top3 收藏商品转为结构化推荐输入（去重逻辑由大模型处理）===
//                    Optional.of(collectProfileApiList)
//                            .orElse(List.of())
//                            .stream()
//                            .filter(Objects::nonNull)
//                            .filter(collect -> top3ProductIdSet.contains(collect.getProduct().getId()))
//                            .forEach(collectProfileApi ->
//                                    productForEmbeddingApVOList.add(
//                                            ProductForEmbeddingApVO.builder()
//                                                    .id(collectProfileApi.getProduct().getId())
//                                                    .title(collectProfileApi.getProduct().getName())
//                                                    .brand(collectProfileApi.getProduct().getBrand())
//                                                    .score(top3ProductRatioMap.get(collectProfileApi.getProduct().getId()))
//                                                    .description(collectProfileApi.getProduct().getDescription())
//                                                    .placeOfOrigin(collectProfileApi.getProduct().getPlaceOfOrigin())
//                                                    .categoryNames(
//                                                            collectProfileApi.getCategoryList()
//                                                                    .stream()
//                                                                    .map(CategoryApiVO::getName)
//                                                                    .collect(Collectors.toList())
//                                                    )
//                                                    .tagNames(
//                                                            collectProfileApi.getTagList()
//                                                                    .stream()
//                                                                    .map(TagApiVO::getName)
//                                                                    .collect(Collectors.toList())
//                                                    )
//                                                    .skuList(
//                                                            collectProfileApi.getSkuList()
//                                                                    .stream()
//                                                                    .map(skuApiVO ->
//                                                                            ProductForEmbeddingApVO.SkuItem.builder()
//                                                                                    .name(skuApiVO.getName())
//                                                                                    .price(skuApiVO.getPrice().toString())
//                                                                                    .id(skuApiVO.getId().toString())
//                                                                                    .skuCode(skuApiVO.getSkuCode())
//                                                                                    .build()
//                                                                    )
//                                                                    .toList()
//                                                    )
//                                                    .build()
//                                    )
//                            );
//                    RequestBodyProductForEmbeddingApVO<List<ProductForEmbeddingApVO>> requestBodyProductForEmbeddingApVO =
//                            RequestBodyProductForEmbeddingApVO.<List<ProductForEmbeddingApVO>>builder()
//                                    .topK(20)
//                                    .data(productForEmbeddingApVOList)
//                                    .build();
//                    return aiChatClientRecommendServiceApi.recommendProduct(requestBodyProductForEmbeddingApVO)
//                            .flatMap(recommendProductIds -> {
//                                        if (Objects.isNull(recommendProductIds)
//                                                || Objects.isNull(recommendProductIds.getData())
//                                                || recommendProductIds.getData().isEmpty()) {
//                                            //默认返回最新加的5条
//                                            return productRepository.findAll().take(20)
//                                                    .map(p -> ProductCustomerVO.builder()
//                                                            .image(p.getImage())
//                                                            .video(p.getVideo())
//                                                            .status(p.getStatus())
//                                                            .description(p.getDescription())
//                                                            .publishTime(p.getPublishTime())
//                                                            .brand(p.getBrand())
//                                                            .id(p.getId())
//                                                            .name(p.getName())
//                                                            .level(p.getLevel())
//                                                            .placeOfOrigin(p.getPlaceOfOrigin())
//                                                            .minPrice(p.getMinPrice().setScale(2, RoundingMode.HALF_UP))
//                                                            .maxPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
//                                                            .originalPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
//                                                            .discountPrice(
//                                                                    Optional.ofNullable(p.getMinPrice())
//                                                                            .map(price -> price.multiply(new BigDecimal("0.7")))
//                                                                            .map(price -> price.setScale(2, RoundingMode.HALF_UP))
//                                                                            .orElse(BigDecimal.ZERO)
//                                                            )
//                                                            .placeOfOrigin(p.getPlaceOfOrigin())
//                                                            .build()
//                                                    ).collectList();
//                                        }
//                                        return productRepository.findAllById(recommendProductIds.getData())
//                                                .map(p ->
//                                                        ProductCustomerVO
//                                                                .builder()
//                                                                .image(p.getImage())
//                                                                .video(p.getVideo())
//                                                                .status(p.getStatus())
//                                                                .description(p.getDescription())
//                                                                .publishTime(p.getPublishTime())
//                                                                .brand(p.getBrand())
//                                                                .id(p.getId())
//                                                                .name(p.getName())
//                                                                .level(p.getLevel())
//                                                                .placeOfOrigin(p.getPlaceOfOrigin())
//                                                                .minPrice(p.getMinPrice().setScale(2, RoundingMode.HALF_UP))
//                                                                .maxPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
//                                                                .originalPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
//                                                                .discountPrice(
//                                                                        Optional.ofNullable(p.getMinPrice())
//                                                                                .map(price -> price.multiply(new BigDecimal("0.7")))
//                                                                                .map(price -> price.setScale(2, RoundingMode.HALF_UP))
//                                                                                .orElse(BigDecimal.ZERO)
//                                                                )
//                                                                .placeOfOrigin(p.getPlaceOfOrigin())
//                                                                .build()
//
//                                                )
//                                                .collectList();
//                                    }
//
//                            )
//                            .onErrorResume(Mono::error);
//                })
//                .onErrorResume(e -> {
//                    log.error("获取推荐商品失败", e);
//                    return Mono.empty();
//                });
//    }

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
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Mono.error(new Exception("请先登陆"));
            }
            BigInteger userId = ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
            return gorseClient.getRecommend(userId.toString(), 20)
                    .flatMap(productIds -> {
                        List<BigInteger> productIdList = productIds
                                .stream()
                                .filter(id -> StringUtils.hasText(id) && id.matches("\\d+"))
                                .map(myBigInteger::bigInteger)
                                .toList();
                        if (productIdList.isEmpty()) {
                            //默认从数据查出5条
                            return productRepository.findAll().take(20)
                                    .map(p ->
                                            ProductCustomerVO
                                                    .builder()
                                                    .image(p.getImage())
                                                    .video(p.getVideo())
                                                    .status(p.getStatus())
                                                    .description(p.getDescription())
                                                    .publishTime(p.getPublishTime())
                                                    .brand(p.getBrand())
                                                    .id(p.getId())
                                                    .name(p.getName())
                                                    .level(p.getLevel())
                                                    .placeOfOrigin(p.getPlaceOfOrigin())
                                                    .minPrice(p.getMinPrice().setScale(2, RoundingMode.HALF_UP))
                                                    .maxPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                                                    .originalPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                                                    .discountPrice(
                                                            Optional.ofNullable(p.getMinPrice())
                                                                    .map(price -> price.multiply(new BigDecimal("0.7")))
                                                                    .map(price -> price.setScale(2, RoundingMode.HALF_UP))
                                                                    .orElse(BigDecimal.ZERO)
                                                    )
                                                    .build()
                                    )
                                    .collectList();
                        }
                        return productRepository.findAllById(productIdList)
                                .map(p ->
                                        ProductCustomerVO
                                                .builder()
                                                .image(p.getImage())
                                                .video(p.getVideo())
                                                .status(p.getStatus())
                                                .description(p.getDescription())
                                                .publishTime(p.getPublishTime())
                                                .brand(p.getBrand())
                                                .id(p.getId())
                                                .name(p.getName())
                                                .level(p.getLevel())
                                                .placeOfOrigin(p.getPlaceOfOrigin())
                                                .minPrice(p.getMinPrice().setScale(2, RoundingMode.HALF_UP))
                                                .maxPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                                                .originalPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                                                .discountPrice(Optional.ofNullable(p.getMinPrice())
                                                        .map(price -> price.multiply(new BigDecimal("0.7")))
                                                        .map(price -> price.setScale(2, RoundingMode.HALF_UP))
                                                        .orElse(BigDecimal.ZERO))
                                                .placeOfOrigin(p.getPlaceOfOrigin())
                                                .build()
                                )
                                .collectList()
                                .flatMap(pList -> {
                                    if (pList.isEmpty()) {
                                        return productRepository.findAll().take(20)
                                                .map(p -> ProductCustomerVO.builder()
                                                        .image(p.getImage())
                                                        .video(p.getVideo())
                                                        .status(p.getStatus())
                                                        .description(p.getDescription())
                                                        .publishTime(p.getPublishTime())
                                                        .brand(p.getBrand())
                                                        .id(p.getId())
                                                        .name(p.getName())
                                                        .level(p.getLevel())
                                                        .placeOfOrigin(p.getPlaceOfOrigin())
                                                        .minPrice(p.getMinPrice().setScale(2, RoundingMode.HALF_UP))
                                                        .maxPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                                                        .originalPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                                                        .discountPrice(
                                                                Optional.ofNullable(p.getMinPrice())
                                                                        .map(price -> price.multiply(new BigDecimal("0.7")))
                                                                        .map(price -> price.setScale(2, RoundingMode.HALF_UP))
                                                                        .orElse(BigDecimal.ZERO)
                                                        )
                                                        .placeOfOrigin(p.getPlaceOfOrigin())
                                                        .build()
                                                ).collectList();
                                    }
                                    return Mono.just(pList);
                                });
                    })
                    .onErrorResume(Mono::error);
        });
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
    public Mono<ProductCustomerDetailVO> detail(BigInteger id) {
       return Mono.deferContextual(ctx->{
        Mono<Product> productMono = productRepository.findById(id);
        Mono<List<TagVO>> tagListMono = utilsService.findTagByProductId(id);
        Mono<List<SKU>> skuListMono = sKURepository.findAllByProductId(id).collectList();
        BigInteger userId = ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
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
                                        .contextWrite(ctxs -> ctxs.put(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY,userId))
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
    public Mono<List<ProductCustomerVO>> findByIds(List<BigInteger> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Mono.just(Collections.emptyList());
        }

        return productRepository.findAllById(ids)
                .map(item -> {
                    // 安全获取 minPrice/maxPrice
                    BigDecimal minPrice = Optional.ofNullable(item.getMinPrice())
                            .orElse(BigDecimal.ZERO);
                    BigDecimal maxPrice = Optional.ofNullable(item.getMaxPrice())
                            .orElse(BigDecimal.ZERO);

                    // 统一精度：2位小数，四舍五入
                    minPrice = minPrice.setScale(2, RoundingMode.HALF_UP);
                    maxPrice = maxPrice.setScale(2, RoundingMode.HALF_UP);

                    // 计算折扣价（7折）
                    BigDecimal discountPrice = minPrice.multiply(BigDecimal.valueOf(0.7))
                            .setScale(2, RoundingMode.HALF_UP);

                    return ProductCustomerVO.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .image(item.getImage())
                            .video(item.getVideo())
                            .status(item.getStatus())
                            .description(item.getDescription())
                            .publishTime(item.getPublishTime())
                            .brand(item.getBrand())
                            .level(item.getLevel())
                            .placeOfOrigin(item.getPlaceOfOrigin())
                            .minPrice(minPrice)
                            .maxPrice(maxPrice)
                            .originalPrice(maxPrice)           // 原价 = maxPrice（与传统查询一致）
                            .discountPrice(discountPrice)
                            .build();
                })
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
                    List<BigInteger> productIds = purchaseOrderVOApis.stream()
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
                                List<BigInteger> categoryIds = productCategories.stream()
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
        Map<BigInteger, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity(), (v1, v2) -> v1));

        // 构建商品ID到分类列表的映射
        Map<BigInteger, List<CategoryApiVO>> productCategoryMap = productCategories.stream()
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
                    BigInteger productId = order.getProductId();

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
                                                                      Map<BigInteger, Double> ratioMap) {
        return ProductForEmbeddingApVO.builder()
                .id(clickProfileApi.getProduct().getId())
                .title(clickProfileApi.getProduct().getName())
                .brand(clickProfileApi.getProduct().getBrand())
                .score(ratioMap.get(clickProfileApi.getProduct().getId()))
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
                                                                        Map<BigInteger, Double> ratioMap) {
        return ProductForEmbeddingApVO.builder()
                .id(collectProfileApi.getProduct().getId())
                .title(collectProfileApi.getProduct().getName())
                .brand(collectProfileApi.getProduct().getBrand())
                .score(ratioMap.get(collectProfileApi.getProduct().getId()))
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
                                                                         Map<BigInteger, Double> ratioMap) {
        return ProductForEmbeddingApVO.builder()
                .id(order.getProductId())
                .title(order.getName())
                .score(ratioMap.get(order.getProductId()))
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
     * 从 Product 构建 ProductCustomerVO（提取公共逻辑）
     */
    private ProductCustomerVO buildProductCustomerVO(Product p) {
        return ProductCustomerVO.builder()
                .image(p.getImage())
                .video(p.getVideo())
                .status(p.getStatus())
                .description(p.getDescription())
                .publishTime(p.getPublishTime())
                .brand(p.getBrand())
                .id(p.getId())
                .name(p.getName())
                .level(p.getLevel())
                .placeOfOrigin(p.getPlaceOfOrigin())
                .minPrice(p.getMinPrice().setScale(2, RoundingMode.HALF_UP))
                .maxPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                .originalPrice(p.getMaxPrice().setScale(2, RoundingMode.HALF_UP))
                .discountPrice(Optional.ofNullable(p.getMinPrice())
                        .map(price -> price.multiply(new BigDecimal("0.7")))
                        .map(price -> price.setScale(2, RoundingMode.HALF_UP))
                        .orElse(BigDecimal.ZERO))
                .build();
    }

    @Override
    public Mono<List<ProductCustomerVO>> recommend() {
        // 获取用户行为记录
        Mono<ResultT<List<ClickProfileApi>>> apiUserClickRecord = userClickServiceApi.findUserClickRecord(ConstNumber.INT_TWO);
        Mono<ResultT<List<CollectProfileApi>>> apiUserCollectRecord = userCollectServiceApi.findUserCollectRecord(ConstNumber.INT_TWO);
        Mono<ResultT<List<SearchContentApi>>> apiUserSearchRecord = userSearchServiceApi.findUserSearchRecord(ConstNumber.INT_TWO);
        // 使用融合后的购买记录
        Mono<List<PurchaseOrderVOApi>> apiOrderRecord = findPurchaseOrdersWithProductInfo();

        return Mono.zip(apiUserClickRecord, apiUserCollectRecord, apiUserSearchRecord, apiOrderRecord)
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

                    // === 合并点击、收藏、购买的商品 ID 列表 ===
                    List<BigInteger> productIdList = Stream.concat(
                                    Stream.concat(
                                            Optional.of(clickProfileApiList).orElse(List.of()).stream()
                                                    .map(c -> c.getProduct().getId()),
                                            Optional.of(collectProfileApiList).orElse(List.of()).stream()
                                                    .map(c -> c.getProduct().getId())
                                    ),
                                    purchaseOrderList.stream()
                                            .map(PurchaseOrderVOApi::getProductId)
                                            .filter(Objects::nonNull)
                            )
                            .filter(Objects::nonNull)
                            .distinct()
                            .toList();

                    int totalProductId = productIdList.size();
                    final int totalIds = totalProductId == ConstNumber.INT_ZERO ? ConstNumber.INT_ONE : totalProductId;

                    // 计算每个商品 ID 的占比（点击+收藏+购买）
                    Map<BigInteger, Double> productRatioMap = productIdList.stream()
                            .collect(Collectors.groupingBy(
                                    Function.identity(),
                                    Collectors.collectingAndThen(
                                            Collectors.counting(),
                                            count -> count * ConstNumber.DOUBLE_ONE / totalIds
                                    )
                            ));

                    // 取占比排名前三的商品
                    Map<BigInteger, Double> top3ProductRatioMap = productRatioMap.entrySet().stream()
                            .sorted(Map.Entry.<BigInteger, Double>comparingByValue().reversed())
                            .limit(ConstNumber.INT_THREE)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (v1, v2) -> v1,
                                    LinkedHashMap::new
                            ));

                    Set<BigInteger> top3ProductIdSet = top3ProductRatioMap.keySet();

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
                                                                    .price(searchContentApi.getMinPrice().toString())
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

                    // === 将购买记录商品转为结构化推荐输入（新增） ===
                    purchaseOrderList.stream()
                            .filter(Objects::nonNull)
                            .filter(order -> top3ProductIdSet.contains(order.getProductId()))
                            .forEach(order ->
                                    productForEmbeddingApVOList.add(buildProductForEmbeddingFromPurchase(order, top3ProductRatioMap))
                            );

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
                                            .map(this::buildProductCustomerVO)
                                            .collectList();
                                }
                                List<BigInteger> recommendIds = recommendProductIds.getData();

                                return productRepository.findAllById(recommendIds)
                                        .collectMap(Product::getId)
                                        .flatMapMany(productMap ->
                                                Flux.fromIterable(recommendIds)
                                                        .map(productMap::get)
                                                        .filter(Objects::nonNull)
                                        )
                                        .map(this::buildProductCustomerVO)
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
        return null;
    }

    @Override
    public Mono<List<ProductCustomerVO>> mostNew() {
        return null;
    }
}