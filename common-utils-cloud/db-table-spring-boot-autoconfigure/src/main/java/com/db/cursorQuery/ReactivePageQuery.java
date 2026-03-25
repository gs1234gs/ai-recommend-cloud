package com.db.cursorQuery;

import cn.hutool.core.util.StrUtil;
import com.db.constsql.SqlConst;
import com.db.page.PageUtils;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.sqlenums.LikeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;

import java.util.Collection;
import java.util.List;

/**
 * 分页查询构造器（R2DBC版）
 */
@Slf4j
public class ReactivePageQuery<T> {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final Class<T> entityClass;
    //  使用经过 validate 的 page
    private final RequestPage<T> validatedPage;
    private Criteria criteria = Criteria.empty();
    private Sort.Order order = Sort.Order.desc(SqlConst.ID);

    public ReactivePageQuery(R2dbcEntityTemplate r2dbcEntityTemplate,
                             Class<T> entityClass,
                             RequestPage<T> page) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.entityClass = entityClass;
        this.validatedPage = PageUtils.pageValidation(page,entityClass);
    }

    public static <T> ReactivePageQuery<T> of(R2dbcEntityTemplate r2dbcEntityTemplate,
                                        Class<T> entityClass,
                                        RequestPage<T> page) {
        return new ReactivePageQuery<>(r2dbcEntityTemplate, entityClass, page)
                .delFlagQuery(entityClass); // 自动触发 validate
    }

    public ReactivePageQuery<T> eq(String field, Object value){
        if (value != null) {
            this.criteria = this.criteria.and(field).is(value);
        }
        return this;
    }
    public ReactivePageQuery<T> like(String field, String value){
        return like(field, value, LikeType.CONTAINS);
    }
    public ReactivePageQuery<T> likeLeft(String field, String value){
        return like(field, value, LikeType.STARTS_WITH);
    }
    public ReactivePageQuery<T> likeRight(String field, String value){
        return like(field, value, LikeType.ENDS_WITH);
    }
    public ReactivePageQuery<T> orderByDesc(String field){
        this.order = Sort.Order.desc(field);
        return this;
    }
    public ReactivePageQuery<T> orderByAsc(String field){
        this.order = Sort.Order.asc(field);
        return this;
    }
    public Mono<Long> count(){
        return r2dbcEntityTemplate.count(Query.query( criteria), entityClass);
    }
    public Flux<T> list(){
        Long pageNum = validatedPage.getPageNum();
        Integer pageSize = validatedPage.getPageSize();
        long multiply = pageNum * pageSize;
        Query limit = Query.query(criteria)
                .sort(Sort.by(order))
                .limit(pageSize)
                .offset(multiply);
        return r2dbcEntityTemplate.select(limit, entityClass);
    }


    public Mono<PageResultT<List<T>>> page(){
        return count()
                .flatMap(total -> list()
                        .collectList()
                        .map(rows -> PageResultT.<List<T>>builder()
                                .pageNum(validatedPage.getPageNum())
                                .pageSize(validatedPage.getPageSize())
                                .total(total)
                                .rows(rows)
                                .build()));
    }

    public ReactivePageQuery<T> between(String field, Object min, Object max) {
        if (min != null && max != null) {
            this.criteria = this.criteria.and(field).between(min, max);
        } else if (min != null) {
            gte(field, min);
        } else if (max != null) {
            lte(field, max);
        }
        return this;
    }
    public ReactivePageQuery<T> gt(String field, Object value) {
        if (value != null) {
            this.criteria = this.criteria.and(field).greaterThan(value);
        }
        return this;
    }

    /**
     * 字段大于等于 value
     */
    public ReactivePageQuery<T> gte(String field, Object value) {
        if (value != null) {
            this.criteria = this.criteria.and(field).greaterThanOrEquals(value);
        }
        return this;
    }

    /**
     * 字段小于 value
     */
    public ReactivePageQuery<T> lt(String field, Object value) {
        if (value != null) {
            this.criteria = this.criteria.and(field).lessThan(value);
        }
        return this;
    }

    /**
     * 字段小于等于 value
     */
    public ReactivePageQuery<T> lte(String field, Object value) {
        if (value != null) {
            this.criteria = this.criteria.and(field).lessThanOrEquals(value);
        }
        return this;
    }

// ========== 集合查询 ==========

    /**
     * 字段值在给定集合中
     */
    public <V> ReactivePageQuery<T> in(String field, Collection<V> values) {
        if (values != null && !values.isEmpty()) {
            this.criteria = this.criteria.and(field).in(values);
        }
        return this;
    }

    /**
     * 字段值不在给定集合中
     */
    public <V> ReactivePageQuery<T> notIn(String field, Collection<V> values) {
        if (values != null && !values.isEmpty()) {
            this.criteria = this.criteria.and(field).notIn( values);
        }
        return this;
    }

// ========== 不等于 ==========

    /**
     * 字段不等于 value
     */
    public ReactivePageQuery<T> ne(String field, Object value) {
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
    public ReactivePageQuery<T> isNull(String field) {
        this.criteria = this.criteria.and(field).isNull();
        return this;
    }

    /**
     * 字段不为 null
     */
    public ReactivePageQuery<T> isNotNull(String field) {
        this.criteria = this.criteria.and(field).isNotNull();
        return this;
    }
    private ReactivePageQuery<T> like(String field, String value, LikeType type) {
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

    private ReactivePageQuery<T> delFlagQuery(Class<?> entityClass) {
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
