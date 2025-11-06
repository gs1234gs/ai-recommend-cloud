package com.guanshiyun.service.tag;

import com.guanshiyun.controller.tag.vo.TagSaveVO;
import com.guanshiyun.controller.tag.vo.TagVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface TagService {
    //添加标签
    Mono<BigInteger> save(TagSaveVO tagSaveVO);
    //删除标签
    Mono<Void> deleteById(BigInteger id);
    //查询标签
    Mono<TagVO> findById(BigInteger id);
    //查询标签列表
    Mono<PageResultT<List<TagVO>>> findAllByPage(RequestPage<TagVO> requestPage);

    Mono<Void> deleteAllById(List<BigInteger> ids);
}
