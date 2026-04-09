package com.guanshiyun.service.utils;

import com.guanshiyun.category.Category;
import com.guanshiyun.controller.tag.vo.TagVO;
import com.guanshiyun.product.Product;
import com.guanshiyun.sku.SKU;
import com.guanshiyun.tag.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UtilsService {
    Mono<List<TagVO>> findTagByProductId(Long productId);
    Mono<List<Product>> findProductByProductId(List<Long> productIds);

    Flux<Product> findProductPage(Long pageNum, int pageSize, String nameKeyword);
    Mono<List<Long>> findProductIdsByTotalSalesGreaterThan(Integer salesVolume);
    Flux<SKU> findAllByProductId(Long productId);

    Flux<Category> findAllByCategoryId(List<Long> categoryIds);
    //删除skuids
    Mono<Void>  deleteAllByProductId(Long productId);
    //根据分类id获取分类
    Mono<Category> findCategoryByCategoryId(Long categoryId);

    //根据tagIds获取tag
    Flux<Tag> findTagByTagIds(List<Long> tagIds);
}
