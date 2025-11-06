package com.db.page;


import com.db.dbnumber.ConstNumber;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.sqlenums.SortOrderEnum;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

@Slf4j
public class CursorPageUtil {

    /**
     * 对 RequestCursorPage 进行合法性校验，并填充默认值
     *
     * @param page      请求分页对象
     * @param clazz     条件对象类型（用于反射创建 condition 实例）
     * @param <T>       条件对象泛型
     * @return          校验后的 RequestCursorPage
     */
    public static <T> RequestCursorPage<T> validate(RequestCursorPage<T> page, Class<T> clazz) {
        try {
            // 创建 condition 实例（无参构造）
            T condition = createConditionInstance(clazz);

            // 如果 page 为 null，返回默认值
            if (page == null) {
                log.warn("RequestCursorPage 为空，使用默认配置");
                return RequestCursorPage.<T>builder()
                        .lastId(null)
                        .pageSize(ConstNumber.INT_TEN)  // 默认 10
                        .order(SortOrderEnum.DESC.getKey())
                        .condition(condition)
                        .build();
            }

            // 确保 condition 不为 null
            T finalCondition = page.getCondition() != null ? page.getCondition() : condition;

            // 校验 pageSize
            Integer validPageSize = pageSize(page.getPageSize());

            // 校验 order（忽略大小写）
            String order = page.getOrder();
            if (!SortOrderEnum.ASC.getKey().equalsIgnoreCase(order) && !SortOrderEnum.DESC.getKey().equalsIgnoreCase(order)) {
                log.warn("无效排序方式: {}, 使用默认 {}", order,SortOrderEnum.DESC.getKey());
                order = SortOrderEnum.DESC.getKey();
            }

            // 构建最终对象（lastId 可为 null，表示第一页）
            return RequestCursorPage.<T>builder()
                    .lastId(page.getLastId())           // lastId 允许 null
                    .pageSize(validPageSize)
                    .order(order.toUpperCase())
                    .condition(finalCondition)
                    .build();

        } catch (Exception e) {
            log.error("校验 RequestCursorPage 失败", e);
            // 失败时返回安全默认值
            return safeDefault(clazz);
        }
    }

    /**
     * 创建 condition 实例
     */
    private static <T> T createConditionInstance(Class<T> clazz) throws Exception {
        return clazz.getDeclaredConstructor().newInstance();
    }

    /**
     * 安全默认值（出错时使用）
     */
    private static <T> RequestCursorPage<T> safeDefault(Class<T> clazz) {
        try {
            T condition = createConditionInstance(clazz);
            return RequestCursorPage.<T>builder()
                    .lastId(null)
                    .pageSize(ConstNumber.INT_TEN)
                    .order(SortOrderEnum.DESC.getKey())
                    .condition(condition)
                    .build();
        } catch (Exception e2) {
            log.error("创建安全默认值失败", e2);
            throw new RuntimeException("无法初始化分页参数", e2);
        }
    }

    /**
     * 校验并获取合法 pageSize
     */
    public static Integer pageSize(Integer pageSize) {
        try {
            if (pageSize != null && pageSize > 0 && pageSize <= ConstNumber.INT_HUNDRED) { // 最大限制 100
                return pageSize;
            }
        } catch (Exception e) {
            log.error("获取 pageSize 失败", e);
        }
        log.debug("pageSize 不合法，使用默认值: {}", ConstNumber.INT_TEN);
        return ConstNumber.INT_TEN;
    }

    /**
     * 可选：校验 lastId 是否有效（非负数）
     */
    public static BigInteger validateLastId(BigInteger lastId) {
        if (lastId == null || lastId.compareTo(BigInteger.ZERO) < 0) {
            return null; // 表示第一页
        }
        return lastId;
    }
}
