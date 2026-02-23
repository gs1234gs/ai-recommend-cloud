package com.guanshiyun.service.utils;

import cn.hutool.core.bean.BeanUtil;
import com.guanshiyun.controller.tag.vo.TagVO;
import com.guanshiyun.product.Product;
import com.guanshiyun.repository.product.ProductRepository;
import com.guanshiyun.repository.relation.ProductTagRepository;
import com.guanshiyun.repository.sku.SKURepository;
import com.guanshiyun.repository.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilsService {
    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;
    private final ProductRepository productRepository;
    private final SKURepository skuRepository;
    public Mono<List<TagVO>> findTagByProductId(BigInteger productId) {
        return productTagRepository.findTagIdByProductId(productId)
                .flatMap(tagRepository::findById)
                .collectList()
                .map(tags -> tags.stream()
                        .map(tag -> BeanUtil.toBean(tag, TagVO.class))
                        .toList()
                );

    }
    //根据商品id查询商品信息
    public Mono<List<Product>> findProductByProductId(List<BigInteger> productIds) {
        return productRepository.findAllById(productIds).collectList();
    }

    //根据商品id查询商品信息
    public Flux<Product> findProductPage(BigInteger pageNum, int pageSize, String nameKeyword) {
        return productRepository.findPageByName(nameKeyword, pageSize, pageNum);
    }

    //根据总销售量查询商品id列表
    public Mono<List<BigInteger>> findProductIdsByTotalSalesGreaterThan(Integer salesVolume) {
        return skuRepository
                .findProductIdsByTotalSalesGreaterThan(salesVolume)
                .collectList()
                .map(ids->{
                    Collections.shuffle(ids);
                   return ids.subList(0, Math.min(ids.size(), 4));
                });

    }
}
