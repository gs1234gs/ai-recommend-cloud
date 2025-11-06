package com.guanshiyun.service.signin.impl;


import com.alibaba.fastjson.JSON;
import com.guanshiyun.consts.ConstClassNickName;
import com.guanshiyun.menupojo.SysMenu;
import com.guanshiyun.repository.signin.SignInUpRepository;
import com.guanshiyun.responsepojo.Result;
import com.guanshiyun.roleId.RoleIdConst;
import com.guanshiyun.security.redisConfig.ReactiveRedisUtil;
import com.guanshiyun.service.signin.SignInUpService;
import com.guanshiyun.service.sysmenu.SysMenuService;
import com.guanshiyun.service.sysmenurole.SysRoleMenuService;
import com.guanshiyun.service.userrole.SysUserRoleService;
import com.guanshiyun.signinpojo.SignUser;
import com.guanshiyun.userpojo.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Override
    public Mono<SignUser> signIn(String username) {
        return signInUpRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("用户不存在")))
                .flatMap(this::buildUserDetails);
    }

    @Override
    public Mono<Result> signUp(SysUser signUser) {
        SysUser sysUser = SysUser.builder()
                .id(null)
                .createTime(LocalDateTime.now())
                .username(signUser.getUsername())
                .password(passwordEncoder.encode(signUser.getPassword()))
                .nickName(signUser.getNickName())
                .build();
        return signInUpRepository.findByUsername(signUser.getUsername())
                .flatMap(existUser -> Mono.just(Result.builder()
                                .code(400)
                                .msg("用户已存在,请重新注册")
                                .data(null)
                                .build()
                        )
                )
                .switchIfEmpty(signInUpRepository.save(sysUser)
                        .flatMap(user ->
                                sysUserRoleService.addUserRole(sysUser.getId(), RoleIdConst.ROLE_COMMON_USER)
                                        .flatMap(result ->
                                                Mono.just(
                                                        Result.builder()
                                                                .code(200)
                                                                .msg("注册成功")
                                                                .data(null)
                                                                .build()
                                                )

                                        )
                        )
                )
                .onErrorResume(throwable -> {
                    log.error("注册失败", throwable);
                    return Mono.just(Result.builder()
                            .code(500)
                            .msg("注册失败")
                            .data(null)
                            .build()
                    );
                });
    }

    private Mono<SignUser> buildUserDetails(SysUser user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // 添加用户类型角色（前缀"ROLE_"）
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getType()));
        return sysMenuFlux(user.getId())
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
                            return redisUtil.hSet(ConstClassNickName.REDIS_PERMISSION_KEY,
                                    user.getId(),
                                    JSON.toJSONString(urlList)
                            ).then(redisUtil.expire(ConstClassNickName.REDIS_PERMISSION_KEY,
                                    60));
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

    public Flux<SysMenu> sysMenuFlux(BigInteger userId) {

        return sysUserRoleService.findRoleIdsByUserId(userId)
                .collectList()
                .flatMapMany(roleIds ->
                        sysRoleMenuService.findMenuIdsByRoleId(roleIds)
                )
                .distinct()
                .collectList()
                .flatMapMany(menuIds ->
                        sysMenuService.findByIds(menuIds));
    }

}
