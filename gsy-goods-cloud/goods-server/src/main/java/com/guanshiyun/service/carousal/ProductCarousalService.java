package com.guanshiyun.service.carousal;

import com.guanshiyun.controller.carousal.vo.ProductCarousalSaveVO;
import com.guanshiyun.controller.carousal.vo.ProductCarousalVO;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface ProductCarousalService {
    //获取轮播图列表
    public Mono<List<ProductCarousalVO>> findAll();
    //根据id获取轮播图
    public Mono<ProductCarousalVO> findById(BigInteger id);
    //保存轮播图
    public Mono<ProductCarousalVO> save(ProductCarousalSaveVO productCarousalSaveVO);
    //删除轮播图
    public Mono<Void> deleteById(BigInteger id);
    //批量删除轮播图
    public Mono<Void> deleteByIds(List<BigInteger> ids);
    //更新轮播图
    public Mono<ProductCarousalSaveVO> update(ProductCarousalSaveVO productCarousalSaveVO);

    Mono<List<ProductCarousalVO>> findByType(Integer type);

}
