package com.db.cursorQuery;

import com.guanshiyun.requestpojo.RequestPage;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;


public class ReactivePageQueryFactory implements ReactiveQuery{
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public ReactivePageQueryFactory(R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    // 工厂方法，用于创建 Query 对象
    @Override
    public <T> ReactivePageQuery<T> createQuery(Class<T> entityClass, RequestPage<T> page) {
        return  ReactivePageQuery.of(r2dbcEntityTemplate, entityClass, page);
    }
}
