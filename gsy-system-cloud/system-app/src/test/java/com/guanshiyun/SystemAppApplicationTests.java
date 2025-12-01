package com.guanshiyun;


import com.guanshiyun.repository.userrole.SysUserRoleRepository;
import com.guanshiyun.service.sysmenu.SysMenuService;
import com.guanshiyun.service.sysmenurole.SysRoleMenuService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@SpringBootTest
class SystemAppApplicationTests {

    @Autowired
    private SysUserRoleRepository sysUserRoleRepository;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Autowired
    private SysMenuService sysMenuService;

//    @Test
//    void contextLoads() {
//        sysUserRoleRepository.findRoleIdByUserId(BigInteger.valueOf(3L))
//                .collectList()
//                .subscribe(list -> System.out.println(list));
//        sysUserRoleRepository.existsByUserIdAndRoleId(BigInteger.valueOf(3L), BigInteger.valueOf(1L))
//                .subscribe(exist -> System.out.println(exist));
//    }

    @Test
    void test() {
        sysRoleMenuService.findMenuIdsByRoleId(List.of(BigInteger.valueOf(1L)))
                .collectList()
                .flatMap(menuIds ->
                        sysMenuService.findByIds(menuIds)
                                .collectList()

                        )
                .subscribe(list -> System.out.println(list));
    }

    @Test
    void test2() {
        sysMenuService.findAllByParentId(BigInteger.TWO)
                .collectList()
                .subscribe(list -> {
                    log.info("====================");
                   log.info("：{}",list);
                    log.info("====================");
                });
    }

}
