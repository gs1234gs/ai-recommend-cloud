package com.guanshiyun.service.product.help;

import reactor.core.publisher.Mono;


public class HelpUtil {
    /**
     * 填充池子逻辑
     */
    //    private Mono<Void> refillPoolWithAiLogic(String poolKey) {
//        Mono<ResultT<List<ClickProfileApi>>> clickMono = userClickServiceApi.findUserClickRecord(ConstNumber.INT_TWO);
//        Mono<ResultT<List<CollectProfileApi>>> collectMono = userCollectServiceApi.findUserCollectRecord(ConstNumber.INT_TWO);
//        Mono<ResultT<List<SearchContentApi>>> searchMono = userSearchServiceApi.findUserSearchRecord(ConstNumber.INT_TWO);
//        Mono<List<PurchaseOrderVOApi>> orderMono = findPurchaseOrdersWithProductInfo();
//        Mono<ResultT<List<BrowseProfileApi>>> apiUserBrowseRecord = userBrowseServiceApi.findUserBrowseRecord(ConstNumber.INT_TWO);
//
//        return Mono.zip(clickMono, collectMono, searchMono, orderMono, apiUserBrowseRecord)
//                .flatMap(tuple -> {
//                    //点击
//                    List<ClickProfileApi> clickList = Optional.ofNullable(tuple.getT1().getData()).orElse(List.of());
//                    //收藏
//                    List<CollectProfileApi> collectList = Optional.ofNullable(tuple.getT2().getData()).orElse(List.of());
//                    //搜索
//                    List<SearchContentApi> searchList = Optional.ofNullable(tuple.getT3().getData())
//                            .orElse(List.of())
//                            .stream()
//                            .sorted(Comparator.comparing(SearchContentApi::getSearchTime, Comparator.reverseOrder()))
//                            .toList();
//                    List<PurchaseOrderVOApi> purchaseList = tuple.getT4();
//                    // 解析浏览记录
//                    List<BrowseProfileApi> browseList = Optional.ofNullable(tuple.getT5().getData()).orElse(List.of());
//
//                    //  合并所有行为的商品 ID (点击 + 收藏 + 购买 + 浏览)
//                    List<Long> allProductIds = Stream.concat(
//                                    Stream.concat(
//                                            Stream.concat(
//                                                    // 点击
//                                                    clickList.stream()
//                                                            .map(c -> c.getProduct().getId()),
//                                                    // 收藏
//                                                    collectList.stream()
//                                                            .map(c -> c.getProduct().getId())
//                                            ),
//                                            // 购买
//                                            purchaseList.stream()
//                                                    .map(PurchaseOrderVOApi::getProductId)
//                                                    .filter(Objects::nonNull)
//                                    ),
//                                    // 【新增】浏览 (注意 flatMap 展开 List<ProductApiVO>)
//                                    browseList.stream()
//                                            .filter(Objects::nonNull)
//                                            .flatMap(b -> Optional.ofNullable(b.getProduct()).orElse(List.of()).stream())
//                                            .map(ProductApiVO::getId)
//                                            .filter(Objects::nonNull)
//                            )
//                            .filter(Objects::nonNull)
//                            .distinct()
//                            .toList();
//
//
//                    int totalIds = allProductIds.isEmpty() ? 1 : allProductIds.size();
//                    Map<Long, Double> ratioMap = allProductIds.stream()
//                            .collect(Collectors.groupingBy(Function.identity(), Collectors.collectingAndThen(
//                                    Collectors.counting(), c -> c * 1.0 / totalIds)));
//
//                    Map<Long, Double> top3RatioMap = ratioMap.entrySet().stream()
//                            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
//                            .limit(3)
//                            .collect(Collectors
//                                    .toMap(Map
//                                                    .Entry::getKey,
//                                            Map.Entry::getValue,
//                                            (v1, v2) -> v1,
//                                            LinkedHashMap::new)
//                            );
//
//                    Set<Long> top3Ids = top3RatioMap.keySet();
//                    List<ProductForEmbeddingApVO> embeddingInput = new ArrayList<>();
//                    //  构造 Embedding 输入数据
//                    // 搜索关键词 (虚拟商品)
//                    searchList.stream().limit(3).forEach(s ->
//                            embeddingInput.add(ProductForEmbeddingApVO.builder()
//                                    .title(s.getSearchContent())
//                                    .brand(s.getSearchContent())
//                                    .categoryNames(List.of(s.getSearchContent()))
//                                    .tagNames(List.of(s.getSearchContent()))
//                                    .build())
//                    );
//                    // 点击商品 (Top3)
//                    clickList.stream().filter(c -> top3Ids.contains(c.getProduct().getId()))
//                            .forEach(c -> embeddingInput.add(buildProductForEmbeddingFromClick(c, top3RatioMap)));
//                    //收藏商品 (Top3)
//                    collectList.stream().filter(c -> top3Ids.contains(c.getProduct().getId()))
//                            .forEach(c -> embeddingInput.add(buildProductForEmbeddingFromCollect(c, top3RatioMap)));
//                    //买商品 (Top3)
//                    purchaseList.stream().filter(o -> top3Ids.contains(o.getProductId()))
//                            .forEach(o -> embeddingInput.add(buildProductForEmbeddingFromPurchase(o, top3RatioMap)));
//                    // 浏览商品 (Top3)
//                    // 需要保留 browse 对象以获取 categoryList/tagList/skuList
//                    browseList.stream()
//                            .filter(Objects::nonNull)
//                            .flatMap(browse -> {
//                                List<ProductApiVO> products = Optional.ofNullable(browse.getProduct()).orElse(List.of());
//                                return products.stream()
//                                        .filter(Objects::nonNull)
//                                        .filter(p -> top3Ids.contains(p.getId()))
//                                        .map(p -> new AbstractMap.SimpleEntry<>(p, browse)); // 绑定商品和父对象
//                            })
//                            .forEach(entry -> {
//                                ProductApiVO product = entry.getKey();
//                                BrowseProfileApi browseRecord = entry.getValue();
//
//                                ProductForEmbeddingApVO vo = buildProductForEmbeddingFromBrowse(
//                                        product,
//                                        browseRecord.getCategoryList(),
//                                        browseRecord.getTagList(),
//                                        browseRecord.getSkuList(),
//                                        top3RatioMap
//                                );
//                                if (vo != null) {
//                                    embeddingInput.add(vo);
//                                }
//                            });
//                    RequestBodyProductForEmbeddingApVO<List<ProductForEmbeddingApVO>> requestBody =
//                            RequestBodyProductForEmbeddingApVO.<List<ProductForEmbeddingApVO>>builder()
//                                    .topK(ProductKey.RECOMMEND_POOL_SIZE)
//                                    .data(embeddingInput)
//                                    .build();
//
//                    return aiChatClientRecommendServiceApi.recommendProduct(requestBody)
//                            .map(resp -> Optional.ofNullable(resp.getData()).orElse(Collections.emptyList()))
//                            .onErrorResume(e -> {
//                                log.warn("AI 推荐服务调用失败，降级为纯热门/新品策略", e);
//                                return Mono.just(Collections.emptyList());
//                            })
//                            .flatMap(aiIds -> {
//                                int totalSize = ProductKey.RECOMMEND_POOL_SIZE;
//                                int aiCount = (int) (totalSize * 0.65);
//                                int hotCount = (int) (totalSize * 0.25);
//                                int newCount = totalSize - aiCount - hotCount;
//
//                                List<Long> validAiIds = aiIds.stream().limit(aiCount).toList();
//                                List<Long> finalPoolIds = new ArrayList<>(validAiIds);
//
//                                if (finalPoolIds.size() < totalSize) {
//                                    // 补充热门商品
//                                    Mono<List<Long>> hotMono = utilsService
//                                            .findProductIdsByTotalSalesGreaterThan(ConstNumber.INT_HUNDRED)
//                                            .defaultIfEmpty(Collections.emptyList())
//                                            .map(list -> list.stream()
//                                                    .filter(id -> !finalPoolIds
//                                                            .contains(id)).limit(hotCount)
//                                                    .toList()
//                                            );
//
//                                    Mono<List<Long>> newMono = productRepository.findAll()
//                                            .sort(Comparator.comparing(Product::getPublishTime).reversed())
//                                            .map(Product::getId)
//                                            .collectList()
//                                            .map(list -> list
//                                                    .stream()
//                                                    .filter(id -> !finalPoolIds
//                                                            .contains(id))
//                                                    .limit(newCount)
//                                                    .toList()
//                                            );
//
//                                    return Mono.zip(hotMono, newMono)
//                                            .map(t -> {
//                                                finalPoolIds.addAll(t.getT1());
//                                                finalPoolIds.addAll(t.getT2());
//                                                return finalPoolIds;
//                                            });
//                                }
//                                return Mono.just(finalPoolIds);
//                            })
//                            .flatMap(ids -> writeIdsToPoolInternal(poolKey, ids));
//                });
//    }
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
//    @Override
//    public Mono<List<ProductCustomerVO>> recommend() {
//        //已经登陆,调用推荐接口，推荐接口自动根据条件判断新用户还是老用户
//        //浏览暂时不要
//           Mono<ResultT<List<BrowseProfileApi>>> apiUserBrowseRecord = userBrowseServiceApi.findUserBrowseRecord(ConstNumber.INTEGER_TEN);
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
//                    List<Long> orderPids =
//                            purchaseOrderList.stream().map(PurchaseOrderVOApi::getProductId).toList();
//                    Flux<Product> allById = productRepository.findAllById(orderPids);
//                    // === 合并点击与收藏的商品 ID 列表 ===
//                    List<Long> productIdList =
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
//                    Map<Long, Double> productRatioMap =
//                            productIdList.stream()
//                                    .collect(Collectors.groupingBy(
//                                            Function.identity(),          // 商品ID作为key
//                                            Collectors.collectingAndThen(
//                                                    Collectors.counting(), // 统计次数
//                                                    count -> count * ConstNumber.DOUBLE_ONE / totalIds// 计算占比
//                                            )
//                                    ));
//                    // 取占比排名前三的商品
//                    Map<Long, Double> top3ProductRatioMap =
//                            productRatioMap.entrySet()
//                                    .stream()
//                                    // 按占比倒序排序
//                                    .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
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
//                    Set<Long> top3ProductIdSet = top3ProductRatioMap.keySet();
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
}
