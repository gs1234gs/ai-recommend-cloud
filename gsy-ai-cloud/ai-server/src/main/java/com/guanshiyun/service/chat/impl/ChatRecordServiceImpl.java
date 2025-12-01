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

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRecordServiceImpl implements ChatRecordService {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final ChatRecordRepository chatRecordRepository;
    private final MyBigInteger myBigInteger;

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
            if(!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)){
                return Mono.just(PageResultT.<List<ChatRecordVO>>builder()
                        .total(0L)
                        .rows(Collections.emptyList())
                        .build());
            }
                    BigInteger userId = myBigInteger.bigInteger(
                            ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
                    );

                    // 用户未登录，返回空结果
                    if (userId == null) {
                        return Mono.just(PageResultT.<List<ChatRecordVO>>builder()
                                .total(0L)
                                .rows(Collections.emptyList())
                                .build());
                    }
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
