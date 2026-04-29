package com.guanshiyun;


import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.repository.sysuser.SysUserRepository;
import com.guanshiyun.repository.userrole.SysUserRoleRepository;
import com.guanshiyun.service.signin.SignInUpService;
import com.guanshiyun.service.sysmenu.SysMenuService;
import com.guanshiyun.service.sysmenurole.SysRoleMenuService;
import com.guanshiyun.signinpojo.SignUser;
import com.guanshiyun.user.User;
import com.guanshiyun.userpojo.SysUser;
import io.gorse.gorse4j.Gorse;
import io.gorse.gorse4j.Item;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
//        sysUserRoleRepository.findRoleIdByUserId(Long.valueOf(3L))
//                .collectList()
//                .subscribe(list -> System.out.println(list));
//        sysUserRoleRepository.existsByUserIdAndRoleId(Long.valueOf(3L), Long.valueOf(1L))
//                .subscribe(exist -> System.out.println(exist));
//    }

    @Test
    void test() {
        sysRoleMenuService.findMenuIdsByRoleId(List.of(Long.valueOf(1L)))
                .collectList()
                .flatMap(menuIds ->
                        sysMenuService.findByIds(menuIds)
                                .collectList()

                        )
                .subscribe(list -> System.out.println(list));
    }

    @Test
    void test2() {
        sysMenuService.findAllByParentId(Long.valueOf(2L))
                .collectList()
                .subscribe(list -> {
                    log.info("====================");
                   log.info("：{}",list);
                    log.info("====================");
                });
    }

    @Resource
    private SysUserRepository sysUserRepository;

    @Resource
    private GorseClient gorseClient;

    @Test
    void test3() {
        sysUserRepository.findAll()
                .flatMap(user->{
                    System.out.println("正在同步用户: " + user.getUsername());
                    System.out.println("===============================");
                    LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
                    linkedHashMap.put(SysUser.Fields.username, user.getUsername());
                    linkedHashMap.put("school", "滇西应用技术大学");
                    linkedHashMap.put("location", "Dali of Yunnan in China");
                    User gorseUser = User.builder()
                            .userId(String.valueOf(user.getId()))
                            .labels(linkedHashMap)
                            .build();
                    return gorseClient.saveUser(gorseUser)
                            .doOnSuccess(v -> System.out.println("同步成功: " + user.getUsername()))
                            .onErrorResume(e -> {
                                System.err.println("同步失败: " + user.getUsername());
                                return Mono.error(e);
                            });
                })
                .then()
                .block();
    }

    @Autowired
    SignInUpService signInUpService;
    @Test
    void test4() throws IOException {
        SignUser block = signInUpService.signIn("15287218571")
                .block();
        System.out.println("==================================");
        System.out.println(block);
        Gorse client = new Gorse("http://127.0.0.1:8087", "api_key");
        client.insertUser(new io.gorse.gorse4j.User("bob", Map.of(
                "company", "gorse",
                "location", "hangzhou, china"
        )));
        client.insertItem(new Item("gorse-io:gorse", false, Map.of(
                "topics", List.of("recommendation", "machine-learning")
        ), List.of("go"), "2022-02-22", "Gorse is an open-source recommender system."));

    }
}
