package com.guanshiyun.controller.sysmenu;

import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.consts.code.HttpCodeConst;
import com.guanshiyun.menupojo.SysMenu;
import com.guanshiyun.menupojo.reponse.SysMenuResponse;
import com.guanshiyun.menuutil.MenuTreeUtils;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.sysmenu.SysMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sysMenu")
@RequiredArgsConstructor
public class SysMenuController {
    private final SysMenuService sysMenuService;
    private final MenuTreeUtils menuTreeUtils;


    //根据用户id获取菜单,用户id从当前线程获取，避免传入，影响安全
    @GetMapping("/userId")
    public Mono<ResultT<List<SysMenuResponse>>> sysMenuByUserId() {
        return sysMenuService.findMenuByUserId()
                .collectList()
                .flatMap(menuList ->
                        menuTreeUtils.buildMenuTree(menuList)
                                .collectList()
                )
                .map(menuList -> // 明确泛型
                        {
                            log.info("获取菜单成功：{}", menuList.size());
                            return ResultT.<List<SysMenuResponse>>builder()
                                    .code(HttpCodeConst.OK)
                                    .msg("成功")
                                    .data(menuList)
                                    .build();
                        }
                );
    }

    //获取菜单列表
    @GetMapping({"/menuList/{id}"})
    public Mono<ResultT<List<SysMenu>>> sysMenuList(
            @PathVariable(name = "id", required = false) BigInteger id) {
        return sysMenuService.findAllByParentId(id)
                .collectList()
                .flatMap(menuList ->
                        Mono.just(
                                ResultT.<List<SysMenu>>builder()
                                        .code(HttpCodeConst.OK)
                                        .msg("成功")
                                        .data(menuList)
                                        .build()
                        )
                );
    }

    //删除菜单
    @GetMapping("/delete/{id}")
    public Mono<ResultT<Long>> deleteMenu(@PathVariable BigInteger id) {
        return sysMenuService.deleteById(id)
                .map(result ->
                        result >= ConstNumber.INT_ONE ?
                                ResultT.<Long>builder()
                                        .code(HttpCodeConst.OK)
                                        .msg("删除成功")
                                        .data(result)
                                        .build()
                                :
                                ResultT.<Long>builder()
                                        .code(HttpCodeConst.UNAUTHORIZED)
                                        .msg("删除失败，该菜单不能删除")
                                        .data(result)
                                        .build()
                )
                .onErrorResume(throwable -> {
                    log.warn("删除菜单失败", throwable);
                    return Mono.just(ResultT.<Long>builder()
                            .code(HttpCodeConst.UNAUTHORIZED)
                            .msg("删除失败")
                            .build()
                    );
                });
    }

    //添加菜单
    @PostMapping("/add")
    public Mono<ResultT<List<SysMenuResponse>>> addMenu(@RequestBody SysMenu sysMenu) {
        return sysMenuService.save(sysMenu)
                .collectList()
                .flatMap(menuList ->
                        menuTreeUtils.buildMenuTree(menuList)
                                .collectList()
                )
                .map(menuList ->
                        ResultT.<List<SysMenuResponse>>builder()
                                .code(HttpCodeConst.OK)
                                .msg("成功")
                                .data(menuList)
                                .build()
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<List<SysMenuResponse>>builder()
                                        .code(HttpCodeConst.UNAUTHORIZED)
                                        .msg("添加失败")
                                        .data(null)
                                        .build()
                        )
                )
                .onErrorResume(throwable ->
                        Mono.just(
                                ResultT.<List<SysMenuResponse>>builder()
                                        .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                        .msg("系统错误")
                                        .data(null)
                                        .build()
                        )
                );
    }

    //修改菜单
    @PutMapping("/update")
    public Mono<ResultT<List<SysMenuResponse>>> updateMenu(@RequestBody SysMenu sysMenu) {
        return sysMenuService.updateById(sysMenu)
                .collectList()
                .flatMap(menuList ->
                        menuTreeUtils.buildMenuTree(menuList)
                                .collectList()
                )
                .map(menuList ->
                        ResultT.<List<SysMenuResponse>>builder()
                                .code(HttpCodeConst.OK)
                                .msg("成功")
                                .data(menuList)
                                .build()
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<List<SysMenuResponse>>builder()
                                        .code(HttpCodeConst.UNAUTHORIZED)
                                        .msg("修改失败")
                                        .data(null)
                                        .build()
                        )
                )
                .onErrorResume(throwable ->
                        Mono.just(
                                ResultT.<List<SysMenuResponse>>builder()
                                        .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                        .msg("系统错误")
                                        .data(null)
                                        .build()
                        )
                );
    }

    //获取菜单树
    @GetMapping("/menuList")
    public Mono<ResultT<List<SysMenuResponse>>> menuList() {
        return sysMenuService.findAll()
                .collectList()
                .flatMap(menuList ->
                        menuTreeUtils.buildMenuTree(menuList)
                                .collectList()
                )
                .map(menuList ->
                        ResultT.<List<SysMenuResponse>>builder()
                                .code(HttpCodeConst.OK)
                                .msg("成功")
                                .data(menuList)
                                .build()
                ).onErrorResume(throwable ->
                        Mono.just(
                                ResultT.<List<SysMenuResponse>>builder()
                                        .code(HttpCodeConst.UNAUTHORIZED)
                                        .msg("获取菜单树失败")
                                        .data(null)
                                        .build()
                        )
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<List<SysMenuResponse>>builder()
                                        .code(HttpCodeConst.UNAUTHORIZED)
                                        .msg("获取菜单树失败")
                                        .data(null)
                                        .build()
                        )
                );
    }
}
