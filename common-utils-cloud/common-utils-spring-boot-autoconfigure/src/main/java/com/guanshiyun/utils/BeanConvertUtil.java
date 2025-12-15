package com.guanshiyun.utils;

import cn.hutool.core.bean.BeanUtil;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.PageResultT;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BeanConvertUtil {

    /**
     * 将 source 转换成 targetClass 类型的 Bean
     */
    public static <T> T toBean(Object source, Class<T> targetClass) {
        if (Objects.isNull(source)) return null;
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
        if (Objects.isNull(source) || Objects.isNull(target)) return;
        try {
            copyProperties(source, target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Copy properties failed", e);
        }
    }

    /**
     * 根据字段名匹配，把 source 的值拷贝到 target（非空才覆盖，浅拷贝）
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
                    if (Objects.nonNull(value)) {
                        targetField.set(target, value);
                    }
                    break;
                }
            }
        }
    }


    // ========== 以下方法使用 Hutool，支持嵌套转换 ==========

    public static <T> List<T> toBeanList(Collection<?> source, Class<T> targetClass) {
        return source.stream()
                .map(item -> BeanUtil.toBean(item, targetClass))
                .toList();
    }

    public static <T, C extends Collection<T>> C toBean(
            Collection<?> source,
            Class<T> targetClass,
            Supplier<C> collectionFactory) {
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

    // ========== 分页工具 ==========

    public static <S, T> RequestPage<T> toBean(RequestPage<S> source, Class<T> targetConditionClass) {
        if (source == null) return null;

        T convertedCondition = null;
        if (source.getCondition() != null) {
            //  使用 Hutool 支持嵌套
            convertedCondition = BeanUtil.toBean(source.getCondition(), targetConditionClass);
        }

        RequestPage<T> target = new RequestPage<>();
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setCondition(convertedCondition);
        return target;
    }

    public static <S, T> PageResultT<List<T>> toBean(
            PageResultT<List<S>> source,
            Class<T> elementTargetType) {

        if (source == null) {
            return null;
        }

        List<T> convertedRows = Collections.emptyList();
        if (source.getRows() != null && !source.getRows().isEmpty()) {
            // 修复：将结果赋值给 convertedRows
            convertedRows = toBeanList(source.getRows(), elementTargetType);
        }

        return PageResultT.<List<T>>builder()
                .pageNum(source.getPageNum())
                .pageSize(source.getPageSize())
                .total(source.getTotal())
                .rows(convertedRows)
                .build();
    }


    public static <S, T> CursorPageResult<List<T>> toBean(
            CursorPageResult<List<S>> source,
            Class<T> elementTargetType) {

        if (source == null) {
            return null;
        }

        List<T> convertedRows = Collections.emptyList();
        if (source.getRows() != null && !source.getRows().isEmpty()) {
            // 修复：将结果赋值给 convertedRows
            convertedRows = toBeanList(source.getRows(), elementTargetType);
        }

        return CursorPageResult.<List<T>>builder()
                .cursor(source.getCursor())
                .hasNext(source.getHasNext())
                .rows(convertedRows)
                .build();
    }

    public static <S, T> RequestCursorPage<T> toBean(RequestCursorPage<S> source, Class<T> targetConditionClass) {
        if (source == null) return null;

        T convertedCondition = null;
        if (source.getCondition() != null) {
            //  使用 Hutool 支持嵌套
            convertedCondition = BeanUtil.toBean(source.getCondition(), targetConditionClass);
        }

        return RequestCursorPage.<T>builder()
                .order(source.getOrder())
                .condition(convertedCondition)
                .lastId(source.getLastId())
                .pageSize(source.getPageSize())
                .build();
    }

}

