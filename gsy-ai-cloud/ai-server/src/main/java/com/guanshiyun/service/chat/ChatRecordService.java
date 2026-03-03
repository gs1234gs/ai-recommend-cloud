package com.guanshiyun.service.chat;

import com.guanshiyun.chat.ChatRecord;
import com.guanshiyun.controller.chat.vo.ChatRecordVO;
import com.guanshiyun.mymongodb.ChatRecordContent;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;
/**
 * ChatRecordService
 *
 * 接口功能：
 * - 提供聊天记录（ChatRecord）的管理功能
 * - 支持分页查询、保存记录和游标分页查询
 * - 响应式接口，基于 Reactor 的 Mono 和 Flux 实现异步非阻塞操作
 */
public interface ChatRecordService {
    /**
     * 分页查询聊天记录
     *
     * @param requestPage 分页请求参数，包含页码、每页数量和查询条件
     * @return Mono<PageResultT<List<ChatRecordVO>>> 分页查询结果，包含总记录数和当前页数据列表
     */
    Mono<PageResultT<List<ChatRecordVO>>> findPageChat(RequestPage<ChatRecordVO> requestPage);

    /**
     * 保存聊天记录
     *
     * @param chatRecord 聊天记录实体对象
     * @return Mono<BigInteger> 返回保存成功的记录 ID
     *                        如果保存失败或用户未登录返回 0
     */
    public Mono<BigInteger> save(ChatRecordContent chatRecord);

    /**
     * 游标分页查询聊天记录
     *
     * @param requestCursorPage 游标分页请求对象，包含起始游标、页大小和查询条件
     * @return Flux<ChatRecord> 返回符合条件的聊天记录流（Flux 可用于响应式流式处理）
     */
    Flux<ChatRecordContent> findCursorPageChat(RequestCursorPage<ChatRecord> requestCursorPage);
}
