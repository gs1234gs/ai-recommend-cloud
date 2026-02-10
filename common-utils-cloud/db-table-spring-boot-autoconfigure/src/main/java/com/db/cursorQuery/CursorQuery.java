package com.db.cursorQuery;

import cn.hutool.core.util.StrUtil;
import com.db.constsql.SqlConst;
import com.db.page.CursorPageUtil;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.sqlenums.LikeType;
import com.guanshiyun.sqlenums.SortOrderEnum;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public class CursorQuery<T> {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final Class<T> entityClass;

    //  使用经过 validate 的 page
    private final RequestCursorPage<T> validatedPage;

    private Criteria criteria = Criteria.empty();
    private Sort.Order order = Sort.Order.desc(SqlConst.ID); // 默认排序

    public CursorQuery(R2dbcEntityTemplate r2dbcEntityTemplate,
                       Class<T> entityClass,
                       RequestCursorPage<T> page) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.entityClass = entityClass;

        //  核心改造：在构造时就进行完整校验并填充默认值
        this.validatedPage = CursorPageUtil.validate(page, entityClass);
    }

    // ============== 条件方法 ==============

    public CursorQuery<T> eq(String field, Object value) {
        if (value != null) {
            this.criteria = this.criteria.and(field).is(value);
        }
        return this;
    }

    public CursorQuery<T> like(String field, String value) {
        return like(field, value, LikeType.CONTAINS);
    }

    public CursorQuery<T> likeLeft(String field, String value) {
        return like(field, value, LikeType.STARTS_WITH);
    }

    public CursorQuery<T> likeRight(String field, String value) {
        return like(field, value, LikeType.ENDS_WITH);
    }

    private CursorQuery<T> like(String field, String value, LikeType type) {
        if (StrUtil.isBlank(value)) return this;

        String pattern = switch (type) {
            case STARTS_WITH -> value.trim() + SqlConst.PERCENT;
            case ENDS_WITH -> SqlConst.PERCENT + value.trim();
            case CONTAINS -> SqlConst.PERCENT + value.trim() + SqlConst.PERCENT;
            case EXACT -> value.trim();
        };

        this.criteria = this.criteria.and(field).like(pattern);
        return this;
    }

    public CursorQuery<T> orderByDesc(String field) {
        this.order = Sort.Order.desc(field);
        return this;
    }

    public CursorQuery<T> orderByAsc(String field) {
        this.order = Sort.Order.asc(field);
        return this;
    }

    // ============== 查询方法 ==============

    /**
     * 执行 COUNT 查询
     */
    public Mono<Long> count() {
        return r2dbcEntityTemplate.count(Query.query(criteria), entityClass);
    }

    /**
     * 执行列表查询（带游标分页）
     */
    public Flux<T> list() {
        BigInteger lastId = validatedPage.getLastId();         // 使用校验后的 lastId
        int pageSize =  Integer.sum(validatedPage.getPageSize(), 1);         // 已经是合法值
        String orderStr = validatedPage.getOrder();             //  已转为大写 ASC/DESC

        // 添加游标条件：基于 lastId 和 order 方向
        if (lastId != null && lastId.compareTo(BigInteger.ZERO) > 0) {
            criteria = SortOrderEnum.ASC.getKey().equalsIgnoreCase(orderStr)
                    ? criteria.and(SqlConst.ID).greaterThan(lastId)
                    : criteria.and(SqlConst.ID).lessThan(lastId);
        }

        Query query = Query.query(criteria)
                .sort(Sort.by(order))
                .limit(pageSize);

        return r2dbcEntityTemplate.select(query, entityClass);
    }

    /**
     * 执行分页查询，返回带 total 的 PageResultT
     */
    public Mono<PageResultT<List<T>>> page() {
        return count()
                .flatMap(total -> list()
                        .collectList()
                        .map(list -> PageResultT.<List<T>>builder()
                                .total(total)
                                .rows(list)
                                .build()
                        )
                );
    }
// ========== 范围查询 ==========

    /**
     * 字段在 [min, max] 范围内（闭区间）
     */
    public CursorQuery<T> between(String field, Object min, Object max) {
        if (min != null && max != null) {
            this.criteria = this.criteria.and(field).between(min, max);
        } else if (min != null) {
            gte(field, min);
        } else if (max != null) {
            lte(field, max);
        }
        return this;
    }

    /**
     * 字段大于 value
     */
    public CursorQuery<T> gt(String field, Object value) {
        if (value != null) {
            this.criteria = this.criteria.and(field).greaterThan(value);
        }
        return this;
    }

    /**
     * 字段大于等于 value
     */
    public CursorQuery<T> gte(String field, Object value) {
        if (value != null) {
            this.criteria = this.criteria.and(field).greaterThanOrEquals(value);
        }
        return this;
    }

    /**
     * 字段小于 value
     */
    public CursorQuery<T> lt(String field, Object value) {
        if (value != null) {
            this.criteria = this.criteria.and(field).lessThan(value);
        }
        return this;
    }

    /**
     * 字段小于等于 value
     */
    public CursorQuery<T> lte(String field, Object value) {
        if (value != null) {
            this.criteria = this.criteria.and(field).lessThanOrEquals(value);
        }
        return this;
    }

// ========== 集合查询 ==========

    /**
     * 字段值在给定集合中
     */
    public <V> CursorQuery<T> in(String field, Collection<V> values) {
        if (values != null && !values.isEmpty()) {
            this.criteria = this.criteria.and(field).in(values);
        }
        return this;
    }

    /**
     * 字段值不在给定集合中
     */
    public <V> CursorQuery<T> notIn(String field, Collection<V> values) {
        if (values != null && !values.isEmpty()) {
            this.criteria = this.criteria.and(field).notIn( values);
        }
        return this;
    }

// ========== 不等于 ==========

    /**
     * 字段不等于 value
     */
    public CursorQuery<T> ne(String field, Object value) {
        if (value != null) {
            this.criteria = this.criteria.and(field).notIn( value);
        } else {
            // null 情况下使用 isNull()
            return isNotNull(field);
        }
        return this;
    }

// ========== 空值判断 ==========

    /**
     * 字段为 null
     */
    public CursorQuery<T> isNull(String field) {
        this.criteria = this.criteria.and(field).isNull();
        return this;
    }

    /**
     * 字段不为 null
     */
    public CursorQuery<T> isNotNull(String field) {
        this.criteria = this.criteria.and(field).isNotNull();
        return this;
    }
    // ============== 静态工厂方法 ==============
    public static <T> CursorQuery<T> of(R2dbcEntityTemplate r2dbcEntityTemplate,
                                        Class<T> entityClass,
                                        RequestCursorPage<T> page) {
        return new CursorQuery<>(r2dbcEntityTemplate, entityClass, page)// 自动触发 validate
                .delFlagQuery(entityClass); // 自动触发 软删除
    }
    private CursorQuery<T> delFlagQuery(Class<?> entityClass) {
        Class<?> clazz = entityClass;

        // 递归遍历当前类及其所有父类（直到 Object）
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if ("delFlag".equals(field.getName())) {
                    // 找到 delFlag 字段 → 添加 del_flag = 0 条件
                    this.criteria = this.criteria.and("del_flag").is((short) 0);
                    return this;
                }
            }
            clazz = clazz.getSuperclass();
        }

        // 没找到 delFlag 字段 → 不加条件，安全返回
        return this;
    }

}
