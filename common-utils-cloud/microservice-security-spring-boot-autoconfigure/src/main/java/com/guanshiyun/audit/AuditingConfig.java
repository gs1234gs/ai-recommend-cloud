package com.guanshiyun.audit;

import com.guanshiyun.mylong.MyLong;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import reactor.core.publisher.Mono;

@Configuration
@EnableR2dbcAuditing
@RequiredArgsConstructor
public class AuditingConfig implements ReactiveAuditorAware<Long> {
        private final MyLong myLong;
    @Override
    public @NotNull Mono<Long> getCurrentAuditor() {
        return Mono.deferContextual(ctx->{
            if (!myLong.hasKey(ctx)) {
                return Mono.empty();
            }
            Long userId = myLong.findUserId(ctx);
            return Mono.just(userId);
        });

    }
}
