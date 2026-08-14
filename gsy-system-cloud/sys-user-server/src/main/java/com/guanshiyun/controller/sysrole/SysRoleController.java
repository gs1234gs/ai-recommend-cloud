package com.guanshiyun.controller.sysrole;

import cn.hutool.core.bean.BeanUtil;
import com.guanshiyun.controller.sysrole.vo.SysRoleSaveVO;
import com.guanshiyun.controller.sysrole.vo.SysRoleVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.sysrole.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sysRole/")
@RequiredArgsConstructor
public class SysRoleController {
    private final SysRoleService sysRoleService;

    //添加角色或者修改角色
//    @Operation(summary = "添加角色或者修改角色")
    @PostMapping("save")
    public Mono<ResultT<Long>> save(@RequestBody SysRoleSaveVO sysRoleSaveVO) {
        return sysRoleService.save(sysRoleSaveVO)
                .map(ResultT::success)
                .switchIfEmpty(Mono.just(ResultT.error("添加角色失败")))
                .onErrorResume(throwable -> Mono.just(ResultT.error("添加角色失败" + throwable.getMessage())));
    }

    //删除角色
    @DeleteMapping("deleteById/{id}")
    public Mono<ResultT<Long>> deleteRole(@PathVariable Long id) {
        return sysRoleService.deleteRoleById(id)
                .map(ResultT::success)
                .switchIfEmpty(Mono.just(ResultT.error("删除角色失败")))
                .onErrorResume(throwable -> {
                    log.error("删除角色失败", throwable);
                    return Mono.just(ResultT.error("删除角色失败" + throwable.getMessage()));
                });
    }

    //修改角色
    @PutMapping("updateById")
    public Mono<ResultT<Long>> updateRole(@RequestBody SysRoleSaveVO sysRoleVO) {
        return sysRoleService.update(sysRoleVO)
                .map(ResultT::success)
                .switchIfEmpty(Mono.just(ResultT.error("修改角色失败")))
                .onErrorResume(
                        throwable -> {
                            log.error("修改角色失败", throwable);
                            return Mono.just(ResultT.error("修改角色失败" + throwable.getMessage()));
                        }
                );

    }

    //查询角色
    @PostMapping("findPage")
    public Mono<ResultT<PageResultT<List<SysRoleVO>>>> findPage(
            @RequestBody(required = false) RequestPage<SysRoleVO> requestPage) {
        return sysRoleService.findPage(requestPage)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.error("获取角色列表失败", throwable);
                    return Mono.just(ResultT.error("获取角色列表失败" + throwable.getMessage()));
                });
    }

    //根据用户id获取角色
    @GetMapping("findRoleListByUserId/{userId}")
    public Mono<ResultT<List<SysRoleVO>>> findAllByUserId(@PathVariable Long userId) {
        return sysRoleService.findAllByUserId(userId)
                .map(role -> BeanUtil.toBean(role, SysRoleVO.class))
                .collectList()
                .map(ResultT::success)
                .switchIfEmpty(Mono.just(ResultT.error("获取角色列表失败")))
                .onErrorResume(
                        throwable -> {
                            log.error("获取角色列表失败", throwable);
                            return Mono.just(ResultT.error("获取角色列表失败" + throwable.getMessage()));
                        }
                );
    }
    @GetMapping("findById/{id}")
    public Mono<ResultT<SysRoleVO>> findById(@PathVariable Long id) {
        return sysRoleService.findById(id)
                .map(ResultT::success)
                .switchIfEmpty(Mono.just(ResultT.error("获取角色失败")))
                .onErrorResume(throwable -> {
                            log.error("获取角色失败", throwable);
                            return Mono.just(ResultT.error("获取角色失败" + throwable.getMessage()));
                        }
                );
    }


}
