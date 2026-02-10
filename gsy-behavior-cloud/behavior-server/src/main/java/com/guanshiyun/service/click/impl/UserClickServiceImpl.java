package com.guanshiyun.service.click.impl;

import com.db.dbnumber.ConstNumber;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.behaviorenums.GuestEnum;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.click.UserClickMongodb;
import com.guanshiyun.controller.click.vo.UserClickSaveVO;
import com.guanshiyun.controller.click.vo.UserClickVO;
import com.guanshiyun.feedback.Feedback;
import com.guanshiyun.gorseenum.GorseFeedbackEnum;
import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.repository.click.UserClickMongodbRepository;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rowAffected.RowAffected;
import com.guanshiyun.rpc.goodsapi.category.CategoryApiService;
import com.guanshiyun.rpc.goodsapi.sku.SkuApiService;
import com.guanshiyun.rpc.goodsapi.tag.TagApiService;
import com.guanshiyun.rpc.profile.CategoryApiVO;
import com.guanshiyun.rpc.profile.SKUApiVO;
import com.guanshiyun.rpc.profile.TagApiVO;
import com.guanshiyun.service.click.UserClickService;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
/**
 * UserClickServiceImpl
 *
 * 作用：
 * 1. 保存用户点击记录到 MongoDB
 * 2. 同步用户点击行为到推荐系统（Gorse）
 * 3. 查询用户点击历史记录
 *
 * 技术特点：
 * - 使用 Reactive 编程 (Mono / Flux) 异步处理
 * - 使用 Context 获取用户身份信息
 * - 分别处理登录用户与游客用户
 * - 聚合 RPC 获取商品维度信息（分类 / SKU / 标签）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserClickServiceImpl implements UserClickService {
    private final UserClickMongodbRepository userClickMongodbRepository;
    private final CategoryApiService categoryApiService;
    private final SkuApiService skuApiService;
    private final TagApiService tagApiService;
    private final SnowflakePermanent snowflakePermanent;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final GorseClient gorseClient;
    private final MyBigInteger  myBigInteger;


    /**
     * 保存用户点击记录
     *
     * 核心流程：
     * 1. VO -> MongoDB 实体
     * 2. 填充基础字段：id / clickTime / createTime / creator
     * 3. 异步获取商品维度信息：分类 / SKU / 标签
     * 4. 保存到 MongoDB，并上报点击行为到推荐系统
     * 5. 区分登录用户与游客用户
     *
     * @param userClickVO 前端点击记录
     * @return Mono<BigInteger> 返回保存成功的记录 ID
     */
    @Override
    public Mono<BigInteger> save(UserClickSaveVO userClickVO) {
        // 将前端 VO 转换成 MongoDB 实体
        UserClickMongodb userClickMongodb = BeanConvertUtil.toBean(userClickVO, UserClickMongodb.class);
        LocalDateTime now = LocalDateTime.now();
        // 全局唯一 ID
        BigInteger nextId = snowflakePermanent.nextId();

        return Mono.deferContextual(ctx -> {
            // ====================== 登录用户逻辑 ======================
            if (ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                BigInteger userId =
                        ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
                // 填充基础字段
                userClickMongodb.setId(nextId)
                        .setClickTime(now)
                        .setCreateTime(now)
                        .setCreator(userId);

                BigInteger productId = userClickMongodb.getProduct().getId();

                // 异步 RPC 获取商品维度信息
                Mono<ResultT<List<CategoryApiVO>>> categoryApiServiceByProductId =
                        categoryApiService.findByProductId(productId);
                Mono<ResultT<List<SKUApiVO>>> skuApiServiceByProductId =
                        skuApiService.findByProductId(productId);
                Mono<ResultT<List<TagApiVO>>> tagApiServiceByProductId =
                        tagApiService.findByProductId(productId);

                // 聚合 RPC 结果
                return Mono.zip(categoryApiServiceByProductId, skuApiServiceByProductId, tagApiServiceByProductId)
                        .map(tuple -> {
                                     // 回填商品维度信息
                                    UserClickMongodb clickMongodb = userClickMongodb.setSkuList(tuple.getT2().getData())
                                            .setCategoryList(tuple.getT1().getData())
                                            .setTagList(tuple.getT3().getData());
                            // 构造推荐系统点击反馈
                                    Feedback feedback = Feedback.builder()
                                            .feedbackType(GorseFeedbackEnum.CLICK.getValue())
                                            .userId(userId.toString())
                                            .itemId(productId.toString())
                                            .timestamp(userClickMongodb.getClickTime()
                                                    .format(DateTimeFormatter.ISO_DATE_TIME))
                                            .build();
                                    return Tuples.of(clickMongodb, feedback);
                                }

                        )
                        // 保存到 MongoDB 并上报 Gorse
                        .flatMap(tuple -> {
                            UserClickMongodb clickMongodb = tuple.getT1();
                            Feedback feedback = tuple.getT2();
                            // 并行执行：
                            // 1. 保存到 MongoDB
                            // 2. 上报 Gorse
                            Mono<RowAffected> rowAffectedMono = gorseClient.insertFeedback(List.of(feedback));
                            Mono<UserClickMongodb> save = userClickMongodbRepository.save(clickMongodb);
                            return Mono.zip(rowAffectedMono, save)
                                    .map(t -> t.getT2().getId());
                        });
            }
            // ====================== 游客用户逻辑 ======================
            // 游客不设置 creator
            userClickMongodb
                    .setId(nextId)
                    .setClickTime(now)
                    .setCreateTime(now);

            BigInteger productId = userClickMongodb.getProduct().getId();

            // 异步 RPC 获取商品维度信息
            Mono<ResultT<List<CategoryApiVO>>> categoryApiServiceByProductId =
                    categoryApiService.findByProductId(productId);
            Mono<ResultT<List<SKUApiVO>>> skuApiServiceByProductId =
                    skuApiService.findByProductId(productId);
            Mono<ResultT<List<TagApiVO>>> tagApiServiceByProductId =
                    tagApiService.findByProductId(productId);
            // 聚合 RPC 结果
            return Mono.zip(categoryApiServiceByProductId, skuApiServiceByProductId, tagApiServiceByProductId)
                    .map(tuple -> {
                                UserClickMongodb clickMongodb = userClickMongodb.setSkuList(tuple.getT2().getData())
                                        .setCategoryList(tuple.getT1().getData())
                                        .setTagList(tuple.getT3().getData());

                        // 游客上报 Gorse 行为，使用 guestId
                                Feedback feedback = Feedback.builder()
                                        .feedbackType(GorseFeedbackEnum.CLICK.getValue())
                                        .userId(GuestEnum.GUEST_USER_ID.getValue())
                                        .itemId(productId.toString())
                                        .timestamp(userClickMongodb.getClickTime().format(DateTimeFormatter.ISO_DATE_TIME))
                                        .build();
                                return Tuples.of(clickMongodb, feedback);
                            }

                    )

                    // 保存 MongoDB 并上报 Gorse
                    .flatMap(tuple -> {
                        UserClickMongodb clickMongodb = tuple.getT1();
                        Feedback feedback = tuple.getT2();
                        Mono<RowAffected> rowAffectedMono = gorseClient.insertFeedback(List.of(feedback));
                        Mono<UserClickMongodb> save = userClickMongodbRepository.save(clickMongodb);
                        return Mono.zip(rowAffectedMono, save)
                                .map(t -> t.getT2().getId());
                    });
        });
    }

    /**
     * 查询当前用户点击记录
     *
     * 特点：
     * - 必须登录
     * - 按点击时间倒序排序
     * - 默认返回 10 条
     *
     * @param rows 查询条数
     * @return Flux<UserClickVO> 点击记录列表
     */
    @Override
    public Flux<UserClickVO> findAll(Integer rows) {
        return Flux.deferContextual(ctx -> {

            // 游客不返回点击记录
            if(!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)){
                return Flux.empty();
            }
            int limit =(Objects.isNull( rows) || rows <= ConstNumber.INT_ZERO)  ? ConstNumber.INTEGER_TEN : rows;

            // 获取用户 ID
            BigInteger userId =
                    myBigInteger.bigIntegerOrNull(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));

            // 构造 MongoDB 查询
            Query query = new Query()
                    .with(Sort.by(Sort.Order.desc(UserClickMongodb.Fields.clickTime))) // 按点击时间降序
                    .limit(limit)// 限制返回条数
                    .addCriteria(Criteria.where(BasePojo.Fields.creator).is(userId)); // 仅查询该用户记录
            // 仅查询该用户记录
            return reactiveMongoTemplate.find(query, UserClickMongodb.class)
                    .map(item->
                            BeanConvertUtil.toBean( item, UserClickVO.class))
                    .onErrorResume(e -> Flux.error(new RuntimeException("查询失败", e)));
        })
                .onErrorResume(e->{
                    log.error("查询click ： ",e);
                    return Mono.empty();
                });
    }
}
