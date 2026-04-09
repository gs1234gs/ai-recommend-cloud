package com.guanshiyun.controller.logout;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.logout.LogoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/logout")
public class LogoutController {
    private final LogoutService logoutService;
    //退出登陆
    @GetMapping()
    public Mono<ResultT<Long>> logout(){
        return logoutService.logout()
                .map(aLong ->
                        ResultT.<Long>builder()
                                .code(HttpStatus.OK.value())
                                .msg("退出成功！")
                                .data(aLong)
                                .build()
                )
                .onErrorResume(throwable ->{
                    log.error("退出失败！", throwable);
                    return Mono.just(ResultT.<Long>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .msg("退出失败！")
                            .data(null)
                            .build());
                        }
                        );
    }
}
