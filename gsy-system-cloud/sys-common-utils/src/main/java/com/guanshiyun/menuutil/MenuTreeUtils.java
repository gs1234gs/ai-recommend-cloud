package com.guanshiyun.menuutil;

import cn.hutool.core.bean.BeanUtil;
import com.guanshiyun.menupojo.SysMenu;
import com.guanshiyun.menupojo.reponse.SysMenuResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MenuTreeUtils {
    public Flux<SysMenuResponse> buildMenuTree(List<SysMenu> menus) {
        List<SysMenuResponse> sysMenuResponseList = menus.stream().map(
                menu -> BeanUtil.toBean(menu, SysMenuResponse.class)
        ).toList();
        // 将菜单按父节点分组
        Map<BigInteger, List<SysMenuResponse>> parentMap = sysMenuResponseList.stream()
                .collect(Collectors.groupingBy(SysMenuResponse::getParentId));

        // 递归构建树
        return Flux.fromIterable(sysMenuResponseList)
                .filter(menu -> BigInteger.valueOf(0L).equals(menu.getParentId()))  // 找到根菜单（parentId = "0"）
                .map(rootMenu -> buildMenuNode(rootMenu, parentMap))
                .collectList()
                .flatMapMany(Flux::fromIterable);
    }

    private SysMenuResponse buildMenuNode(SysMenuResponse parentMenu,
                                          Map<BigInteger, List<SysMenuResponse>> parentMap) {
        List<SysMenuResponse> children = parentMap.get(parentMenu.getId());
        if (children != null && !children.isEmpty()) {
            parentMenu.setChildren(children.stream()
                    .map(child -> buildMenuNode(child, parentMap))  // 递归构建子节点
                    .toList());
        }
        return parentMenu;
    }

}
