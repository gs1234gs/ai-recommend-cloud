package com.guanshiyun.security.reponse;

import com.guanshiyun.service.signin.SignInUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private final PasswordEncoder passwordEncoder;
    private final SignInUpService signInUpService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        if (!(authentication instanceof UsernamePasswordAuthenticationToken auth)) {
            return Mono.error(new Throwable("无效用户"));
        }

        String username = auth.getName();
        String password = (String) auth.getCredentials();
        //认证成功
        return signInUpService.signIn(username)
                .switchIfEmpty(Mono.error(new Throwable("用户不存在")))
                .flatMap(user -> {
                            if (!passwordEncoder.matches(password, user.getPassword())) {
                                return Mono.error(new BadCredentialsException("用户名或密码错误"));
                            }
                            return Mono.just(new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    List.of()
                            ));

                        }
                );
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123456"));
        System.out.println(bCryptPasswordEncoder.matches(
                "123456",
                "$2a$10$HeOf0/bbu3etDUEjwTs6Eu.rR/NX.lYjir66sGhxB5E8m4GoXMDCi"
        ));

    }
}
