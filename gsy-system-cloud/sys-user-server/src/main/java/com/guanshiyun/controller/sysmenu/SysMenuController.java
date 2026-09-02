package com.guanshiyun.controller.sysmenu;

import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.menupojo.SysMenu;
import com.guanshiyun.menupojo.reponse.SysMenuResponse;
import com.guanshiyun.menuutil.MenuTreeUtils;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.sysmenu.SysMenuService;
import com.guanshiyun.tree.TreeUtil;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sysMenu")
@RequiredArgsConstructor
public class SysMenuController
{
    private final SysMenuService sysMenuService;
    private final MenuTreeUtils menuTreeUtils;


    //根据用户id获取菜单,用户id从当前线程获取，避免传入，影响安全
    @GetMapping("/userId")
    public Mono<ResultT<List<SysMenuResponse>>> findMenuByUserId()
    {
        return sysMenuService.findMenuByUserId()
                .collectList()
                .flatMap(menuList ->
                        menuTreeUtils.buildMenuTree(menuList)
                                .collectList()
                )
                .map(menuList -> // 明确泛型
                        {
                            log.info("获取菜单成功：{}", menuList.size());
                            return ResultT.success(menuList);
                        }
                );
    }

    @GetMapping("findById/{id}")
    public Mono<ResultT<SysMenuResponse>> findById(@PathVariable Long id)
    {
        return sysMenuService.findById(id)
                .map(ResultT::success)
                .onErrorResume(throwable -> {
                    log.warn("获取菜单失败", throwable);
                    return Mono.just(ResultT.error("获取菜单失败 ： "));
                });
    }

    @GetMapping("/findByUserId")
    public Mono<ResultT<List<SysMenuResponse>>> findByUserId()
    {
        // 明确泛型
        return sysMenuService.findMenuByUserId()
                .map(menu -> BeanConvertUtil.toBean(menu, SysMenuResponse.class))
                .collectList()
                .map(menuList ->
                        TreeUtil.buildTree(
                                menuList,
                                SysMenuResponse.Fields.id,
                                SysMenuResponse.Fields.parentId,
                                SysMenuResponse.Fields.children,
                                SysMenuResponse.Fields.sort
                        )
                )
                .map(ResultT::success)
                .onErrorResume(e->{
                    log.warn("获取菜单失败", e);
                    return Mono.just(ResultT.error("获取菜单失败 ： " + e.getMessage()));
                });
    }

    //获取菜单列表
    @GetMapping({"/findByParentId/{id}"})
    public Mono<ResultT<List<SysMenuResponse>>> findAllByParentId(
            @PathVariable(name = "id", required = false) Long id)
    {
        return sysMenuService.findAllByParentId(id)
                .map(menu -> BeanConvertUtil.toBean(menu, SysMenuResponse.class))
                .collectList()
                .flatMap(menuList -> Mono.just(
                        ResultT.success(TreeUtil.buildTree(
                                        menuList,
                                        SysMenuResponse.Fields.id,
                                        SysMenuResponse.Fields.parentId,
                                        SysMenuResponse.Fields.children,
                                        SysMenu.Fields.sort
                                ))
                        )
                );
    }

    //删除菜单
    @DeleteMapping("/deleteById/{id}")
    public Mono<ResultT<Long>> deleteById(@PathVariable Long id)
    {
        return sysMenuService.deleteById(id)
                .map(result ->
                        result >= ConstNumber.INT_ONE ? ResultT.<Long>success() : ResultT.<Long>error("删除失败，该菜单不能删除")
                )
                .onErrorResume(throwable -> {
                    log.warn("删除菜单失败", throwable);
                    return Mono.just(ResultT.error("删除失败 "));
                });
    }

    //添加菜单或者更新菜单
    @PostMapping("/save")
    public Mono<ResultT<Long>> save(@RequestBody SysMenu sysMenu)
    {
        return sysMenuService.save(sysMenu)
                .map(ResultT::success)
                .switchIfEmpty(Mono.just(ResultT.error("添加失败")))
                .onErrorResume(throwable -> Mono.just(ResultT.error("添加失败")));
    }

    //修改菜单
    @PutMapping("/updateById")
    public Mono<ResultT<Long>> updateMenu(@RequestBody SysMenu sysMenu)
    {
        return sysMenuService.updateById(sysMenu)
                .map(ResultT::success)
                .switchIfEmpty(Mono.just(ResultT.error("修改失败")))
                .onErrorResume(throwable -> Mono.just(ResultT.error("修改失败")));
    }

    //获取菜单树
    @GetMapping("/menuList")
    public Mono<ResultT<List<SysMenuResponse>>> findAllMenuList()
    {
        return sysMenuService.findAll()
                .collectList()
                .flatMap(menuList -> menuTreeUtils.buildMenuTree(menuList).collectList())
                .map(ResultT::success)
                .onErrorResume(throwable -> Mono.just(ResultT.error("获取菜单树失败")))
                .switchIfEmpty(Mono.just(ResultT.error("获取菜单树失败")));
    }

    //获取菜单树
    @GetMapping("/findAll")
    public Mono<ResultT<List<SysMenuResponse>>> findAll()
    {
        return sysMenuService.findAll()
                .map(menu -> BeanConvertUtil.toBean(menu, SysMenuResponse.class))
                .collectList()
                .map(menuList ->
                        TreeUtil.buildTree(menuList,
                                SysMenuResponse.Fields.id,
                                SysMenuResponse.Fields.parentId,
                                SysMenuResponse.Fields.children,
                                SysMenuResponse.Fields.sort
                        )
                )
                .map(ResultT::success)
                .onErrorResume(throwable -> Mono.just(ResultT.error("获取菜单树失败")))
                .switchIfEmpty(Mono.just(ResultT.error("获取菜单树失败")));
    }

    //根据角色id获取菜单
    @GetMapping("/findMenuByRoleId/{roleId}")
    public Mono<ResultT<List<SysMenuResponse>>> findMenuByRoleId(@PathVariable Long roleId)
    {
        return sysMenuService.findMenuByRoleId(roleId)
                .map(menu -> BeanConvertUtil.toBean(menu, SysMenuResponse.class))
                .collectList()
                .map(menuList -> ResultT.success(TreeUtil.buildTree(
                                menuList,
                                SysMenuResponse.Fields.id,
                                SysMenuResponse.Fields.parentId,
                                SysMenuResponse.Fields.children,
                                SysMenuResponse.Fields.sort
                        ))
                )
                .onErrorResume(throwable -> Mono.just(ResultT.error("获取菜单树失败")));
    }
}
