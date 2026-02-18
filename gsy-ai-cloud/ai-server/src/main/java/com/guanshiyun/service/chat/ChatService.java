package com.guanshiyun.service.chat;

import com.guanshiyun.req.AllReqChat;
import com.guanshiyun.req.ReqChat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigInteger;

/**
 * ChatService
 *
 * 接口功能：
 * - 提供聊天服务的核心方法
 * - 支持一次性返回完整聊天内容或流式返回分段内容
 * - 支持删除聊天会话记录
 */
public interface ChatService {
    /**
     * 一次性返回对话内容
     *
     * 方法功能：
     * - 接收用户请求（ReqChat），包含会话 ID 和用户输入内容
     * - 生成 AI 回复并一次性返回完整聊天内容
     * - 如果会话不存在，会自动创建新的会话记录
     *
     * @param reqChat 聊天请求对象
     * @return Flux<String> AI 回复文本流（一次性返回全部内容）
     */
    Flux<String> chatAll(ReqChat reqChat);
    /**
     * 分段返回对话内容（流式返回）
     *
     * 方法功能：
     * - 接收用户请求（ReqChat）
     * - 生成 AI 回复并按分段流式返回内容，适合长对话或实时展示
     * - 第一次对话会创建新的会话记录
     *
     * @param reqChat 聊天请求对象
     * @return Flux<String> AI 回复内容流（流式返回）
     */
    Mono<Tuple2<Flux<String>, BigInteger>>  chatFlux(ReqChat reqChat);
    Mono<Tuple2<Flux<String>, BigInteger>> chatFluxRecommend(ReqChat reqChat);
    Mono<AllReqChat> recommend(ReqChat reqChat);
    /**
     * 删除聊天会话
     *
     * 方法功能：
     * - 根据会话 ID 删除聊天记录
     * - 删除 MySQL 中会话记录，不涉及 MongoDB 聊天内容
     *
     * @param id 会话 ID
     * @return Mono<Long> 删除的记录行数
     */
    Mono<Long> deleteChatById(Object id);
}
