package com.guanshiyun.service.embedding;

import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import reactor.core.publisher.Mono;


import java.util.List;
/**
 * EmbeddingProductService
 *
 * 作用：
 * 1. 提供商品向量管理和推荐相关的接口
 * 2. 支持批量保存、删除商品向量
 * 3. 基于向量实现用户实时推荐
 * 4. 可将用户历史行为向量化，用于语义检索推荐
 *
 * 技术特点：
 * - Reactive 编程（Mono 异步返回）
 * - 支持向量检索、向量存储操作
 * - 与推荐系统（如 Gorse）结合进行 fallback 推荐
 */
public interface EmbeddingProductService {

     /**
      * 批量保存商品向量
      *
      * 核心逻辑：
      * - 将商品信息转为向量（语义文本 + metadata）
      * - 批量保存到 VectorStore
      *
      * @param tList 商品列表
      * @return Mono<List<String>> 返回保存成功的向量 ID 列表
      */
     Mono<List<String>> saveBatch(List<ProductForEmbeddingApVO> tList);
     /**
      * 删除商品及其向量
      *
      * 核心逻辑：
      * - 根据商品 ID 删除 VectorStore 中的向量
      *
      * @param idList 商品 ID 列表
      * @return Mono<Void>
      */
     Mono<Void> deleteById(List<Long> idList);
     /**
      * 基于用户历史行为实时推荐商品
      *
      * 核心逻辑：
      * - 根据用户最近浏览商品向量进行相似度搜索
      * - 去重、排序、取 topK
      * - 对新用户或未命中情况调用推荐系统（如 Gorse）补充推荐
      *
      * @param recentProducts 用户最近浏览的商品列表
      * @param topK 返回推荐数量
      * @return Mono<List<Long>> 推荐商品 ID 列表
      */
     Mono<List<Long>> recommendForUser(List<ProductForEmbeddingApVO> recentProducts, int topK);
     /**
      * 向量化用户历史行为
      *
      * 核心逻辑：
      * - 将用户历史浏览的商品转为向量表示
      * - 用于语义检索推荐或向量相似度计算
      *
      * @param recentProducts 用户最近浏览商品列表
      * @return Mono<List<String>> 用户历史商品向量列表（未实现）
      */
     Mono<List<String>> vectorizeUserHistory(List<ProductForEmbeddingApVO> recentProducts);
     /**
      * 根据关键词搜索商品
      * */
     Mono<List<Long>> searchByKeyword(String keyword, int topK);
     List<Long> searchKeyword(String keyword, int topK);

}
