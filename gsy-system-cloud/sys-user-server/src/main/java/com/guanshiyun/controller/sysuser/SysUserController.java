package com.guanshiyun.controller.sysuser;

import com.db.dbnumber.ConstNumber;
import com.guanshiyun.controller.sysuser.vo.SysUserSaveVO;
import com.guanshiyun.controller.sysuser.vo.SysUserVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.sysuser.SysUserService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.userpojo.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Slf4j
@RequestMapping("/sysUser/")
@RestController
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    //删除用户
    @DeleteMapping("deleteById/{id}")
    public Mono<ResultT<Long>> deleteUserById(@PathVariable Long id) {
        return sysUserService.deleteUserById(id)
                .map(deleteCount ->
                    ResultT.<Long>builder()
                            .code(deleteCount.equals(ConstNumber.LONG_ZERO) ? HttpStatus.INTERNAL_SERVER_ERROR.value() : HttpStatus.OK.value())
                            .msg(deleteCount.equals(ConstNumber.LONG_ZERO) ? "删除用户失败" : "删除用户成功")
                            .data(deleteCount)
                            .build()
                )
                .switchIfEmpty(
                        Mono.just(ResultT.<Long>builder()
                                .code(HttpStatus.NOT_FOUND.value())
                                .msg("删除用户失败")
                                .data(null)
                                .build()
                        )
                )
                .onErrorResume(throwable -> {
                    log.error("删除用户失败", throwable);
                    return Mono.just(ResultT.<Long>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .msg("删除用户失败")
                            .data(null)
                            .build());
                });
    }

    //批量删除用户
    @DeleteMapping("deleteUserByIds")
    public Mono<ResultT<Long>> deleteUserByIds(@RequestBody Collection<Long> ids) {
        return sysUserService.deleteUserByIds(ids)
                .map(deleteCount -> {
                            return ResultT.<Long>builder()
                                    .code(HttpStatus.OK.value())
                                    .msg("批量删除用户成功")
                                    .data(deleteCount)
                                    .build();
                        }

                )
                .switchIfEmpty(
                        Mono.just(ResultT.<Long>builder()
                                .code(HttpStatus.NOT_FOUND.value())
                                .msg("批量删除用户失败")
                                .data(null)
                                .build()
                        )
                )
                .onErrorResume(throwable -> {
                    log.error("批量删除用户失败", throwable);
                    return Mono.just(ResultT.<Long>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .msg("批量删除用户失败")
                            .data(null)
                            .build()
                    );
                });
    }

    //获取单个用户信息
    @GetMapping("findById")
    public Mono<ResultT<SysUserVO>> findById() {
        return Mono.deferContextual(contextView -> {
            if (!contextView.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY))
                return Mono.just(
                        ResultT.<SysUserVO>builder()
                                .code(HttpStatus.NOT_FOUND.value())
                                .msg("用户不存在")
                                .data(null)
                                .build()
                );
            Long id = contextView.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
            return sysUserService.findById(id)
                    .map(sysUser ->
                            ResultT.<SysUserVO>builder()
                                    .code(HttpStatus.OK.value())
                                    .msg("获取用户成功")
                                    .data(sysUser)
                                    .build()
                    )
                    .switchIfEmpty(
                            Mono.just(ResultT.<SysUserVO>builder()
                                    .code(HttpStatus.NOT_FOUND.value())
                                    .msg("用户不存在")
                                    .data(null)
                                    .build()
                            )
                    )
                    .onErrorResume(throwable -> {
                        log.error("获取用户失败", throwable);
                        return Mono.just(ResultT.<SysUserVO>builder()
                                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .msg("获取用户失败")
                                .data(null)
                                .build()
                        );
                    });
        });
    }

    //获取单个用户信息
    @GetMapping("findById/{id}")
    public Mono<ResultT<SysUserVO>> find(@PathVariable Long id) {
        return sysUserService.findById(id)
                .map(sysUser ->
                        ResultT.<SysUserVO>builder()
                                .code(HttpStatus.OK.value())
                                .msg("获取用户成功")
                                .data(sysUser)
                                .build()
                )
                .switchIfEmpty(
                        Mono.just(ResultT.<SysUserVO>builder()
                                .code(HttpStatus.NOT_FOUND.value())
                                .msg("用户不存在")
                                .data(null)
                                .build()
                        )
                )
                .onErrorResume(throwable -> {
                    log.error("获取用户失败", throwable);
                    return Mono.just(ResultT.<SysUserVO>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .msg("获取用户失败")
                            .data(null)
                            .build()
                    );
                });
    }

    //修改用户
    @PutMapping("updateUserById")
    public Mono<ResultT<Long>> updateUserById(@RequestBody SysUserVO sysUser) {
        return sysUserService.updateUserById(sysUser)
                .map(id ->
                        ResultT.<Long>builder()
                                .code(HttpStatus.OK.value())
                                .msg("修改用户成功")
                                .data(id)
                                .build()
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<Long>builder()
                                        .code(HttpStatus.NOT_FOUND.value())
                                        .msg("用户不存在")
                                        .data(null)
                                        .build()
                        )
                ).onErrorResume(throwable -> {
                    log.error("修改用户失败", throwable);
                    return Mono.just(ResultT.<Long>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .msg("修改用户失败")
                            .data(null)
                            .build()
                    );
                });
    }

    //添加用户
    @PostMapping("save")
    public Mono<ResultT<Long>> addUser(@RequestBody SysUserSaveVO sysUserSaveVO) {
        return sysUserService.save(sysUserSaveVO)
                .flatMap(id ->
                        Mono.just(
                                ResultT.<Long>builder()
                                        .code(HttpStatus.OK.value())
                                        .msg("成功")
                                        .data(id)
                                        .build()
                        )
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<Long>builder()
                                        .code(HttpStatus.NOT_FOUND.value())
                                        .msg("失败")
                                        .data(null)
                                        .build()
                        )
                )
                .onErrorResume(throwable -> {
                    log.error("添加用户失败", throwable);
                    return Mono.just(ResultT.<Long>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .msg("服务器错误")
                            .data(null)
                            .build());
                })
                ;
    }

    //获取用户列表
    @PostMapping("findPage")
    public Mono<ResultT<PageResultT<List<SysUser>>>> findPage(
            @RequestBody(required = false) RequestPage<SysUser> requestPage) {
        return sysUserService.findPage(requestPage)
                .map(pageResult ->
                        ResultT.<PageResultT<List<SysUser>>>builder()
                                .code(HttpStatus.OK.value())
                                .msg("获取用户列表成功")
                                .data(pageResult)
                                .build()
                )
                .onErrorResume(throwable -> {
                    log.error("获取用户列表失败", throwable);
                    return Mono.just(ResultT.<PageResultT<List<SysUser>>>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .msg("获取用户列表失败")
                            .data(null)
                            .build());
                });
    }

    @PutMapping("updateSignInUser")
    public Mono<ResultT<SysUserVO>> updateSignInUser(@RequestBody SysUserSaveVO sysUserSaveVO) {
        return sysUserService.updateSignInUser(sysUserSaveVO)
                .map(ResultT::success)
                .onErrorResume(Mono::error);

    }

    @GetMapping("findBySignInUserId")
    public Mono<ResultT<SysUserVO>> findBySignInUserId(){
        return sysUserService.findById()
                .map(ResultT::success)
                .onErrorResume(Mono::error);
    }
}
