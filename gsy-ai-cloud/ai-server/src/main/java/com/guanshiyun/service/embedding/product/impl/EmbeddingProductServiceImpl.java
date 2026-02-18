package com.guanshiyun.service.embedding.product.impl;

import com.db.dbnumber.ConstNumber;
import com.guanshiyun.behaviorenums.GuestEnum;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.embedding.ActiveSimilarityThresholdConfiguration;
import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.repository.embedding.ActiveSimilarityThresholdConfigurationRepository;
import com.guanshiyun.service.embedding.product.EmbeddingProductService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * EmbeddingProductServiceImpl
 * <p>
 * 作用：
 * 1. 商品向量管理服务
 * - 将商品信息向量化并保存到 VectorStore
 * - 支持删除向量
 * 2. 基于向量的用户实时推荐
 * - 结合用户最近浏览历史的商品向量进行语义检索
 * - 支持冷启动、新商品推荐
 * 3. 与 Gorse 推荐系统结合
 * - 对冷启动、新用户或未命中向量检索时 fallback
 * <p>
 * 技术特点：
 * - 使用 Reactive 编程 (Mono / Flux) 异步处理
 * - 阻塞调用 VectorStore 使用 boundedElastic 线程池
 * - 支持批量操作和 topK 推荐
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingProductServiceImpl implements EmbeddingProductService {
    private final VectorStore vectorStore;// 向量存储服务
    private final MyBigInteger myBigInteger;// BigInteger 工具
    private final GorseClient gorseClient;// Gorse 推荐客户端
    private final ActiveSimilarityThresholdConfigurationRepository activeSimilarityThresholdConfigurationRepository;

    /**
     * 批量保存商品向量
     * <p>
     * 核心流程：
     * 1. 将每个商品封装成 Document 对象（语义文本 + metadata）
     * 2. 调用 VectorStore 批量保存
     * 3. 返回所有保存成功的 Document ID 列表
     *
     * @param products 商品列表
     * @return Mono<List < String>> 保存成功的向量 ID
     */
    @Override
    public Mono<List<String>> saveBatch(List<ProductForEmbeddingApVO> products) {
        return Flux.fromIterable(products)
                .map(product ->
                        Document.builder()
                                .id(generateVectorId(product.getId()))
                                .text(product.recommendEmbeddingText())   // 只放语义
                                .metadata(product.metadata())             // 非语义信息
                                .build()
                )
                .collectList()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(vectorStore::add) // 统一交给 VectorStore
                .flatMapMany(docs ->
                        Flux.fromIterable(docs)
                                .map(Document::getId)
                )
                .collectList();
    }

    /**
     * 删除商品及其向量
     *
     * @param idList 商品 ID 列表
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> deleteById(List<BigInteger> idList) {
        List<String> vectorIds = idList.stream()
                .map(this::generateVectorId) // 使用统一生成逻辑
                .toList();
        return Mono.fromCallable(() -> {
                    vectorStore.delete(vectorIds);
                    return true;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    /**
     * 实时推荐商品给用户
     * <p>
     * 核心逻辑：
     * 1. 获取用户最近浏览商品向量
     * 2. 对每个商品进行向量相似度搜索
     * 3. 去重、排序、取 topK
     * 4. 若未返回 topK 或新用户，调用 Gorse 推荐作为 fallback
     *
     * @param recentProducts 用户最近浏览商品列表
     * @param topK           返回商品数量
     * @return Mono<List < BigInteger>> 推荐商品 ID 列表
     */
    @Override
    public Mono<List<BigInteger>> recommendForUser(List<ProductForEmbeddingApVO> recentProducts, int topK) {
        //有行为，优先大模型推荐，解决商品冷启动，新商品需要语义检索推荐
        return Mono.deferContextual(ctx -> {
                    boolean hasKey =
                            ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
                    // ================== 游客处理 ==================
                    if (!hasKey) {
                        return gorseClient(GuestEnum.GUEST_USER_ID.getValue(), topK);
                    }
                    BigInteger userId = myBigInteger.bigIntegerOrNull(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));


                    // ================== 无行为：直接走 Gorse ==================
                    if (Objects.isNull(recentProducts) || recentProducts.isEmpty()) {
                        return gorseClient(userId.toString(), topK);
                    }
                    // ===== 构造融合 Query（例如：拼接最近商品的语义文本）=====
                    String fusedQuery = recentProducts.stream()
                            .limit(3) // 取最近 3 个避免过长
                            .map(ProductForEmbeddingApVO::recommendEmbeddingText)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(" "));
                    if (fusedQuery.isBlank()) {
                        return gorseClient(userId.toString(), topK);
                    }
//            SearchRequest request = SearchRequest.builder()
//                    .query(fusedQuery)
//                    .similarityThresholdAll()
//                    .topK(topK) // 多取一点，留去重空间
//                    .build();
                    // ================== 根据历史商品向量推荐 ==================
//            return Flux.fromIterable(recentProducts)
//                    .flatMap(product -> {
//                        String query = product.recommendEmbeddingText();
//                        SearchRequest request = SearchRequest.builder()
//                                .query(query)
////                            .similarityThreshold(0.3f)
//                                .topK(topK)
//                                .build();
//                        // 包装同步调用为 Mono 异步
//                        return Mono.fromCallable(() -> vectorStore.similaritySearch(request))
//                                .subscribeOn(Schedulers.boundedElastic())   // 阻塞调用异步化
//                                .flatMapMany(Flux::fromIterable);
//                    })
                    return activeSimilarityThresholdConfigurationRepository
                            .findById(BigInteger.ONE)
                            .map(ActiveSimilarityThresholdConfiguration::getSimilarityThreshold)
                            .defaultIfEmpty(0.0)
                            .flatMap(threshold -> {
                                SearchRequest request = SearchRequest.builder()
                                        .query(fusedQuery)
                                        .similarityThreshold(threshold)
                                        .topK(topK) // 多取一点，留去重空间
                                        .build();
                                return Flux.fromIterable(vectorStore.similaritySearch(request))
                                        // ===== 去重：保留最高分 =====
                                        .collectMultimap(Document::getId)
                                        .flatMapMany(idToDocsMap ->
                                                Flux.fromIterable(idToDocsMap.entrySet())
                                                        .map(entry ->
                                                                entry.getValue()
                                                                        .stream()
                                                                        .max(Comparator.comparing(Document::getScore,
                                                                                        Comparator.nullsLast(Comparator.naturalOrder())
                                                                                )
                                                                        )
                                                                        .orElseThrow()
                                                        )
                                        )
                                        // ===== 按 score 降序 =====
                                        .sort((d1, d2) -> {
                                            Double s1 = d1.getScore();
                                            Double s2 = d2.getScore();
                                            return Objects.isNull(s1) &&
                                                    Objects.isNull(s2) ?
                                                    ConstNumber.INT_ZERO : (Objects.isNull(s1) ?
                                                    ConstNumber.INT_ONE : Objects.isNull(s2) ?
                                                    ConstNumber.INT_MINUS_ONE : s2.compareTo(s1)
                                            );
                                        })
                                        .take(topK)    // 最终返回 topK
                                        .map(document -> myBigInteger.bigInteger(
                                                document.getMetadata().get(ProductForEmbeddingApVO.Fields.id))
                                        )
                                        .collectList()
                                        .flatMap(productIdList -> {
                                            // 如果返回不足 topK，调用 Gorse 补充推荐
                                            if (productIdList.size() == topK) {
                                                return Mono.just(productIdList);
                                            }
                                            //1.有推荐，但是没有返回topK
                                            if (!productIdList.isEmpty() && productIdList.size() < topK) {
                                                return gorseClient.getRecommend(userId.toString(), topK - productIdList.size())
                                                        .map(productIds -> {
                                                            ArrayList<BigInteger> cloneProductIdList = new ArrayList<>(productIdList);
                                                            productIds.forEach(productId ->
                                                                    cloneProductIdList.add(myBigInteger.bigInteger(productId))
                                                            );
                                                            return cloneProductIdList;
                                                        });
                                            }
                                            // 新用户冷启动
                                            return gorseClient(userId.toString(), topK);
                                        });
                            })
                            .defaultIfEmpty(Collections.emptyList());

//            return Flux.fromIterable(vectorStore.similaritySearch(request))
//                    // ===== 去重：保留最高分 =====
//                    .collectMultimap(Document::getId)
//                    .flatMapMany(idToDocsMap ->
//                            Flux.fromIterable(idToDocsMap.entrySet())
//                                    .map(entry ->
//                                            entry.getValue()
//                                                    .stream()
//                                                    .max(Comparator.comparing(Document::getScore,
//                                                                    Comparator.nullsLast(Comparator.naturalOrder())
//                                                            )
//                                                    )
//                                                    .orElseThrow()
//                                    )
//                    )
//                    // ===== 按 score 降序 =====
//                    .sort((d1, d2) -> {
//                        Double s1 = d1.getScore();
//                        Double s2 = d2.getScore();
//                        return Objects.isNull(s1) &&
//                                Objects.isNull(s2) ?
//                                ConstNumber.INT_ZERO : (Objects.isNull(s1) ?
//                                ConstNumber.INT_ONE : Objects.isNull(s2) ?
//                                ConstNumber.INT_MINUS_ONE : s2.compareTo(s1)
//                        );
////                    if (Objects.isNull(s1) && Objects.isNull(s2)) return ConstNumber.INT_ZERO;
////                    if (Objects.isNull(s1)) return ConstNumber.INT_ONE;   // null 放后面
////                    if (Objects.isNull(s2)) return ConstNumber.INT_MINUS_ONE;  // null 放后面
////                    return s2.compareTo(s1);    // 降序：s2 > s1 → 负数？不，compareTo 是 s2 - s1
//                    })
//                    .take(topK)    // 最终返回 topK
//                    .map(document -> myBigInteger.bigInteger(
//                            document.getMetadata().get(ProductForEmbeddingApVO.Fields.id))
//                    )
//                    .collectList()
//                    .flatMap(productIdList -> {
//                        // 如果返回不足 topK，调用 Gorse 补充推荐
//                        if (productIdList.size() == topK) {
//                            return Mono.just(productIdList);
//                        }
//                        //1.有推荐，但是没有返回topK
//                        if (!productIdList.isEmpty() && productIdList.size() < topK) {
//                            return gorseClient.getRecommend(userId.toString(), topK - productIdList.size())
//                                    .map(productIds -> {
//                                        ArrayList<BigInteger> cloneProductIdList = new ArrayList<>(productIdList);
//                                        productIds.forEach(productId ->
//                                                cloneProductIdList.add(myBigInteger.bigInteger(productId))
//                                        );
//                                        return cloneProductIdList;
//                                    });
//                        }
//                        // 新用户冷启动
//                        return gorseClient(userId.toString(), topK);
//                    });
                }
        );
    }

    /**
     * 内部方法：调用 Gorse 推荐
     *
     * @param userId 用户 ID
     * @param topK   推荐数量
     * @return Mono<List < BigInteger>> 推荐商品 ID
     */
    private Mono<List<BigInteger>> gorseClient(String userId, Integer topK) {
        return gorseClient.getRecommend(userId, topK)
                .flatMap(productIds ->
                        Flux.fromIterable(productIds)
                                .map(myBigInteger::bigInteger
                                )
                                .collectList()
                );
    }

    /**
     * TODO: 将用户历史商品向量化
     *
     * @param recentProducts 用户最近浏览商品
     * @return Mono<List < String>>
     */
    @Override
    public Mono<List<String>> vectorizeUserHistory(List<ProductForEmbeddingApVO> recentProducts) {
        return null;
    }

    @Override
    public Mono<List<BigInteger>> searchByKeyword(String keyword, int topK) {
                    SearchRequest request = SearchRequest.builder()
                            .similarityThreshold(0.3)
                            .query(keyword)
                            .topK(topK)
                            .build();
                    return Flux.fromIterable(vectorStore.similaritySearch(request))
                            .map(document ->
                                    myBigInteger
                                            .bigInteger(document
                                                    .getMetadata()
                                                    .get(ProductForEmbeddingApVO
                                                            .Fields.id)
                                            ))
                            .collectList()
                            .map(list->{
                                log.info("searchByKeyword {}: {}",keyword, list);
                                return list;
                            })
                            .defaultIfEmpty(Collections.emptyList());
//        SearchRequest request = SearchRequest.builder()
//                .query(keyword)
//                .topK(topK)
//                .build();
//        return Flux.fromIterable(vectorStore.similaritySearch(request))
//                .map(document -> myBigInteger.bigInteger(document.getMetadata().get(ProductForEmbeddingApVO.Fields.id)))
//                .collectList();


    }


    @Override
    public List<BigInteger> searchKeyword(String keyword, int topK) {
        SearchRequest request = SearchRequest.builder()
                .query(keyword)
                .similarityThreshold(0.4)
                .topK(topK)
                .build();
        return vectorStore.similaritySearch(request)
                .stream()
                .map(document -> myBigInteger.bigInteger(document.getMetadata().get(ProductForEmbeddingApVO.Fields.id)))
                .collect(Collectors.toList());


    }

    private String generateVectorId(BigInteger productId) {
        return UUID.nameUUIDFromBytes(productId.toString().getBytes(StandardCharsets.UTF_8)).toString();
    }
}
