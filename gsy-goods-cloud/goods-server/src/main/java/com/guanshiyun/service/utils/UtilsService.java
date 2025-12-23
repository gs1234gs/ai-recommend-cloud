package com.guanshiyun.service.utils;

import cn.hutool.core.bean.BeanUtil;
import com.guanshiyun.controller.tag.vo.TagVO;
import com.guanshiyun.repository.relation.ProductTagRepository;
import com.guanshiyun.repository.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilsService {
    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;
    public Mono<List<TagVO>> findTagByProductId(BigInteger productId) {
        return productTagRepository.findTagByProductId(productId)

                .flatMap(tagRepository::findById)
                .collectList()
                .map(tags -> tags.stream()
                        .map(tag -> BeanUtil.toBean(tag, TagVO.class))
                        .toList()
                );

    }
}
