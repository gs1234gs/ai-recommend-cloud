package com.guanshiyun.utils;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import java.lang.reflect.Field;

public class BeanConvertUtil {

    /**
     * 将 source 转换成 targetClass 类型的 Bean
     * 自动根据字段名匹配，支持任意类型
     */
    public static <T> T toBean(Object source, Class<T> targetClass) {
        if (source == null) return null;

        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Bean conversion failed", e);
        }
    }

    /**
     * 将 source 的非空字段覆盖到已有 target Bean
     */
    public static void copyNonNullToTarget(Object source, Object target) {
        if (source == null || target == null) return;

        try {
            copyProperties(source, target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Copy properties failed", e);
        }
    }

    /**
     * 根据字段名匹配，把 source 的值拷贝到 target（非空才覆盖）
     */
    private static void copyProperties(Object source, Object target) throws IllegalAccessException {
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Field[] targetFields = target.getClass().getDeclaredFields();

        for (Field targetField : targetFields) {
            targetField.setAccessible(true);
            for (Field sourceField : sourceFields) {
                sourceField.setAccessible(true);
                if (sourceField.getName().equals(targetField.getName())
                        && sourceField.getType().equals(targetField.getType())) {
                    Object value = sourceField.get(source);
                    if (value != null) {
                        targetField.set(target, value);
                    }
                    break;
                }
            }
        }
    }
}

