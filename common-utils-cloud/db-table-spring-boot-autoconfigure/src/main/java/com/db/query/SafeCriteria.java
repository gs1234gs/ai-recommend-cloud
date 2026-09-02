package com.db.query;

import com.db.constsql.SqlConst;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * R2DBC Criteria 安全封装工具类
 * 自动判断空值/空字符串，避免生成无效条件
 */
@NoArgsConstructor
public class SafeCriteria {

    private Criteria criteria = Criteria.empty();

    public static SafeCriteria safeCriteria() {
        return new SafeCriteria();
    }

    public Criteria criteria() {
        return criteria;
    }

    public SafeCriteria likeIfNotEmpty(String column, String value) {
        if (StringUtils.hasText(value)) {
            criteria = criteria.and(column).like(SqlConst.PERCENT + value + SqlConst.PERCENT);
        }
        return this;
    }

    public SafeCriteria like(String column, String value) {
        criteria = criteria.and(column).like(SqlConst.PERCENT + value + SqlConst.PERCENT);
        return this;
    }

    public SafeCriteria eqIfNotNull(String column, Object value) {
        if (Objects.nonNull(value) && !StringUtils.hasText(value.toString())) {
            criteria = criteria.and(column).is(value);
        }
        return this;
    }

    public SafeCriteria eq(String column, Object value) {
        criteria = criteria.and(column).is(value);
        return this;
    }

    public SafeCriteria inIfNotEmpty(String column, Collection<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            criteria = criteria.and(column).in(values);
        }
        return this;
    }

    public SafeCriteria in(String column, Collection<?> values) {
        criteria = criteria.and(column).in(values);
        return this;
    }

    public SafeCriteria gtIfNotNull(String column, Comparable<?> value) {
        if (Objects.nonNull(value)) {
            criteria = criteria.and(column).greaterThan(value);
        }
        return this;
    }

    public SafeCriteria geIfNotNull(String column, Comparable<?> value) {
        if (Objects.nonNull(value)) {
            criteria = criteria.and(column).greaterThanOrEquals(value);
        }
        return this;
    }

    public SafeCriteria ge(String column, Comparable<?> value) {
        criteria = criteria.and(column).greaterThanOrEquals(value);
        return this;
    }

    public SafeCriteria gt(String column, Comparable<?> value) {
        criteria = criteria.and(column).greaterThan(value);
        return this;
    }

    public SafeCriteria ltIfNotNull(String column, Comparable<?> value) {
        if (Objects.nonNull(value)) {
            criteria = criteria.and(column).lessThan(value);
        }
        return this;
    }

    public SafeCriteria leIfNotNull(String column, Comparable<?> value) {
        if (Objects.nonNull(value)) {
            criteria = criteria.and(column).lessThanOrEquals(value);
        }
        return this;
    }
    public SafeCriteria le(String column, Comparable<?> value) {
        if (Objects.nonNull(value)) {
            criteria = criteria.and(column).lessThanOrEquals(value);
        }
        return this;
    }

    public SafeCriteria lt(String column, Comparable<?> value) {
        criteria = criteria.and(column).lessThan(value);
        return this;
    }


    public SafeCriteria betweenIfNotNull(String column, Comparable<?> start, Comparable<?> end) {
        if (Objects.nonNull(start) && Objects.nonNull(end)) {
            criteria = criteria.and(column).between(start, end);
        }
        return this;
    }

    public SafeCriteria between(String column, Comparable<?> start, Comparable<?> end) {
        criteria = criteria.and(column).between(start, end);
        return this;
    }

    public SafeCriteria and(Criteria base, Criteria... criteriaList) {
        criteria = Objects.isNull(base) ? Criteria.empty() : base;
        if (Objects.nonNull(criteriaList)) {
            for (Criteria c : criteriaList) {
                if (Objects.nonNull(c) && !c.isEmpty()) {
                    criteria = criteria.and(c);
                }
            }
        }
        return this;
    }

    public SafeCriteria or(Criteria base, Criteria... criteriaList) {
        criteria = Objects.isNull(base) ? Criteria.empty() : base;
        if (criteriaList != null) {
            for (Criteria c : criteriaList) {
                if (Objects.nonNull(c) && !c.isEmpty()) {
                    criteria = criteria.or(c);
                }
            }
        }
        return this;
    }
}