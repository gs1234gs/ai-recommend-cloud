package com.guanshiyun.service.browse;

import com.guanshiyun.controller.browse.vo.UserBrowseSaveVO;
import com.guanshiyun.controller.browse.vo.UserBrowseVO;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;
/**
 * UserBrowseService
 *
 * 作用：
 * 1. 提供用户浏览行为的保存、查询功能
 * 2. 适配异步响应式编程（Reactive）
 * 3. 支持分页和游标查询，便于大数据量浏览记录处理
 */
public interface UserBrowseService {
    /**
     * 保存用户浏览记录
     *
     * 核心功能：
     * - 将前端上传的浏览记录保存到数据库
     * - 并返回保存成功的记录 ID 列表
     * - 可支持批量保存
     *
     * @param userBrowseSaveVOList 前端传来的用户浏览记录列表
     * @return Mono<List<Long>> 保存成功后的记录 ID 列表（异步）
     */
    Mono<List<Long>> save(List<UserBrowseSaveVO> userBrowseSaveVOList);
    /**
     * 查询用户浏览记录
     *
     * 核心功能：
     * - 按时间倒序获取用户最近浏览的记录
     * - 支持指定返回条数
     * - 对游客用户可返回空列表
     *
     * @param rows 查询条数，如果为 null 或 <=0 默认返回 10 条
     * @return Flux<UserBrowseVO> 用户浏览记录列表（异步流）
     */
    Flux<UserBrowseVO> findAll(Integer rows);
    /**
     * 游标分页查询用户浏览记录
     *
     * 核心功能：
     * - 支持大数据量浏览记录的分页查询
     * - 返回当前页数据及是否还有更多数据的标识
     * - 使用 Cursor 分页避免 offset 查询性能问题
     *
     * @param cursorPage 包含分页参数（游标位置、分页大小等）
     * @return Mono<CursorPageResult<List<UserBrowseVO>>> 分页结果（异步）
     */
    Mono<CursorPageResult<List<UserBrowseVO>>> findAllByCursor(RequestCursorPage<UserBrowseVO> cursorPage);
}
