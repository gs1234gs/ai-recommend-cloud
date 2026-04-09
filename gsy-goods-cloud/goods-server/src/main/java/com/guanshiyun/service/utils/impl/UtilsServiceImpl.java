package com.guanshiyun.service.utils.impl;

import cn.hutool.core.bean.BeanUtil;
import com.guanshiyun.category.Category;
import com.guanshiyun.controller.tag.vo.TagVO;
import com.guanshiyun.product.Product;
import com.guanshiyun.repository.category.CategoryRepository;
import com.guanshiyun.repository.product.ProductRepository;
import com.guanshiyun.repository.relation.ProductTagRepository;
import com.guanshiyun.repository.sku.SKURepository;
import com.guanshiyun.repository.tag.TagRepository;
import com.guanshiyun.service.utils.UtilsService;
import com.guanshiyun.sku.SKU;
import com.guanshiyun.tag.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilsServiceImpl implements UtilsService {
    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;
    private final ProductRepository productRepository;
    private final SKURepository skuRepository;
    private final CategoryRepository categoryRepository;
    @Override
    public Mono<List<TagVO>> findTagByProductId(Long productId) {
        return productTagRepository.findTagIdByProductId(productId)
                .flatMap(tagRepository::findById)
                .collectList()
                .map(tags -> tags.stream()
                        .map(tag -> BeanUtil.toBean(tag, TagVO.class))
                        .toList()
                );

    }
    //根据商品id查询商品信息
    @Override
    public Mono<List<Product>> findProductByProductId(List<Long> productIds) {
        return productRepository.findAllById(productIds).collectList();
    }

    //根据商品id查询商品信息
    @Override
    public Flux<Product> findProductPage(Long pageNum, int pageSize, String nameKeyword) {
        return productRepository.findPageByName(nameKeyword, pageSize, pageNum);
    }

    //根据总销售量查询商品id列表
    @Override
    public Mono<List<Long>> findProductIdsByTotalSalesGreaterThan(Integer salesVolume) {
        return skuRepository
                .findProductIdsByTotalSalesGreaterThan(salesVolume)
                .collectList()
                .map(ids->{
                    Collections.shuffle(ids);
                    return ids;
                });

    }

    //根据productId获取sku
    @Override
    public Flux<SKU> findAllByProductId(Long productId) {
        return skuRepository.findAllByProductId(productId);
    }
//根据categoryId获取category
    @Override
    public Flux<Category> findAllByCategoryId(List<Long> categoryIds) {
        return categoryRepository.findAllById(categoryIds);
    }

    @Override
    public Mono<Void> deleteAllByProductId(Long productId) {
        return  skuRepository.deleteAllByProductId(productId);
    }

    @Override
    public Mono<Category> findCategoryByCategoryId(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    @Override
    public Flux<Tag> findTagByTagIds(List<Long> tagIds) {
        return tagRepository.findAllById(tagIds);
    }
}
