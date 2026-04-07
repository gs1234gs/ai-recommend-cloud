package com.guanshiyun.service.browse.impl;

import com.db.cursorQuery.MongoCursorQuery;
import com.db.dbnumber.ConstNumber;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.behaviorenums.GuestEnum;
import com.guanshiyun.browse.UserBrowseMongodb;
import com.guanshiyun.controller.browse.vo.UserBrowseSaveVO;
import com.guanshiyun.controller.browse.vo.UserBrowseVO;
import com.guanshiyun.feedback.Feedback;
import com.guanshiyun.gorseenum.GorseFeedbackEnum;
import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.profile.CategoryApiVO;
import com.guanshiyun.profile.SKUApiVO;
import com.guanshiyun.profile.TagApiVO;
import com.guanshiyun.repository.browse.UserBrowseMongodbRepository;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rowAffected.RowAffected;
import com.guanshiyun.rpc.goodsapi.category.CategoryApiService;
import com.guanshiyun.rpc.goodsapi.sku.SkuApiService;
import com.guanshiyun.rpc.goodsapi.tag.TagApiService;
import com.guanshiyun.service.browse.UserBrowseService;
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
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 用户浏览行为服务实现类
 * <p>
 * 主要职责：
 * 1. 记录用户浏览商品的行为（包含分类 / SKU / 标签信息）
 * 2. 同步浏览行为到推荐系统（Gorse）
 * 3. 提供用户浏览记录的查询能力（普通查询 / 游标分页）
 * <p>
 * 技术特点：
 * - 使用 Reactor（Mono / Flux）进行异步编排
 * - 使用 MongoDB 作为浏览记录存储
 * - 使用 Context 传递用户身份信息
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBrowseServiceImpl implements UserBrowseService {
    private final UserBrowseMongodbRepository userBrowseMongodbRepository;
    private final SnowflakePermanent snowflakePermanent;
    private final MyLong myLong;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final GorseClient gorseClient;
    private final CategoryApiService categoryApiService;
    private final SkuApiService skuApiService;
    private final TagApiService tagApiService;

    /**
     * 保存用户浏览记录，并同步行为数据到推荐系统（Gorse）
     * <p>
     * 行为说明：
     * - 登录用户：记录 creator，并以真实 userId 上报浏览行为
     * - 游客用户：不记录 creator，以 guestId 上报浏览行为
     * <p>
     * 核心流程：
     * 1. 填充浏览记录基础字段（id / createTime / creator）
     * 2. 查询商品关联的分类 / SKU / 标签
     * 3. 保存浏览记录到 MongoDB
     * 4. 同步浏览行为到推荐系统
     *
     * @param userBrowseSaveVOList 前端上传的浏览记录
     * @return 保存成功的浏览记录 ID 列表
     */

    @Override
    public Mono<List<Long>> save(List<UserBrowseSaveVO> userBrowseSaveVOList) {
        List<UserBrowseMongodb> userBrowseMongodbList = BeanConvertUtil
                .toBeanList(userBrowseSaveVOList, UserBrowseMongodb.class);
        return Mono.deferContextual(ctx -> {
            LocalDateTime now = LocalDateTime.now();
            // ===== 判断是否为登录用户 =====
            // Context 中存在用户 ID，则视为登录用户
            if (ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                Long useId =
                        myLong.myLong(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                return Flux.fromIterable(userBrowseMongodbList)
                        .flatMap(userBrowseMongodb -> {
                            // ===== 设置浏览记录基础信息 =====
                            //开始时间=结束时间-浏览时间
                            LocalDateTime startTime = now.minusDays(userBrowseMongodb.getBrowseDuration());
                            userBrowseMongodb
                                    .setBrowseEndTime(now)
                                    .setBrowseStartTime(startTime)
                                    .setId(snowflakePermanent.nextId())
                                    .setCreateTime(now)
                                    .setCreator(useId);
                            // ===== 根据商品 ID 查询关联信息 =====
                            // 1. 商品所属分类
                            // 2. 商品 SKU 列表
                            // 3. 商品标签
                            Long productId = userBrowseMongodb.getProduct().getId();
                            Mono<ResultT<List<CategoryApiVO>>> categoryApiServiceByProductId =
                                    categoryApiService.findByProductId(productId);

                            Mono<ResultT<List<SKUApiVO>>> skuApiServiceByProductId =
                                    skuApiService.findByProductId(productId);

                            Mono<ResultT<List<TagApiVO>>> tagApiServiceByProductId =
                                    tagApiService.findByProductId(productId);

                            return Mono.zip(
                                    categoryApiServiceByProductId,
                                            skuApiServiceByProductId,
                                            tagApiServiceByProductId
                                    )
                                    .map(tuple -> {
                                        // ===== 回填商品维度信息到浏览记录 =====
                                                UserBrowseMongodb browseMongodb = userBrowseMongodb
                                                        .setSkuList(tuple.getT2().getData())
                                                        .setCategoryList(tuple.getT1().getData())
                                                        .setTagList(tuple.getT3().getData());
                                        // ===== 构造推荐系统反馈行为 =====
                                                Feedback feedback = Feedback.builder()
                                                        .feedbackType(GorseFeedbackEnum.BROWSE.getValue())
                                                        .userId(useId.toString())
                                                        .itemId(productId.toString())
                                                        .timestamp(
                                                                userBrowseMongodb
                                                                .getBrowseEndTime()
                                                                .format(DateTimeFormatter.ISO_DATE_TIME))
                                                        .build();
                                                return Tuples.of(browseMongodb, feedback);
                                            }

                                    );
                        })
                        .collectList()
                        .flatMap(tupleList -> {
                            // ===== 拆分浏览记录和反馈行为 =====
                            List<UserBrowseMongodb> userBrowseMongodbs =
                                    tupleList.stream().map(Tuple2::getT1).toList();

                            List<Feedback> feedbackList =
                                    tupleList.stream().map(Tuple2::getT2).toList();
                            // ===== 并行执行 =====
                            // 1. 保存浏览记录到 MongoDB
                            // 2. 上报浏览行为到推荐系统
                            Mono<List<UserBrowseMongodb>> userBrowseMono =
                                    userBrowseMongodbRepository
                                            .saveAll(userBrowseMongodbs)
                                            .collectList();
                            Mono<RowAffected> rowAffectedMono =
                                    gorseClient
                                    .insertFeedback(feedbackList);
                            return Mono.zip(userBrowseMono, rowAffectedMono);
                        })
                        .map(tuple -> tuple.getT1().stream().map(UserBrowseMongodb::getId).toList()
                        );
            }
                        // ===== 游客用户逻辑 =====
                        // 特点：
                        // 1. 不设置 creator
                        // 2. 使用 guestId 上报行为

            return Flux.fromIterable(userBrowseMongodbList)
                    .flatMap(userBrowseMongodb -> {
                        userBrowseMongodb
                                .setId(snowflakePermanent.nextId())
                                .setCreateTime(now);
                        Long productId = userBrowseMongodb.getProduct().getId();
                        Mono<ResultT<List<CategoryApiVO>>> categoryApiServiceByProductId =
                                categoryApiService.findByProductId(productId);
                        Mono<ResultT<List<SKUApiVO>>> skuApiServiceByProductId =
                                skuApiService.findByProductId(productId);
                        Mono<ResultT<List<TagApiVO>>> tagApiServiceByProductId =
                                tagApiService.findByProductId(productId);
                        return Mono.zip(categoryApiServiceByProductId, skuApiServiceByProductId, tagApiServiceByProductId)
                                .map(tuple -> {
                                            UserBrowseMongodb browseMongodb = userBrowseMongodb.setSkuList(tuple.getT2().getData())
                                                    .setCategoryList(tuple.getT1().getData())
                                                    .setTagList(tuple.getT3().getData());
                                            Feedback feedback = Feedback.builder()
                                                    .feedbackType(GorseFeedbackEnum.CLICK.getValue())
                                                    .userId(GuestEnum.GUEST_USER_ID.getValue())
                                                    .itemId(productId.toString())
                                                    .timestamp(userBrowseMongodb.getBrowseEndTime().format(DateTimeFormatter.ISO_DATE_TIME))
                                                    .build();
                                            return Tuples.of(browseMongodb, feedback);
                                        }

                                );
                    })
                    .collectList()
                    .flatMap(tupleList -> {
                        List<UserBrowseMongodb> userBrowseMongodbs = tupleList.stream().map(Tuple2::getT1)
                                .toList();
                        List<Feedback> feedbackList = tupleList.stream().map(Tuple2::getT2).toList();
                        Mono<List<UserBrowseMongodb>> userBrowseMono = userBrowseMongodbRepository.saveAll(userBrowseMongodbs).collectList();
                        Mono<RowAffected> rowAffectedMono = gorseClient.insertFeedback(feedbackList);
                        return Mono.zip(userBrowseMono, rowAffectedMono);
                    })
                    .map(tuple -> tuple.getT1().stream().map(UserBrowseMongodb::getId).toList()
                    );
        });
    }


    /**
     * 查询当前登录用户的最近浏览记录
     *
     * 特点：
     * - 必须登录
     * - 按创建时间倒序
     * - 默认最多返回 10 条
     */

    @Override
    public Flux<UserBrowseVO> findAll(Integer rows) {
        return Flux.deferContextual(ctx -> {

            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Flux.empty();
            }

            Long userId = myLong.longOrNull(
                    ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));

            // ===== 构造查询条件 =====
                // 1. creator = 当前用户
                // 2. createTime 倒序
                // 3. 限制返回条数

            Query query = new Query()
                    .addCriteria(Criteria.where(BasePojo.Fields.creator).is(userId))
                    .with(Sort.by(Sort.Order.desc(BasePojo.Fields.createTime))) // 按创建时间降序
                    .limit((Objects.nonNull(rows) && ConstNumber.INT_ZERO < rows) ? rows : ConstNumber.INT_TEN); // 默认最多10条

            return reactiveMongoTemplate.find(query, UserBrowseMongodb.class)
                    .mapNotNull(item -> BeanConvertUtil.toBean(item, UserBrowseVO.class));
        })
                .onErrorResume(e->{
                    log.error("查询click ： ",e);
                    return Mono.empty();
                });

    }


    /**
     * @param cursorPage
     * @return Mono<CursorPageResult < List < UserBrowseVO>>>
     * @description: 游标分页查询用户浏览记录
     * @author Guanshiyun
     * @date 2025/12/19 15:10
     */
    /**
     * 使用游标方式分页查询用户浏览记录
     *
     * 适用场景：
     * - 大数据量浏览记录
     * - 无限下拉 / 翻页加载
     *
     * 游标规则：
     * - 以浏览记录 ID 作为游标
     */

    @Override
    public Mono<CursorPageResult<List<UserBrowseVO>>> findAllByCursor(RequestCursorPage<UserBrowseVO> cursorPage) {
        RequestCursorPage<UserBrowseMongodb> requestCursorPage = BeanConvertUtil.toBean(cursorPage, UserBrowseMongodb.class);

        return MongoCursorQuery.of(reactiveMongoTemplate, UserBrowseMongodb.class, requestCursorPage)
                .list()
                .collectList()
                .map(browseList -> {
                    // ===== 获取本次查询结果中最大的 ID =====
                    // 用于作为下一页的游标
                    Long maxId = browseList.stream()
                            .map(UserBrowseMongodb::getId)
                            .filter(Objects::nonNull)
                            .max(Comparator.naturalOrder())
                            .orElse(ConstNumber.LONG_ZERO);
                    // 构造返回结果
                    return CursorPageResult.of(
                            maxId,
                            BeanConvertUtil.toBeanList(browseList, UserBrowseVO.class),
                            browseList.size() >= requestCursorPage.getPageSize()
                    );
                });
    }
}
