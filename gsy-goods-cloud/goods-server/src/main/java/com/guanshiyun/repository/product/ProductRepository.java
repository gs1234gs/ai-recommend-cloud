package com.guanshiyun.repository.product;

import com.guanshiyun.product.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.math.BigInteger;

public interface ProductRepository extends R2dbcRepository<Product, BigInteger> {
    //分页查询
    /**
     * 分页查询有效商品（且该商品至少有一个未删除的 SKU），支持按名称模糊搜索
     *
     * @param nameKeyword  商品名称关键词（可为 null 或空）
     * @param limit        每页数量
     * @param offset       偏移量
     * @return 商品列表
     */
    @Query("""
        SELECT p.*
        FROM product p
        WHERE p.del_flag = 0
          AND EXISTS (
              SELECT 1 FROM sku s 
              WHERE s.product_id = p.id AND s.del_flag = 0
          )
          AND (:nameKeyword IS NULL OR p.name LIKE '%' || :nameKeyword || '%')
        ORDER BY p.id
        LIMIT :limit OFFSET :offset
        """)
    Flux<Product> findPageByName(
            String nameKeyword, // 当值为 null 时，'p.name LIKE ...' 部分不会匹配任何行，效果等同于忽略此条件
            int limit,
            BigInteger offset
    );
}
