//package com.db.databaseclientx;
//
//import com.guanshiyun.requestpojo.RequestPage;
//import com.guanshiyun.responsepojo.PageResultT;
//import org.springframework.data.r2dbc.repository.R2dbcRepository;
//import org.springframework.lang.NonNull;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.Collection;
//import java.util.List;
//
//public interface  R2dbcRepositoryX<T, R extends Number> extends R2dbcRepository<T, R> {
//
//    Mono<Long> updateIgnoreNull(T entity);
//
//
//    R2dbcRepositoryX<T, R> eq(String field, Object value);
//    R2dbcRepositoryX<T, R> like(String field, String value);
//    R2dbcRepositoryX<T, R> likeLeft(String field, String value);
//    R2dbcRepositoryX<T, R> likeRight(String field, String value);
//    R2dbcRepositoryX<T, R> orderByDesc(String field);
//    R2dbcRepositoryX<T, R> orderByAsc(String field);
//    @NonNull
//    Mono<Long> count();
//    Flux<T> list();
//    Mono<PageResultT<List<T>>> page();
//
//    R2dbcRepositoryX<T, R> between(String field, Object min, Object max);
//    R2dbcRepositoryX<T, R> gt(String field, Object value) ;
//
//    /**
//     * 字段大于等于 value
//     */
//
//    R2dbcRepositoryX<T, R> gte(String field, Object value) ;
//
//    /**
//     * 字段小于 value
//     */
//    R2dbcRepositoryX<T, R> lt(String field, Object value) ;
//
//    /**
//     * 字段小于等于 value
//     */
//    R2dbcRepositoryX<T, R> lte(String field, Object value) ;
//
//// ========== 集合查询 ==========
//
//    /**
//     * 字段值在给定集合中
//     */
//    <V> R2dbcRepositoryX<T, R> in(String field, Collection<V> values) ;
//
//    /**
//     * 字段值不在给定集合中
//     */
//    <V> R2dbcRepositoryX<T, R> notIn(String field, Collection<V> values) ;
//
//// ========== 不等于 ==========
//
//    /**
//     * 字段不等于 value
//     */
//    R2dbcRepositoryX<T, R> ne(String field, Object value) ;
//
//// ========== 空值判断 ==========
//
//    /**
//     * 字段为 null
//     */
//    R2dbcRepositoryX<T, R> isNull(String field);
//    /**
//     * 字段不为 null
//     */
//    R2dbcRepositoryX<T, R> isNotNull(String field) ;
//
//    R2dbcRepositoryX<T, R> page(RequestPage<T> validatedPage);
//}
