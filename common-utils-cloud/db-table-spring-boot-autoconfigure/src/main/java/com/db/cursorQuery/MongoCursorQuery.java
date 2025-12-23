package com.db.cursorQuery;

import cn.hutool.core.util.StrUtil;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.sqlenums.LikeType;
import com.guanshiyun.sqlenums.SortOrderEnum;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public class MongoCursorQuery<T> {

    private final ReactiveMongoTemplate mongoTemplate;
    private final Class<T> entityClass;
    private final RequestCursorPage<T> validatedPage;

    private Criteria criteria = new Criteria();
    private Sort.Order order = Sort.Order.desc("_id"); // 默认按 _id 降序

    public MongoCursorQuery(ReactiveMongoTemplate mongoTemplate,
                            Class<T> entityClass,
                            RequestCursorPage<T> page) {
        this.mongoTemplate = mongoTemplate;
        this.entityClass = entityClass;
        this.validatedPage = page; // 可以加 validate
    }

    // ================= 条件方法 =================

    public MongoCursorQuery<T> eq(String field, Object value) {
        if (value != null) {
            criteria = criteria.and(field).is(value);
        }
        return this;
    }

    public MongoCursorQuery<T> ne(String field, Object value) {
        if (value != null) {
            criteria = criteria.and(field).ne(value);
        }
        return this;
    }

    public MongoCursorQuery<T> like(String field, String value) {
        return like(field, value, LikeType.CONTAINS);
    }

    public MongoCursorQuery<T> likeLeft(String field, String value) {
        return like(field, value, LikeType.STARTS_WITH);
    }

    public MongoCursorQuery<T> likeRight(String field, String value) {
        return like(field, value, LikeType.ENDS_WITH);
    }

    private MongoCursorQuery<T> like(String field, String value, LikeType type) {
        if (StrUtil.isNotBlank(value)) {
            String pattern = switch (type) {
                case STARTS_WITH -> "^" + value.trim();
                case ENDS_WITH -> value.trim() + "$";
                case CONTAINS -> value.trim();
                case EXACT -> "^" + value.trim() + "$";
            };
            criteria = criteria.and(field).regex(pattern);
        }
        return this;
    }

    public MongoCursorQuery<T> gt(String field, Object value) {
        if (value != null) criteria = criteria.and(field).gt(value);
        return this;
    }

    public MongoCursorQuery<T> gte(String field, Object value) {
        if (value != null) criteria = criteria.and(field).gte(value);
        return this;
    }

    public MongoCursorQuery<T> lt(String field, Object value) {
        if (value != null) criteria = criteria.and(field).lt(value);
        return this;
    }

    public MongoCursorQuery<T> lte(String field, Object value) {
        if (value != null) criteria = criteria.and(field).lte(value);
        return this;
    }

    public MongoCursorQuery<T> between(String field, Object min, Object max) {
        if (min != null && max != null) {
            criteria = criteria.and(field).gte(min).lte(max);
        } else if (min != null) gte(field, min);
        else if (max != null) lte(field, max);
        return this;
    }

    public <V> MongoCursorQuery<T> in(String field, Collection<V> values) {
        if (values != null && !values.isEmpty()) criteria = criteria.and(field).in(values);
        return this;
    }

    public <V> MongoCursorQuery<T> notIn(String field, Collection<V> values) {
        if (values != null && !values.isEmpty()) criteria = criteria.and(field).nin(values);
        return this;
    }

    public MongoCursorQuery<T> isNull(String field) {
        criteria = criteria.and(field).is(null);
        return this;
    }

    public MongoCursorQuery<T> isNotNull(String field) {
        criteria = criteria.and(field).ne(null);
        return this;
    }

    public MongoCursorQuery<T> orderByDesc(String field) {
        order = Sort.Order.desc(field);
        return this;
    }

    public MongoCursorQuery<T> orderByAsc(String field) {
        order = Sort.Order.asc(field);
        return this;
    }

    // ================= 查询方法 =================

    public Mono<Long> count() {
        return mongoTemplate.count(Query.query(criteria), entityClass);
    }

    public Flux<T> list() {
        BigInteger lastId = validatedPage.getLastId();
        int pageSize = validatedPage.getPageSize();

        Criteria cursorCriteria = criteria;
        if (lastId != null) {
            // 游标分页
            if (SortOrderEnum.ASC.getKey().equalsIgnoreCase(validatedPage.getOrder())) {
                cursorCriteria = cursorCriteria.and("_id").gt(lastId);
            } else {
                cursorCriteria = cursorCriteria.and("_id").lt(lastId);
            }
        }

        Query query = new Query(cursorCriteria)
                .with(Sort.by(order))
                .limit(pageSize);

        return mongoTemplate.find(query, entityClass);
    }

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

    // ================= 静态工厂 =================
    public static <T> MongoCursorQuery<T> of(ReactiveMongoTemplate mongoTemplate,
                                             Class<T> entityClass,
                                             RequestCursorPage<T> page) {
        return new MongoCursorQuery<>(mongoTemplate, entityClass, page);
    }
}
