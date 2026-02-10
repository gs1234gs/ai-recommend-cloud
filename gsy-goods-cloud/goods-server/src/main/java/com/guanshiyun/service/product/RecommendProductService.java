package com.guanshiyun.service.product;

import com.guanshiyun.controller.product.vo.ProductCustomerDetailVO;
import com.guanshiyun.controller.product.vo.ProductCustomerVO;
import com.guanshiyun.controller.product.vo.ProductSearchSaveVO;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;
/**
 * 商品推荐服务接口。
 * 提供商品搜索、个性化推荐、猜你喜欢以及商品详情等功能。
 */
public interface RecommendProductService {

    /**
     * 根据分页和搜索条件，以游标分页方式查询商品列表。
     *
     * @param requestCursorPage 包含搜索条件（如关键词、分类等）和游标分页参数（如游标值、每页大小）的请求对象
     * @return 返回包含商品列表及分页信息的 {@link CursorPageResult}，其中数据项为 {@link ProductCustomerVO}
     */
    Mono<CursorPageResult<List<ProductCustomerVO>>> findCursor(RequestCursorPage<ProductSearchSaveVO> requestCursorPage);


    /**
     * 获取系统推荐的商品列表。
     * 通常基于热销、新品、综合评分等策略生成推荐结果。
     *
     * @return 推荐商品列表，每个元素为面向客户的商品视图对象 {@link ProductCustomerVO}
     */
    Mono<List<ProductCustomerVO>> recommend();



    /**
     * 获取“猜你喜欢”个性化推荐商品列表。
     * 通常基于用户行为、偏好或协同过滤算法生成个性化推荐。
     *
     * @return 个性化推荐商品列表，每个元素为 {@link ProductCustomerVO}
     */
    Mono<List<ProductCustomerVO>> like();



    /**
     * 根据商品 ID 查询商品的详细信息。
     *
     * @param id 商品唯一标识（使用 {@link BigInteger} 类型）
     * @return 商品详情视图对象 {@link ProductCustomerDetailVO}
     */
    Mono<ProductCustomerDetailVO> detail(BigInteger id);

    Mono<List<ProductCustomerVO>> findByIds(List<BigInteger> ids);
}
