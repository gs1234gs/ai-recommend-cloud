package com.guanshiyun.repository.carousal;

import com.guanshiyun.carousal.ProductCarousal;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;




public interface ProductCarousalRepository extends R2dbcRepository<ProductCarousal, Long> {

    /**
     * 根据类型查询轮播图
     * @param type 类型
     * @return 轮播图列表
     */
    @Query("SELECT * FROM product_carousal WHERE type = :type")
    Flux<ProductCarousal> findByType(Integer type);
}
