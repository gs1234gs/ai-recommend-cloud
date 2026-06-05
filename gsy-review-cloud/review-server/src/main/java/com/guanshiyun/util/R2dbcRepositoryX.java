//package com.guanshiyun.util;
//
//import com.guanshiyun.requestpojo.RequestPage;
//import com.guanshiyun.responsepojo.PageResultT;
//import org.springframework.data.r2dbc.repository.Query;
//import org.springframework.data.r2dbc.repository.R2dbcRepository;
//import org.springframework.data.repository.NoRepositoryBean;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.function.Function;
//
//@NoRepositoryBean
//public interface R2dbcRepositoryX<T, ID> extends R2dbcRepository<T, ID> {
//
//
//    @Query("")
//    Mono<Void> softDeleteById(ID id);
//
//    Mono<Void> softDeleteByField(Function<T, ?> fieldFn, Object value);
//
//    //分页
//
//    SimpleR2dbcRepositoryX<T, ID> page(RequestPage<T> requestPage);
//
//
//    SimpleR2dbcRepositoryX<T, ID> is(Function<T, ?> fieldFn, Object value);
//
//
//    SimpleR2dbcRepositoryX<T, ID> not(Function<T, ?> fieldFn, Object value);
//
//
//    SimpleR2dbcRepositoryX<T, ID> in(Function<T, ?> fieldFn, Object value);
//
//
//    SimpleR2dbcRepositoryX<T, ID> notIn(Function<T, ?> fieldFn, Object value);
//
//
//    SimpleR2dbcRepositoryX<T, ID> between(Function<T, ?> fieldFn, Object value1, Object value2);
//
//
//    SimpleR2dbcRepositoryX<T, ID> notBetween(Function<T, ?> fieldFn, Object value1, Object value2);
//
//    SimpleR2dbcRepositoryX<T, ID> like(Function<T, ?> fieldFn, Object value);
//
//    SimpleR2dbcRepositoryX<T, ID> notLike(Function<T, ?> fieldFn, Object value);
//
//    SimpleR2dbcRepositoryX<T, ID> orderBy(Function<T, ?> fieldFn);
//
//    SimpleR2dbcRepositoryX<T, ID> groupBy(Function<T, ?> fieldFn);
//
//    SimpleR2dbcRepositoryX<T, ID> greaterThan(Function<T, ?> fieldFn, Object value);
//
//
//    SimpleR2dbcRepositoryX<T, ID> lessThan(Function<T, ?> fieldFn, Object value);
//
//
//    SimpleR2dbcRepositoryX<T, ID> greaterThanOrEquals(Function<T, ?> fieldFn, Object value);
//
//
//    SimpleR2dbcRepositoryX<T, ID> lessThanOrEquals(Function<T, ?> fieldFn, Object value);
//    Mono<PageResultT<List<T>>>page();
//
//    Mono<Void> softDeleteByIdIfNotNull(ID id);
//
//    Mono<Void> softDeleteByFieldIfNotNull(Function<T, ?> fieldFn, Object value);
//
//
//    SimpleR2dbcRepositoryX<T, ID> isIfNotNull(Function<T, ?> fieldFn, Object value);
//
//
//    SimpleR2dbcRepositoryX<T, ID> notIfNotNull(Function<T, ?> fieldFn, Object value);
//
//
//    SimpleR2dbcRepositoryX<T, ID> inIfNotNull(Function<T, ?> fieldFn, Object value);
//
//
//    SimpleR2dbcRepositoryX<T, ID> notInIfNotNull(Function<T, ?> fieldFn, Object value);
//
//
//    SimpleR2dbcRepositoryX<T, ID> betweenIfNotNull(Function<T, ?> fieldFn, Object value1, Object value2);
//
//    SimpleR2dbcRepositoryX<T, ID> notBetweenIfNotNull(Function<T, ?> fieldFn, Object value1, Object value2);
//
//    SimpleR2dbcRepositoryX<T, ID> likeIfNotNull(Function<T, ?> fieldFn, Object value);
//
//    SimpleR2dbcRepositoryX<T, ID> notLikeIfNotNull(Function<T, ?> fieldFn, Object value);
//
//    SimpleR2dbcRepositoryX<T, ID> orderByIfNotNull(Function<T, ?> fieldFn, String direction);
//
//    SimpleR2dbcRepositoryX<T, ID> groupByIfNotNull(Function<T, ?> fieldFn);
//
//    SimpleR2dbcRepositoryX<T, ID> greaterThanIfNotNull(Function<T, ?> fieldFn, Object value);
//
//    SimpleR2dbcRepositoryX<T, ID> lessThanIfNotNull(Function<T, ?> fieldFn, Object value);
//
//    SimpleR2dbcRepositoryX<T, ID> greaterThanOrEqualsIfNotNull(Function<T, ?> fieldFn, Object value);
//
//
//    SimpleR2dbcRepositoryX<T, ID> lessThanOrEqualsIfNotNull(Function<T, ?> fieldFn, Object value);
//
//    SimpleR2dbcRepositoryX<T, ID> isNull(Function<T, ?> fieldFn);
//
//    SimpleR2dbcRepositoryX<T, ID> isNotNull(Function<T, ?> fieldFn);
//
//    SimpleR2dbcRepositoryX<T, ID> isTrue(Function<T, ?> fieldFn);
//
//    SimpleR2dbcRepositoryX<T, ID> isFalse(Function<T, ?> fieldFn);
//
//    SimpleR2dbcRepositoryX<T, ID> orderByDesc();
//
//    SimpleR2dbcRepositoryX<T, ID> orderByAsc();
//
//}
