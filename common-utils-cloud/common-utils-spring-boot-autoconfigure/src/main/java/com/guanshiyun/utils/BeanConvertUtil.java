package com.guanshiyun.utils;

import cn.hutool.core.bean.BeanUtil;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.CursorPageResult;
import com.guanshiyun.responsepojo.PageResultT;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BeanConvertUtil {

    /**
     * 将 source 转换成 targetClass 类型的 Bean
     */
    public static <T> T toBean(Object source, Class<T> targetClass) {
        if (Objects.isNull(source)) return BeanUtil.toBean(source, targetClass);
        try {
            return BeanUtil.toBean(source, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Bean conversion failed", e);
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

