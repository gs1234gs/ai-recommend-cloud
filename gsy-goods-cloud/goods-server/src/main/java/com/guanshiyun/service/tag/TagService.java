package com.guanshiyun.service.tag;

import com.guanshiyun.controller.tag.vo.TagSaveVO;
import com.guanshiyun.controller.tag.vo.TagVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Mono;


import java.util.List;

public interface TagService {
    //添加标签
    Mono<Long> save(TagSaveVO tagSaveVO);
    //删除标签
    Mono<Void> deleteById(Long id);
    //查询标签
    Mono<TagVO> findById(Long id);
    //查询标签列表
    Mono<PageResultT<List<TagVO>>> findAllByPage(RequestPage<TagVO> requestPage);

    Mono<Void> deleteAllById(List<Long> ids);

    Mono<List<TagVO>> findTagByProductId(Long productId);

    Mono<List<TagVO>> findTagByProductId(List<Long> productIds);
}
