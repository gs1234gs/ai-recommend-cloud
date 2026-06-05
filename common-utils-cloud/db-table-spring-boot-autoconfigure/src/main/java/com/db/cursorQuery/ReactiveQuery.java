package com.db.cursorQuery;

import com.guanshiyun.requestpojo.RequestPage;

public interface ReactiveQuery {
    <T> ReactivePageQuery<T> createQuery(Class<T> entityClass, RequestPage<T> page);
}
