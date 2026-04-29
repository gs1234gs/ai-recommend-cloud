package com.guanshiyun.exception;


import com.guanshiyun.responsepojo.Result;
import com.guanshiyun.responsepojo.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
public class GlobalException {
    /**
     * 捕获所有未处理的异常
     */
    @ExceptionHandler(Throwable.class)
    public Mono<ResultT<Object>> handleAllException(Throwable e, ServerWebExchange exchange) {
        log.error("全局异常捕获：{}", e.getMessage(), e);

        return Mono.just(ResultT.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .msg(e.getMessage())
                .data(null) // 可返回堆栈信息（生产可删）
                .build());
    }

    /**
     * 捕获自定义异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Mono<Result> handleRuntimeException(RuntimeException ex) {
        return Mono.just(Result.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .msg(ex.getMessage())
                .data(null)
                .build());
    }

}
