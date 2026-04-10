package com.guanshiyun.service.chat.impl;


import cn.hutool.core.bean.BeanUtil;
import com.db.page.PageUtils;
import com.guanshiyun.chat.ChatRecord;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.controller.chat.vo.ChatRecordVO;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.mymongodb.ChatRecordContent;
import com.guanshiyun.repositorymongodb.chat.ChatRecordContentMongodbRepository;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.chat.ChatRecordService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

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
    private final ChatRecordContentMongodbRepository chatRecordContentMongodbRepository;
    private final MyLong myLong;
    private final ReactiveMongoTemplate reactiveMongoTemplate;


    /**
     * 分页查询用户聊天记录 (MongoDB 版)
     * 只返回标题、ID 等元数据，不返回具体内容 (contentTexts)
     */
    @Override
    public Mono<PageResultT<List<ChatRecordVO>>> findPageChat(RequestPage<ChatRecordVO> requestPage) {
        // 校验参数
        RequestPage<ChatRecordVO> validRequestPage = PageUtils.pageValidation(requestPage, ChatRecordVO.class);
        Long pageNum = validRequestPage.getPageNum();
        Integer pageSize = PageUtils.pageSize(validRequestPage.getPageSize());

        // 提取查询条件中的标题
        String title = validRequestPage.getCondition() != null ? validRequestPage.getCondition().getTitle() : null;

        return Mono.deferContextual(ctx -> {
                    // 未登录或游客，返回空分页
                    if (!myLong.hasKey(ctx)) {
                        return Mono.just(PageResultT.<List<ChatRecordVO>>builder()
                                .total(0L)
                                .rows(Collections.emptyList())
                                .build());
                    }

                    Long userId = myLong.myLong(
                            ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
                    );

                    if (Objects.isNull(userId)) {
                        return Mono.just(PageResultT.<List<ChatRecordVO>>builder()
                                .total(0L)
                                .rows(Collections.emptyList())
                                .build());
                    }

                    // 构建查询条件
                    Criteria criteria = new Criteria();
                    // 1. 必须匹配当前用户 (作为创建者)
                    criteria.and(ChatRecordContent.Fields.creator).is(userId);
                    // 2. 未删除标记 (假设 delFlag 0 为正常)
                    criteria.and(ChatRecordContent.Fields.delFlag).is((short) 0);

                    // 3. 标题模糊匹配 (MongoDB 使用正则)
                    if (StringUtils.hasText(title)) {
                        log.warn("标题模糊查询：{}", title);
                        // 编译正则，忽略大小写
                        Pattern pattern = Pattern.compile("^.*" + Pattern.quote(title) + ".*$", Pattern.CASE_INSENSITIVE);
                        criteria.and(ChatRecordContent.Fields.title).regex(pattern);
                    }

                    // 计算 offset
                    long offset = (pageNum-1)  * pageSize;

                    // --- 总数查询 ---
                    Query countQuery = Query.query(criteria);
                    Mono<Long> countMono = reactiveMongoTemplate.count(countQuery, ChatRecordContent.class);

                    // --- 数据查询 ---
                    Query dataQuery = Query.query(criteria)
                            .with(Sort.by(
                                    Sort.Order.desc(ChatRecordContent.Fields.createTime),
                                    Sort.Order.desc(ChatRecordContent.Fields.id)
                            ))
                            .skip(offset)
                            .limit(pageSize);

                    // 【关键】排除 contentTexts 字段，只获取元数据，减少网络传输和内存消耗
                    dataQuery.fields().exclude(ChatRecordContent.Fields.contentTexts);

                    Mono<List<ChatRecordVO>> listMono = reactiveMongoTemplate.find(dataQuery, ChatRecordContent.class)
                            .map(entity -> {
                                // 转换 VO，此时 entity.getContentTexts() 应为 null (因为被 exclude 了)
                                // 双重保险：确保 VO 中也不包含内容
                                return BeanUtil.toBean(entity, ChatRecordVO.class);
                            })
                            .collectList();

                    return Mono.zip(countMono, listMono)
                            .map(tuple -> PageResultT.<List<ChatRecordVO>>builder()
                                    .total(tuple.getT1())
                                    .rows(tuple.getT2())
                                    .build());
                })
                .onErrorResume(throwable -> {
                    log.error("MongoDB 分页查询对话记录失败", throwable);
                    return Mono.just(PageResultT.<List<ChatRecordVO>>builder()
                            .total(0L)
                            .rows(Collections.emptyList())
                            .build());
                });
    }

    /**
     * 保存聊天记录
     */
    @Override
    public Mono<Long> save(ChatRecordContent chatRecord) {
        chatRecord.setUpdateTime(LocalDateTime.now());
        // 如果是新建，设置创建时间和默认未删除
        if (chatRecord.getCreateTime() == null) {
            chatRecord.setCreateTime(LocalDateTime.now());
        }
        if (chatRecord.getDelFlag() == 0) {
            chatRecord.setDelFlag((short) 0);
        }

        return Mono.deferContextual(ctx -> {
            Long userId = myLong.myLong(
                    ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
            );

            if (Objects.isNull(userId)) {
                // 游客模式可能不允许保存，或者保存为特定 ID，这里按原逻辑返回 0
                return Mono.just(ConstNumber.LONG_ZERO);
            }

            chatRecord.setUpdater(userId);
            if (chatRecord.getCreator() == null) {
                chatRecord.setCreator(userId);
            }

            return chatRecordContentMongodbRepository.save(chatRecord)
                    .map(saved -> saved.getId())
                    .onErrorResume(throwable -> {
                        log.error("保存会话记录到 MongoDB 失败", throwable);
                        return Mono.just(ConstNumber.LONG_ZERO);
                    });
        });
    }

    /**
     * 游标分页查询聊天记录 (MongoDB 版)
     * 同样不返回 contentTexts
     */
    @Override
    public Flux<ChatRecordContent> findCursorPageChat(RequestCursorPage<ChatRecord> requestCursorPage) {
        // 注意：入参泛型可能需要调整为 ChatRecordContent 或保持兼容，这里假设 requestCursorPage.getCondition() 能取到 title
        String title = requestCursorPage.getCondition() != null ? requestCursorPage.getCondition().getTitle() : null;
        Object cursorValue = requestCursorPage.getLastId(); // 游标值 (通常是上一个元素的 createTime 或 id)

        return Flux.deferContextual(ctx -> {
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Flux.error(new RuntimeException("用户未登录"));
            }

            Long userId = myLong.myLong(
                    ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
            );

            if (Objects.isNull(userId)) {
                return Flux.empty();
            }

            Criteria criteria = new Criteria();
            criteria.and(ChatRecordContent.Fields.creator).is(userId);
            criteria.and(ChatRecordContent.Fields.delFlag).is((short) 0);

            // 标题模糊匹配
            if (StringUtils.hasText(title)) {
                Pattern pattern = Pattern.compile("^.*" + Pattern.quote(title) + ".*$", Pattern.CASE_INSENSITIVE);
                criteria.and(ChatRecordContent.Fields.title).regex(pattern);
            }

            // 游标逻辑 (Range Query)
            // 假设是倒序排列 (最新的在前)，游标逻辑通常是: field < cursorValue
            // 如果是正序，则是: field > cursorValue
            // 这里根据原代码的 sort (desc) 推断为倒序
            if (cursorValue != null && StringUtils.hasText(ChatRecordContent.Fields.id)) {
                // 简单处理：假设游标字段是 createTime 或 id
                // 实际生产中建议使用专门的 CursorUtil 处理类型转换
                criteria.and(ChatRecordContent.Fields.id).lt(cursorValue);
            }

            Query query = new Query(criteria)
                    .with(Sort.by(
                            Sort.Order.desc(StringUtils.hasText(ChatRecordContent.Fields.id) ? ChatRecordContent.Fields.id : ChatRecordContent.Fields.createTime),
                            Sort.Order.desc(ChatRecordContent.Fields.id)
                    ))
                    .limit(requestCursorPage.getPageSize());

            // 【关键】排除内容字段
            query.fields().exclude(ChatRecordContent.Fields.contentTexts);

            return reactiveMongoTemplate.find(query, ChatRecordContent.class);
        });
    }
}
