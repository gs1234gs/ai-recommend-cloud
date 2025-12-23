package com.guanshiyun.service.product.impl;

import cn.hutool.core.util.StrUtil;
import com.db.dbnumber.ConstNumber;
import com.db.page.CursorPageUtil;
import com.db.query.SafeCriteria;
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
import com.guanshiyun.publicbehaviorvo.CategoryApiVO;
import com.guanshiyun.publicbehaviorvo.ProductApiVO;
import com.guanshiyun.publicbehaviorvo.TagApiVO;
import com.guanshiyun.repository.product.ProductRepository;
import com.guanshiyun.repository.sku.SKURepository;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.apisave.UserClickSaveApiVO;
import com.guanshiyun.rpc.behaviorapi.click.UserClickServiceApi;
import com.guanshiyun.rpc.behaviorapi.collect.UserCollectServiceApi;
import com.guanshiyun.rpc.behaviorapi.search.UserSearchServiceApi;
import com.guanshiyun.rpc.chatrecommend.AiChatClientRecommendServiceApi;
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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    public Mono<CursorPageResult<List<ProductCustomerVO>>> findCursor(RequestCursorPage<ProductSearchSaveVO> requestCursorPage) {
        RequestCursorPage<ProductSearchSaveVO> validate = CursorPageUtil.validate(requestCursorPage, ProductSearchSaveVO.class);
        ProductSearchSaveVO condition = validate.getCondition();
        // 最后id
        BigInteger lastId = validate.getLastId();
        //每页数量
        Integer pageSize = validate.getPageSize();
        //排序参数
        String order = validate.getOrder();
        //商品最高价格
        BigDecimal maxPrice = condition.getMaxPrice();
        //商品最低价格
        BigDecimal minPrice = condition.getMinPrice();
        //搜索内容
        String searchContent = condition.getSearchContent();
        /**
         *
         * 这里需要使用协同过滤，大模型决策后规则来查询，暂时忽略
         * */
        return Mono.deferContextual(ctx -> {
            //没有登陆
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                //暂时返回空
                return Mono.empty();
            }
            //已经登陆
            BigInteger userId = ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
            SafeCriteria safeCriteria = SafeCriteria.safeCriteria();
            //暂时支持商品名称匹配，放弃分类并行，后面采用ElasticSearch引擎解决
            Criteria criteria = safeCriteria
                    .likeIfNotEmpty(Product.Fields.name, searchContent)
                    .geIfNotNull(Product.Fields.minPrice, minPrice)
                    .leIfNotNull(Product.Fields.minPrice, maxPrice)
                    .gtIfNotNull(Product.Fields.id, lastId)
                    .criteria();
            Query query = Query.query(criteria)
                    .limit(pageSize)
                    .sort(Sort.by(Sort.Order.asc(Product.Fields.id)));
            //查询商品，调用行为服务记录搜索记录，
            return r2dbcEntityTemplate
                    .select(Product.class)
                    .matching(query)
                    .all()
                    .map(p -> ProductCustomerVO
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
                            .minPrice(p.getMinPrice())
                            .maxPrice(p.getMaxPrice())
                            .placeOfOrigin(p.getPlaceOfOrigin())
                            .build()
                    )
                    .collectList()
                    .map(productCustomerVOS ->
                            CursorPageResult.<List<ProductCustomerVO>>builder()
                                    .rows(productCustomerVOS)
                                    .cursor(productCustomerVOS.getLast().getId())
                                    .hasNext(productCustomerVOS.size() > pageSize)
                                    .build()
                    )
                    .publishOn(Schedulers.boundedElastic())
                    .doOnSuccess(cursorPageResult ->
                            userSearchServiceApi.saveUserSearchRecord(
                                    BeanConvertUtil.toBean(condition, SearchContentApi.class)
                                            .setSearchTime(LocalDateTime.now())
                            ).subscribe()
                    );

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
    @Override
    public Mono<List<ProductCustomerVO>> recommend() {
        //已经登陆,调用推荐接口，推荐接口自动根据条件判断新用户还是老用户
        //浏览暂时不要
//            Mono<ResultT<List<BrowseProfileApi>>> apiUserBrowseRecord = userBrowseServiceApi.findUserBrowseRecord(ConstNumber.INTEGER_TEN);
        Mono<ResultT<List<ClickProfileApi>>> apiUserClickRecord = userClickServiceApi.findUserClickRecord(ConstNumber.INTEGER_TEN);
        Mono<ResultT<List<CollectProfileApi>>> apiUserCollectRecord = userCollectServiceApi.findUserCollectRecord(ConstNumber.INTEGER_TEN);
        Mono<ResultT<List<SearchContentApi>>> apiUserSearchRecord = userSearchServiceApi.findUserSearchRecord(ConstNumber.INTEGER_TEN);
        return Mono.zip(
                        apiUserClickRecord,
                        apiUserCollectRecord,
                        apiUserSearchRecord
                )
                .flatMap(tuple -> {
                    //处理搜索记录，按时间倒序
                    List<SearchContentApi> searchContentApiList =  Optional.ofNullable(tuple.getT3().getData())
                            .orElse(List.of())
                            .stream()
                            .filter(Objects::nonNull)
                            .sorted(Comparator.comparing(
                                    SearchContentApi::getSearchTime,
                                    Comparator.reverseOrder()
                            ))
                            .toList();
                    List<ClickProfileApi> clickProfileApiList = tuple.getT1().getData();
                    List<CollectProfileApi> collectProfileApiList = tuple.getT2().getData();
                    // === 合并点击与收藏的商品 ID 列表 ===
                    List<BigInteger> productIdList =
                            Stream.concat(
                                    clickProfileApiList.stream()
                                            .map(c -> c.getProduct().getId()),
                                    collectProfileApiList.stream()
                                            .map(c -> c.getProduct().getId())
                            ).toList();
                    int totalProductId = productIdList.size();
                  final int  totalIds = totalProductId == ConstNumber.INT_ZERO ? ConstNumber.INT_ONE : totalProductId;
                    //  计算每个商品 ID 的占比
                    Map<BigInteger, Double> productRatioMap =
                            productIdList.stream()
                                    .collect(Collectors.groupingBy(
                                            Function.identity(),          // 商品ID作为key
                                            Collectors.collectingAndThen(
                                                    Collectors.counting(), // 统计次数
                                                    count -> count * ConstNumber.DOUBLE_ONE / totalIds// 计算占比
                                            )
                                    ));
                    // 取占比排名前三的商品
                    Map<BigInteger, Double> top3ProductRatioMap =
                            productRatioMap.entrySet()
                                    .stream()
                                    // 按占比倒序排序
                                    .sorted(Map.Entry.<BigInteger, Double>comparingByValue().reversed())
                                    // 取前三
                                    .limit(ConstNumber.INT_THREE)
                                    // 收集回 Map（保持排序）
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            Map.Entry::getValue,
                                            (v1, v2) -> v1,
                                            LinkedHashMap::new
                                    ));

                    Set<BigInteger> top3ProductIdSet = top3ProductRatioMap.keySet();

                    //构造用于大模型推荐的输入数据
                    List<ProductForEmbeddingApVO> productForEmbeddingApVOList = new ArrayList<>();
                    // === 将最新3条搜索内容转为虚拟商品（仅含关键词） ===
                    Optional.of(searchContentApiList)
                            .orElseThrow()
                            .stream()
                            .limit(ConstNumber.INT_THREE)
                            .filter(Objects::nonNull)
                            .forEach(searchContentApi ->
                                    productForEmbeddingApVOList.add(
                                            ProductForEmbeddingApVO.builder()
                                                    .brand(searchContentApi.getSearchContent())
                                                    .title(searchContentApi.getSearchContent())
                                                    .categoryNames(List.of(searchContentApi.getSearchContent()))
                                                    .skuList(List.of(
                                                                    ProductForEmbeddingApVO
                                                                            .SkuItem
                                                                            .builder()
                                                                            .name(searchContentApi.getSearchContent())
                                                                            .price(searchContentApi.getMinPrice().toString())
                                                                            .build()
                                                            )
                                                    )
                                                    .tagNames(List.of(searchContentApi.getSearchContent()))
                                                    .build())
                            );
                    // === 将 Top3 点击商品转为结构化推荐输入 ===
                    Optional.of(clickProfileApiList)
                            .orElseThrow()
                            .stream()
                            .filter(Objects::nonNull)
                            .filter(clickProfileApi -> top3ProductIdSet.contains(clickProfileApi.getProduct().getId()))
                            .forEach(clickProfileApi ->
                                    productForEmbeddingApVOList.add(
                                            ProductForEmbeddingApVO.builder()
                                                    .id(clickProfileApi.getProduct().getId())
                                                    .title(clickProfileApi.getProduct().getName())
                                                    .brand(clickProfileApi.getProduct().getBrand())
                                                    .score(top3ProductRatioMap.get(clickProfileApi.getProduct().getId()))
                                                    .tagNames(
                                                            clickProfileApi.getTagList()
                                                                    .stream()
                                                                    .map(TagApiVO::getName)
                                                                    .collect(Collectors.toList())
                                                    )
                                                    .skuList(
                                                            clickProfileApi.getSkuList()
                                                                    .stream()
                                                                    .map(skuApiVO ->
                                                                            ProductForEmbeddingApVO.SkuItem.builder()
                                                                                    .name(skuApiVO.getName())
                                                                                    .price(skuApiVO.getPrice().toString())
                                                                                    .id(skuApiVO.getId().toString())
                                                                                    .skuCode(skuApiVO.getSkuCode())
                                                                                    .build()
                                                                    )
                                                                    .collect(Collectors.toList())
                                                    )
                                                    .categoryNames(
                                                            clickProfileApi.getCategoryList()
                                                                    .stream()
                                                                    .map(CategoryApiVO::getName)
                                                                    .collect(Collectors.toList())
                                                    )
                                                    .placeOfOrigin(clickProfileApi.getProduct().getPlaceOfOrigin())
                                                    .description(clickProfileApi.getProduct().getDescription())
                                                    .build()
                                    ));
                    // === 将 Top3 收藏商品转为结构化推荐输入（去重逻辑由大模型处理）===
                    Optional.of(collectProfileApiList)
                            .orElseThrow()
                            .stream()
                            .filter(Objects::nonNull)
                            .filter(collect -> top3ProductIdSet.contains(collect.getProduct().getId()))
                            .forEach(collectProfileApi ->
                                    productForEmbeddingApVOList.add(
                                            ProductForEmbeddingApVO.builder()
                                                    .id(collectProfileApi.getProduct().getId())
                                                    .title(collectProfileApi.getProduct().getName())
                                                    .brand(collectProfileApi.getProduct().getBrand())
                                                    .score(top3ProductRatioMap.get(collectProfileApi.getProduct().getId()))
                                                    .description(collectProfileApi.getProduct().getDescription())
                                                    .placeOfOrigin(collectProfileApi.getProduct().getPlaceOfOrigin())
                                                    .categoryNames(
                                                            collectProfileApi.getCategoryList()
                                                                    .stream()
                                                                    .map(CategoryApiVO::getName)
                                                                    .collect(Collectors.toList())
                                                    )
                                                    .tagNames(
                                                            collectProfileApi.getTagList()
                                                                    .stream()
                                                                    .map(TagApiVO::getName)
                                                                    .collect(Collectors.toList())
                                                    )
                                                    .skuList(
                                                            collectProfileApi.getSkuList()
                                                                    .stream()
                                                                    .map(skuApiVO ->
                                                                            ProductForEmbeddingApVO.SkuItem.builder()
                                                                                    .name(skuApiVO.getName())
                                                                                    .price(skuApiVO.getPrice().toString())
                                                                                    .id(skuApiVO.getId().toString())
                                                                                    .skuCode(skuApiVO.getSkuCode())
                                                                                    .build()
                                                                    )
                                                                    .toList()
                                                    )
                                                    .build()
                                    )
                            );
                    RequestBodyProductForEmbeddingApVO<List<ProductForEmbeddingApVO>> requestBodyProductForEmbeddingApVO =
                            RequestBodyProductForEmbeddingApVO.<List<ProductForEmbeddingApVO>>builder()
                                    .topK(ConstNumber.INT_HUNDRED)
                                    .data(productForEmbeddingApVOList)
                                    .build();
                    return aiChatClientRecommendServiceApi.recommendProduct(requestBodyProductForEmbeddingApVO)
                            .flatMap(recommendProductIds ->
                                    productRepository.findAllById(recommendProductIds.getData())
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
                                                            .minPrice(p.getMinPrice())
                                                            .maxPrice(p.getMaxPrice())
                                                            .placeOfOrigin(p.getPlaceOfOrigin())
                                                            .build()

                                            )
                                            .collectList()
                            );
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
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Mono.error(new Exception("请先登陆"));
            }
            BigInteger userId = ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
            return gorseClient.getRecommend(userId.toString(), 100)
                    .flatMap(productIds -> {
                        List<BigInteger> productIdList = productIds.stream().filter(StrUtil::isNotBlank)
                                .map(myBigInteger::bigInteger)
                                .toList();
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
                                                .minPrice(p.getMinPrice())
                                                .maxPrice(p.getMaxPrice())
                                                .placeOfOrigin(p.getPlaceOfOrigin())
                                                .build()
                                )
                                .collectList();
                    });
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
        Mono<Product> productMono = productRepository.findById(id);
        Mono<List<TagVO>> tagListMono = utilsService.findTagByProductId(id);
        Mono<List<SKU>> skuListMono = sKURepository.findAllByProductId(id).collectList();
        return Mono.zip(productMono, tagListMono,skuListMono)
                .flatMap(tuple -> {
                    Product product = tuple.getT1();
                    List<TagVO> tagList = tuple.getT2();
                    List<SKU> skuList = tuple.getT3();
                    return Mono.just(
                            ProductCustomerDetailVO.builder()
                                    .id(product.getId())
                                    .level(product.getLevel())
                                    .name(product.getName())
                                    .maxPrice(product.getMaxPrice())
                                    .minPrice(product.getMinPrice())
                                    .offlineTime(product.getOfflineTime())
                                    .placeOfOrigin(product.getPlaceOfOrigin())
                                    .publishTime(product.getPublishTime())
                                    .brand(product.getBrand())
                                    .status(product.getStatus())
                                    .tagList(tagList)
                                    .skuList(BeanConvertUtil.toBeanList(skuList, SKUVO.class))
                                    .build()
                    )
                            .publishOn(Schedulers.boundedElastic())
                            .doOnSuccess(ok->{
                                // 异步记录点击行为
                                userClickServiceApi.saveUserClickRecord(UserClickSaveApiVO
                                        .builder()
                                        .product(BeanConvertUtil.toBean(product, ProductApiVO.class))
                                        .clickTime(LocalDateTime.now())
                                        .build())
                                        .subscribe();
                            });
                });
    }
}