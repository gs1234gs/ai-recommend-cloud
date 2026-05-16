package com.guanshiyun.security.reponse;

import com.guanshiyun.consts.ConstClassNickName;
import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.emailutil.QQEmailUtil;
import com.guanshiyun.reactiveredis.ReactiveRedisUtil;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.qq.QQEmailApi;
import com.guanshiyun.rpc.qqCode.QQCode;
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

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
@Component
public class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private final PasswordEncoder passwordEncoder;
    private final SignInUpService signInUpService;
    private final QQEmailApi qqEmailApi;
    private final ReactiveRedisUtil reactiveRedisUtil;

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
                            //大于30，非原始测试用户，校验邮箱验证码及邮箱格式
                            return Mono.just(new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    List.of()
                            ));

                        }
                );
    }
    //发送验证码
    public Mono<String> sendVerificationCode(String email) {
        //先判断是不是邮箱类型，不是则直接返回错误
        if (QQEmailUtil.isNotValidEmail(email)) {
            return Mono.error(new Throwable("请输入正确的邮箱格式"));
        }
        String redisEmailKey = ConstClassNickName.REDIS_EMAIL_KEY;
        //格式正确,生成验证码，写入redis，发送验证码校验
        String value = generateVerificationCode();
        return reactiveRedisUtil.hSet(redisEmailKey, email, value)
                .flatMap(rs->{
                    //设置过期时间
                    Mono<Boolean> booleanMono = reactiveRedisUtil.expire(ConstClassNickName.REDIS_EMAIL_KEY, 60 * 10);
                    //发送验证码到客户端
                    Mono<ResultT<Boolean>> resultTMono = qqEmailApi.sendVerificationCode(
                            QQCode.builder()
                                    .email(email)
                                    .code(value)
                                    .expire(ConstNumber.INT_TEN)
                                    .build());
                    return Mono.zip(booleanMono, resultTMono)
                            .flatMap(tuple->{
                                //如果为false
                                if(!tuple.getT2().getData()){
                                    return Mono.error(new Throwable("验证码发送失败"));
                                }
                                return Mono.just("验证码发送成功");
                            })
                            .onErrorResume(e -> Mono.error(new RuntimeException("验证码发送失败", e)));
                });
    }
    //校验验证码
    public Mono<Boolean> checkCode(String email,String code) {
        if(QQEmailUtil.isNotValidEmail(email)) {
            return Mono.error(new Throwable("请输入正确的邮箱格式"));
        }
        if(Objects.isNull(code)){
            return Mono.error(new Throwable("请输入验证码"));
        }
        return reactiveRedisUtil.hGet(ConstClassNickName.REDIS_EMAIL_KEY, email)
                .switchIfEmpty(Mono.defer(()->{
                   return Mono.error(new Throwable("验证码已过期"));
                }))
                .flatMap(c -> {
                    if(!c.equals(code)) {
                        return Mono.error(new Throwable("验证码错误"));
                    }
                    return Mono.just(true);
                })
                .onErrorResume(e -> Mono.error(new RuntimeException("验证码错误", e)));
    }


    //生成六位数验证码
    private String generateVerificationCode() {
        SecureRandom secureRandom = new SecureRandom();
// 生成 100000 到 999999 之间的数，确保一定是 6 位（不会出现 012345 这种情况）
        int codeInt = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(codeInt);
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
