package com.guanshiyun.repository.sysmenu;

import com.guanshiyun.menupojo.SysMenu;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;



public interface SysMenuRepository extends R2dbcRepository<SysMenu, Long> {
    // 根据父级id查询菜单
    @Query("SELECT * FROM sys_menu WHERE parent_id = :parentId")
    Flux<SysMenu> findAllByParentId(Long parentId);
}
