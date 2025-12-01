package com.guanshiyun.controller.sysmenu;

import cn.hutool.core.bean.BeanUtil;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.code.HttpCodeConst;
import com.guanshiyun.menupojo.SysMenu;
import com.guanshiyun.menupojo.reponse.SysMenuResponse;
import com.guanshiyun.menuutil.MenuTreeUtils;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.sysmenu.SysMenuService;
import com.guanshiyun.tree.TreeUtil;
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
    @GetMapping("findById/{id}")
    public Mono<ResultT<SysMenuResponse>> findById(@PathVariable BigInteger id) {
        return sysMenuService.findById(id)
                .map(menu ->
                        ResultT.<SysMenuResponse>builder()
                                .code(HttpCodeConst.OK)
                                .msg("成功")
                                .data(menu)
                                .build()
                )
                .onErrorResume(throwable -> {
                    log.warn("获取菜单失败", throwable);
                    return Mono.just(ResultT.<SysMenuResponse>builder()
                            .code(HttpCodeConst.UNAUTHORIZED)
                            .msg("获取菜单失败")
                            .build()
                    );
                });
    }
    @GetMapping("/findByUserId")
    public Mono<ResultT<List<SysMenuResponse>>> findByUserId() {
        return sysMenuService.findMenuByUserId()
                .map(menu -> BeanUtil.toBean(menu, SysMenuResponse.class))
                .collectList()
                .map(menuList -> TreeUtil.buildTree(menuList,
                        SysMenuResponse.Fields.id,
                        SysMenuResponse.Fields.parentId,
                        SysMenuResponse.Fields.children,
                        SysMenuResponse.Fields.sort
                        )
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
    @GetMapping({"/findByParentId/{id}"})
    public Mono<ResultT<List<SysMenuResponse>>> sysMenuList(
            @PathVariable(name = "id", required = false) BigInteger id) {
        return sysMenuService.findAllByParentId(id)
                .map(menu -> BeanUtil.toBean(menu, SysMenuResponse.class))
                .collectList()
                .flatMap(menuList ->
                        Mono.just(
                                ResultT.<List<SysMenuResponse>>builder()
                                        .code(HttpCodeConst.OK)
                                        .msg("成功")
                                        .data(TreeUtil.buildTree(
                                                menuList,
                                                SysMenuResponse.Fields.id,
                                                SysMenuResponse.Fields.parentId,
                                                SysMenuResponse.Fields.children,
                                                SysMenu.Fields.sort
                                                ))
                                        .build()
                        )
                );
    }

    //删除菜单
    @DeleteMapping("/deleteById/{id}")
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

    //添加菜单或者更新菜单
    @PostMapping("/save")
    public Mono<ResultT<BigInteger>> addMenu(@RequestBody SysMenu sysMenu) {
        return sysMenuService.save(sysMenu)
                .map(id ->
                        ResultT.<BigInteger>builder()
                                .code(HttpCodeConst.OK)
                                .msg("成功")
                                .data(id)
                                .build()
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<BigInteger>builder()
                                        .code(HttpCodeConst.UNAUTHORIZED)
                                        .msg("添加失败")
                                        .data(null)
                                        .build()
                        )
                )
                .onErrorResume(throwable ->
                        Mono.just(
                                ResultT.<BigInteger>builder()
                                        .code(HttpCodeConst.INTERNAL_SERVER_ERROR)
                                        .msg("系统错误")
                                        .data(null)
                                        .build()
                        )
                );
    }

    //修改菜单
    @PutMapping("/updateById")
    public Mono<ResultT<BigInteger>> updateMenu(@RequestBody SysMenu sysMenu) {
        return sysMenuService.updateById(sysMenu)
                .map(id ->
                        ResultT.<BigInteger>builder()
                                .code(HttpCodeConst.OK)
                                .msg("成功")
                                .data(id)
                                .build()
                )
                .switchIfEmpty(
                        Mono.just(
                                ResultT.<BigInteger>builder()
                                        .code(HttpCodeConst.UNAUTHORIZED)
                                        .msg("修改失败")
                                        .data(null)
                                        .build()
                        )
                )
                .onErrorResume(throwable ->
                        Mono.just(
                                ResultT.<BigInteger>builder()
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

    //获取菜单树
    @GetMapping("/findAll")
    public Mono<ResultT<List<SysMenuResponse>>> findAll() {
        return sysMenuService.findAll()
                .map(menu ->BeanUtil.toBean(menu, SysMenuResponse.class))
                .collectList()
                .map(menuList ->
                        TreeUtil.buildTree(menuList,
                                SysMenuResponse.Fields.id,
                                SysMenuResponse.Fields.parentId,
                                SysMenuResponse.Fields.children,
                                SysMenuResponse.Fields.sort
                                )
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
    //根据角色id获取菜单
    @GetMapping("/findMenuByRoleId/{roleId}")
    public Mono<ResultT<List<SysMenuResponse>>> findMenuByRoleId(@PathVariable BigInteger roleId) {
        return sysMenuService.findMenuByRoleId(roleId)
                .map(menu ->BeanUtil.toBean(menu, SysMenuResponse.class))
                .collectList()
                .map(menuList ->
                        ResultT.<List<SysMenuResponse>>builder()
                                .code(HttpCodeConst.OK)
                                .msg("成功")
                                .data(TreeUtil.buildTree(menuList,
                                        SysMenuResponse.Fields.id,
                                        SysMenuResponse.Fields.parentId,
                                        SysMenuResponse.Fields.children,
                                        SysMenuResponse.Fields.sort
                                ))
                                .build()
                )
                .onErrorResume(throwable ->{
                    return Mono.just(
                            ResultT.<List<SysMenuResponse>>builder()
                                    .code(HttpCodeConst.UNAUTHORIZED)
                                    .msg("获取菜单树失败")
                                    .data(null)
                                    .build()
                    );
                });
    }
}
