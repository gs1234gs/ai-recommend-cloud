package com.guanshiyun.service.chat.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.db.constsql.SqlConst;
import com.db.cursorQuery.CursorQuery;
import com.db.page.PageUtils;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.chat.ChatRecord;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.controller.chat.vo.ChatRecordVO;
import com.guanshiyun.repository.chat.ChatRecordRepository;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.chat.ChatRecordService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/**
 * ChatRecordServiceImpl
 *
 * 类功能：
 * - 管理用户聊天记录（ChatRecord）的增删改查
 * - 提供分页查询、游标分页查询、保存记录等功能
 * - 使用 R2DBC 异步非阻塞操作 + Reactor 响应式编程
 * - 支持上下文用户识别，游客或未登录用户限制访问
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRecordServiceImpl implements ChatRecordService {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final ChatRecordRepository chatRecordRepository;
    private final MyBigInteger myBigInteger;
    /**
     * 分页查询用户聊天记录
     *
     * @param requestPage 前端请求分页参数，包括页码、每页数量、查询条件
     * @return Mono<PageResultT<List<ChatRecordVO>>> 分页结果（总数 + 数据列表）
     *
     * 核心逻辑：
     * - 校验分页参数
     * - 获取当前用户 ID（上下文）
     * - 构造查询条件（支持 title 模糊匹配）
     * - 查询总数 + 分页数据
     * - 异常返回空结果
     */
    @Override
    public Mono<PageResultT<List<ChatRecordVO>>> findPageChat(RequestPage<ChatRecordVO> requestPage) {
        //校验参数
        RequestPage<ChatRecordVO> chatRecordRequestPage = PageUtils.pageValidation(requestPage, ChatRecordVO.class);
        //起始页码
        BigInteger pageNum = chatRecordRequestPage.getPageNum();
        //每页数量
        Integer pageSize = PageUtils.pageSize(chatRecordRequestPage.getPageSize());
        //查询条件
        ChatRecord condition = BeanUtil.toBean(chatRecordRequestPage.getCondition(), ChatRecord.class);
        //标题
        String title = condition.getTitle();

        return Mono.deferContextual(ctx -> {
                    // 未登录或游客，返回空分页
            if(!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)){
                return Mono.just(PageResultT.<List<ChatRecordVO>>builder()
                        .total(0L)
                        .rows(Collections.emptyList())
                        .build());
            }
                    // 获取用户 ID
                    BigInteger userId = myBigInteger.bigInteger(
                            ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
                    );
                    //构建查询条件
                    Criteria criteria = Criteria.empty();
                    if (!StrUtil.isBlank(title)) {
                        log.warn("标题模糊查询：{}", title);
                        criteria = criteria.and(ChatRecord.Fields.title).like(SqlConst.PERCENT + title + SqlConst.PERCENT);
                    }
                    // 将用户权限加入查询条件
//                    criteria = criteria.and(ChatRecord.Fields.creator).is(userId);

                    // 计算 offset
                    long offset = pageNum.subtract(BigInteger.ONE)
                            .multiply(BigInteger.valueOf(pageSize))
                            .longValue();

                    // 数据查询：推荐主键排序 + 二级排序
                    Query dataQuery = Query.query(criteria)
                            .sort(Sort.by(
                                    Sort.Order.desc(ChatRecord.Fields.createTime),
                                    Sort.Order.desc(ChatRecord.Fields.id) // 防止时间重复
                            ))
                            .offset(offset)
                            .limit(pageSize);

                    // 总数查询
                    Query countQuery = Query.query(criteria);

                    // 查询总数 + 数据
                    return r2dbcEntityTemplate.select(countQuery, ChatRecord.class)
                            .count()
                            .flatMap(count -> r2dbcEntityTemplate.select(dataQuery, ChatRecord.class)
                                    .map(chatRecord -> BeanUtil.toBean(chatRecord, ChatRecordVO.class))
                                    .collectList()
                                    .map(list -> PageResultT.<List<ChatRecordVO>>builder()
                                            .total(count)
                                            .rows(list
                                            )
                                            .build()
                                    )
                            );
                })
                .onErrorResume(throwable -> {
                    log.error("分页查询对话记录失败", throwable);
                    return Mono.just(PageResultT.<List<ChatRecordVO>>builder()
                            .total(0L)
                            .rows(Collections.emptyList())
                            .build());
                });
    }
    /**
     * 保存聊天记录
     *
     * @param chatRecord 聊天记录实体
     * @return Mono<BigInteger> 返回保存成功的记录 ID
     *
     * 核心逻辑：
     * - 设置更新时间
     * - 获取当前用户 ID，游客返回 0
     * - 保存记录到数据库
     * - 异常返回 0
     */
    @Override
    public Mono<BigInteger> save(ChatRecord chatRecord) {
        chatRecord.setUpdateTime(LocalDateTime.now());
        return Mono.deferContextual(ctx -> {
            BigInteger userId = myBigInteger.bigInteger(
                    ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
            );
            if (Objects.isNull(userId))
                return Mono.just(ConstNumber.BIG_INTEGER_ZERO);
            chatRecord.setUpdater(userId);
            return chatRecordRepository.save(chatRecord)
                    .thenReturn(chatRecord.getId())
                    .onErrorResume(throwable -> {
                        log.error("保存会话记录失败", throwable);
                        return Mono.just(ConstNumber.BIG_INTEGER_ZERO);
                    });
        });
    }

    /**
     * 游标分页查询聊天记录（支持大数据量）
     *
     * @param requestCursorPage 游标分页请求
     * @return Flux<ChatRecord> 返回符合条件的聊天记录流
     *
     * 核心逻辑：
     * - 获取用户 ID
     * - 构建游标分页查询
     * - 支持 title 模糊匹配 + 用户过滤
     */
    @Override
    public Flux<ChatRecord> findCursorPageChat(RequestCursorPage<ChatRecord> requestCursorPage) {
        return Flux.deferContextual(ctx -> {
            if(!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY))
                return Flux.error(new RuntimeException("用户未登录"));
            BigInteger userId = myBigInteger.bigInteger(
                    ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
            );
            if (Objects.isNull(userId))
                return Flux.empty();
            return CursorQuery.of(r2dbcEntityTemplate, ChatRecord.class, requestCursorPage)
                    // 添加查询条件：title 模糊匹配
                    .like(ChatRecord.Fields.title, requestCursorPage.getCondition().getTitle())
                    .eq(ChatRecord.Fields.creator, userId)
                    .list();
        });
    }
}
