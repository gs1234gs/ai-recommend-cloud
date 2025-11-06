package com.guanshiyun.utils;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;
@Slf4j
public class OnDisableBusinessWebFilterCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context,@Nullable AnnotatedTypeMetadata metadata) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        // 获取所有带有 @DisableBusinessWebFilter 注解的 Bean 名称
        String[] beanNames = Objects.requireNonNull(context.getBeanFactory())
                .getBeanNamesForAnnotation(DisableBusinessWebFilter.class);
        log.info("🔍 正在检查 @DisableBusinessWebFilter...");
        log.info("📊 找到带 @DisableBusinessWebFilter 的 Bean 数量: " + beanNames.length);
        for (String name : beanNames) {
            assert beanFactory != null;
            log.info("  ➕ " + name + " (" + beanFactory.getType(name) + ")");
        }

        // 如果存在任何 Bean（通常是主配置类）加了这个注解
        // 说明当前服务想禁用该 Filter → 返回 false（不匹配，即不加载）
        return beanNames.length == 0; // 只有没加注解时才加载
    }
}
