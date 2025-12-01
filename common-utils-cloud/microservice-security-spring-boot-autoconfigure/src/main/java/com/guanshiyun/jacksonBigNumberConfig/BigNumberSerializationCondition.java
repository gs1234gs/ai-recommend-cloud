package com.guanshiyun.jacksonBigNumberConfig;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

import java.util.Objects;

public class BigNumberSerializationCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context,@NonNull AnnotatedTypeMetadata metadata) {
        // 如果类或者方法被 @UseBigNumberSerialization 注解标识，返回 true
        return !Objects.requireNonNull(context.getBeanFactory()).getBeansWithAnnotation(UseBigNumberSerialization.class).isEmpty();
    }
}
