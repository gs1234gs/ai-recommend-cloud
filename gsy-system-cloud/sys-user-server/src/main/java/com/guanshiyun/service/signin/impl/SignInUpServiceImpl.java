package com.guanshiyun.service.signin.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanshiyun.consts.ConstClassNickName;
import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.menupojo.SysMenu;
import com.guanshiyun.reactiveredis.ReactiveRedisUtil;
import com.guanshiyun.repository.signin.SignInUpRepository;
import com.guanshiyun.roleId.RoleIdConst;
import com.guanshiyun.service.signin.SignInUpService;
import com.guanshiyun.service.sysmenu.SysMenuService;
import com.guanshiyun.service.sysmenurole.SysRoleMenuService;
import com.guanshiyun.service.userrole.SysUserRoleService;
import com.guanshiyun.signinpojo.SignUser;
import com.guanshiyun.user.User;
import com.guanshiyun.userpojo.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignInUpServiceImpl implements SignInUpService {
    private final SignInUpRepository signInUpRepository;
    private final SysUserRoleService sysUserRoleService;
    private final PasswordEncoder passwordEncoder;
    private final SysMenuService sysMenuService;
    private final SysRoleMenuService sysRoleMenuService;
    private final ReactiveRedisUtil redisUtil;
    private final TransactionalOperator transactionalOperator;
    private final ObjectMapper  objectMapper;
    private final GorseClient gorseClient;

    @Override
    public Mono<SignUser> signIn(String username) {
        return signInUpRepository.findByUsername(username.trim())
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("用户不存在")))
                .flatMap(this::buildUserDetails);
    }

    @Override
    public Mono<Boolean> signUp(SignUser signUser) {
//        if(signUser.getUsername().trim().length()<6){
//            return Mono.error(new Throwable("用户名长度不能小于6"));
//        }
        String username = signUser.getUsername();
        SysUser sysUser = SysUser.builder()
                .id(null)
                .createTime(LocalDateTime.now())
                .username(username)
                .password(passwordEncoder.encode(signUser.getPassword()))
                .nickName(signUser.getNickName())
                .build();
        return signInUpRepository.findByUsername(username)
                .map(existUser -> Boolean.FALSE)
                .switchIfEmpty(signInUpRepository.save(sysUser)
                        .flatMap(user ->
                                sysUserRoleService.addUserRole(user.getId(), List.of(RoleIdConst.ROLE_COMMON_USER))
                                        .flatMap(result -> {
                                            //添加角色
                                            log.info("注册成功: {}", result);
                                            LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
                                            linkedHashMap.put(SysUser.Fields.username, username);
                                            linkedHashMap.put("school", "滇西应用技术大学");
                                            linkedHashMap.put("location", "Dali of Yunnan in China");
                                            User gorseUser = User.builder()
                                                    .userId(String.valueOf(user.getId()))
                                                    .labels(linkedHashMap)
                                                    .build();
                                            return gorseClient.saveUser(gorseUser)
                                                    .thenReturn(Boolean.TRUE);
                                                }
                                        )
                        )
                        .transform(transactionalOperator::transactional)
                )
                .onErrorResume(throwable -> {
                    log.error("注册失败", throwable);
                    return Mono.error(new RuntimeException("注册失败"));
                });
    }

    private Mono<SignUser> buildUserDetails(SysUser user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // 添加用户类型角色（前缀"ROLE_"）
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getType()));
        return sysMenu(user.getId())
                .collectList()
                .flatMap(menus -> {
//                         return  Mono.just(
//                                 menus.stream()
//                                         .map(SysMenu::getPath)
//                                         .toList()
//                         );
                            List<String> urlList = menus.stream()
                                    .map(SysMenu::getPath)
                                    .toList();

                            return Mono.fromCallable(()->objectMapper.writeValueAsString(urlList))
                                    .flatMap(urlJson->redisUtil.hSet(ConstClassNickName.REDIS_PERMISSION_KEY,
                                            String.valueOf(user.getId()),
                                            urlJson

                                    ).then(redisUtil.expire(ConstClassNickName.REDIS_PERMISSION_KEY,
                                            60)))
                                    .onErrorResume(JsonProcessingException.class, e -> {
                                        log.error("JSON 序列化失败", e);
                                        return Mono.error(new Throwable("JSON 序列化失败",e)); // 或者返回默认值
                                    });

//                            return redisUtil.hSet(ConstClassNickName.REDIS_PERMISSION_KEY,
//                                    user.getId(),
////                                    JSON.toJSONString(urlList)
//                                    urlJson
//
//                            ).then(redisUtil.expire(ConstClassNickName.REDIS_PERMISSION_KEY,
//                                    60));
                        }
                )
                .flatMap(menus -> {
                    return Mono.just(SignUser.builder()
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .authorities(authorities)
                            .sysUser(user)
                            .build());
                });
    }

    public Flux<SysMenu> sysMenu(Long userId) {

        return sysUserRoleService.findRoleIdsByUserId(userId)
                .collectList()
                .flatMapMany(sysRoleMenuService::findMenuIdsByRoleId
                )
                .distinct()
                .collectList()
                .flatMapMany(sysMenuService::findByIds);
    }

}
