package com.guanshiyun.utils;

import cn.hutool.core.bean.BeanUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BeanConvertUtil {

    /**
     * 将 source 转换成 targetClass 类型的 Bean
     * 自动根据字段名匹配，支持任意类型
     */
    public static <T> T toBean(Object source, Class<T> targetClass) {
        if (Objects.isNull( source)) return null;
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
        if (Objects.isNull( source) || Objects.isNull( target)) return;

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
                    if (Objects.nonNull( value)) {
                        targetField.set(target, value);
                    }
                    break;
                }
            }
        }
    }
    public static <T> List<T> toBeanList(Collection<T> source, Class<T> targetClass){
        return source.stream()
                .map(
                        item ->
                                BeanUtil
                                        .toBean(item,
                                                targetClass))
                .toList();
    }

    public static <T, C extends Collection<T>> C toBean(
            Collection<?> source,
            Class<T> targetClass,
            Supplier<C> collectionFactory) { // 工厂函数创建目标集合

        if (source == null || source.isEmpty()) {
            return collectionFactory.get();
        }

        return source.stream()
                .map(item -> BeanUtil.toBean(item, targetClass))
                .collect(Collectors.toCollection(collectionFactory));
    }
    public static <T> Collection<T> toBean(Collection<?> source, Class<T> targetClass) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<T> result;
        if (source instanceof Set) {
            result = new HashSet<>();
        } else if (source instanceof Queue) {
            result = new LinkedList<>();
        } else {
            result = new ArrayList<>();
        }

        source.forEach(item -> result.add(BeanUtil.toBean(item, targetClass)));
        return result;
    }
}

