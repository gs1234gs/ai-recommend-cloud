//package com.guanshiyun.util;
//
//import com.guanshiyun.requestpojo.RequestPage;
//import com.guanshiyun.responsepojo.PageResultT;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
//import org.springframework.data.relational.core.query.Criteria;
//import org.springframework.data.relational.core.query.Query;
//import org.springframework.data.relational.core.query.Update;
//import org.springframework.r2dbc.core.DatabaseClient;
//import org.springframework.stereotype.Repository;
//import reactor.core.publisher.Mono;
//
//import java.lang.reflect.Field;
//import java.util.List;
//import java.util.Objects;
//import java.util.function.Function;
//
//@Repository
//@Slf4j
//public  abstract   class SimpleR2dbcRepositoryX<T, ID> implements R2dbcRepositoryX<T, ID> {
//    private final R2dbcEntityTemplate r2dbcEntityTemplate;
//    private final DatabaseClient databaseClient;
//    private final Class<T> domainType;
//    private final String DEL_FLAG = "delFlag";
//    private Criteria criteria = Criteria.empty();
//    private Long limit;
//    private Integer offset;
//    private Long pageNum;
//    private Integer pageSize;
//    private String orderBy = null;
//    private String groupBy = null;
//    private Integer sort = null;
//
//    public SimpleR2dbcRepositoryX(R2dbcEntityTemplate r2dbcEntityTemplate, DatabaseClient databaseClient, Class<T> domainType) {
//        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
//        this.databaseClient = databaseClient;
//        this.domainType = domainType;
//    }
//
//    @SneakyThrows
//    @Override
//   public Mono<Void> softDeleteById(ID id){
//        Field field = domainType.getDeclaredField(DEL_FLAG);
//        field.setAccessible(true);
//        String idColumnName = ColumnNameExtractor.extractIdColumnName(domainType);
//        Update update = Update.update(DEL_FLAG, 1);
//        Criteria criteria = Criteria.empty().and(idColumnName).is(id);
//        Query query = Query.query(criteria);
//        return r2dbcEntityTemplate
//                .update(query, update, domainType)
//                .then()
//                .onErrorResume(e->{
//                    log.info("软删除失败", e);
//                    return Mono.error(e);
//                });
//
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> page(RequestPage<T> requestPage) {
//         pageNum = requestPage.getPageNum();
//        limit = (pageNum - 1L);
//       pageSize = requestPage.getPageSize();
//        offset = limit.intValue() * pageSize;
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> is(Function<T, ?> fieldFn, Object value) {
//        String columnName = ColumnNameExtractor.extract(fieldFn);
//        this.criteria = this.criteria.and(columnName).is(value);
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> not(Function<T, ?> fieldFn, Object value) {
//        String columnName = ColumnNameExtractor.extract(fieldFn);
//        this.criteria = this.criteria.and(columnName).not(value);
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> in(Function<T, ?> fieldFn, Object value) {
//        String columnName = ColumnNameExtractor.extract(fieldFn);
//        this.criteria = this.criteria.and(columnName).in(value);
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> notIn(Function<T, ?> fieldFn, Object value) {
//        String columnName = ColumnNameExtractor.extract(fieldFn);
//        this.criteria = this.criteria.and(columnName).notIn(value);
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> between(Function<T, ?> fieldFn, Object value1, Object value2) {
//        String columnName = ColumnNameExtractor.extract(fieldFn);
//        this.criteria = this.criteria.and(columnName).between(value1, value2);
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> notBetween(Function<T, ?> fieldFn, Object value1, Object value2) {
//        String columnName = ColumnNameExtractor.extract(fieldFn);
//        this.criteria = this.criteria.and(columnName).notBetween(value1, value2);
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> like(Function<T, ?> fieldFn, Object value) {
//        String columnName = ColumnNameExtractor.extract(fieldFn);
//        this.criteria = this.criteria.and(columnName).like(value);
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> notLike(Function<T, ?> fieldFn, Object value) {
//        String columnName = ColumnNameExtractor.extract(fieldFn);
//        this.criteria = this.criteria.and(columnName).notLike(value);
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> orderBy(Function<T, ?> fieldFn) {
//        String columnName = ColumnNameExtractor.extract(fieldFn);
//        this.orderBy = columnName;
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> groupBy(Function<T, ?> fieldFn) {
//        this.groupBy = ColumnNameExtractor.extract(fieldFn);
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> greaterThan(Function<T, ?> fieldFn, Object value) {
//        this.criteria = this.criteria.and(ColumnNameExtractor.extract(fieldFn)).greaterThan(value);
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> lessThan(Function<T, ?> fieldFn, Object value) {
//        this.criteria = this.criteria.and(ColumnNameExtractor.extract(fieldFn)).lessThan(value);
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> greaterThanOrEquals(Function<T, ?> fieldFn, Object value) {
//        this.criteria = this.criteria.and(ColumnNameExtractor.extract(fieldFn)).greaterThanOrEquals(value);
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> lessThanOrEquals(Function<T, ?> fieldFn, Object value) {
//        this.criteria = this.criteria.and(ColumnNameExtractor.extract(fieldFn)).lessThanOrEquals(value);
//        return this;
//    }
//
//    @Override
//    public Mono<Void> softDeleteByIdIfNotNull(ID id) {
//        if(Objects.nonNull(id)){
//            return softDeleteById(id);
//        }
//        return Mono.error(new Throwable("ID 为空"));
//    }
//
//    @Override
//    public Mono<Void> softDeleteByFieldIfNotNull(Function<T, ?> fieldFn, Object value) {
//        if(Objects.nonNull(value)){
//            return softDeleteByField(fieldFn, value);
//        }
//        return Mono.error(new Throwable("值为空"));
//    }
//
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> isIfNotNull(Function<T, ?> fieldFn, Object value) {
//        if(Objects.nonNull(value)){
//            return is(fieldFn, value);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> notIfNotNull(Function<T, ?> fieldFn, Object value) {
//        if(Objects.nonNull(value)){
//            return not(fieldFn, value);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> inIfNotNull(Function<T, ?> fieldFn, Object value) {
//        if(Objects.nonNull(value)){
//            return in(fieldFn, value);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> notInIfNotNull(Function<T, ?> fieldFn, Object value) {
//        if(Objects.nonNull(value)){
//            return notIn(fieldFn, value);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> betweenIfNotNull(Function<T, ?> fieldFn, Object value1, Object value2) {
//        if(Objects.nonNull(value1) && Objects.nonNull(value2)){
//            return between(fieldFn, value1, value2);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> notBetweenIfNotNull(Function<T, ?> fieldFn, Object value1, Object value2) {
//        if(Objects.nonNull(value1) && Objects.nonNull(value2)){
//            return notBetween(fieldFn, value1, value2);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> likeIfNotNull(Function<T, ?> fieldFn, Object value) {
//        if(Objects.nonNull(value)){
//            return like(fieldFn, value);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> notLikeIfNotNull(Function<T, ?> fieldFn, Object value) {
//        if(Objects.nonNull(value)){
//            return notLike(fieldFn, value);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> orderByIfNotNull(Function<T, ?> fieldFn, String direction) {
//        if(Objects.nonNull(direction)){
//            return orderBy(fieldFn);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> groupByIfNotNull(Function<T, ?> fieldFn) {
//        if(Objects.nonNull(fieldFn)){
//            return groupBy(fieldFn);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> greaterThanIfNotNull(Function<T, ?> fieldFn, Object value) {
//        if(Objects.nonNull(value)){
//            return greaterThan(fieldFn, value);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> lessThanIfNotNull(Function<T, ?> fieldFn, Object value) {
//        if(Objects.nonNull(value)){
//            return lessThan(fieldFn, value);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> greaterThanOrEqualsIfNotNull(Function<T, ?> fieldFn, Object value) {
//        if(Objects.nonNull(value)){
//            return greaterThanOrEquals(fieldFn, value);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> lessThanOrEqualsIfNotNull(Function<T, ?> fieldFn, Object value) {
//        if(Objects.nonNull(value)){
//            return lessThanOrEquals(fieldFn, value);
//        }
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> isNull(Function<T, ?> fieldFn) {
//       this.criteria.and(ColumnNameExtractor.extract(fieldFn)).isNull();
//        return this;
//    }
////
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> isNotNull(Function<T, ?> fieldFn) {
//       this.criteria.and(ColumnNameExtractor.extract(fieldFn)).isNotNull();
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> isTrue(Function<T, ?> fieldFn) {
//       this.criteria.and(ColumnNameExtractor.extract(fieldFn)).isTrue();
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> isFalse(Function<T, ?> fieldFn) {
//       this.criteria.and(ColumnNameExtractor.extract(fieldFn)).isFalse();
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> orderByDesc() {
//        sort = 0;
//        return this;
//    }
//
//    @Override
//    public SimpleR2dbcRepositoryX<T, ID> orderByAsc() {
//        sort = 1;
//        return this;
//    }
//
//    @Override
//    public Mono<PageResultT<List<T>>>page(){
//        Query query = Query.query(this.criteria).limit(limit.intValue()).offset(offset);
//        if(sort ==0 && this.orderBy != null){
//            query = query.sort(Sort.by(Sort.Order.desc(this.orderBy)));
//        }
//        if(sort ==1 && this.orderBy != null){
//            query = query.sort(Sort.by(Sort.Order.asc(this.orderBy)));
//        }
//        if(this.groupBy != null){
//          query =  query.sort(Sort.by(Sort.Order.by(this.groupBy)));
//        }
//        return r2dbcEntityTemplate.select(query,domainType)
//                .collectList()
//                .zipWith(r2dbcEntityTemplate.count(Query.query(this.criteria), domainType))
//                .map(tuple->{
//                    List<T> t1 = tuple.getT1();
//                    Long t2 = tuple.getT2();
//                    return PageResultT.<List<T>>builder()
//                            .total(t2)
//                            .rows(t1)
//                            .pageNum(pageNum)
//                            .pageSize(pageSize)
//                            .build();
//                });
//    }
//
//
//    /**
//     * 通用逻辑删除：支持通过方法引用指定要匹配的字段
//     * 示例：softDeleteByField(User::getId, 100L)
//     *       会自动生成：UPDATE user SET is_deleted = 1 WHERE id = 100
//     */
//    @Override
//    public Mono<Void> softDeleteByField(Function<T, ?> fieldFn, Object value) {
//        String tableName =ColumnNameExtractor. extractTableName(domainType);
//
//        //  调用工具类，自动把 User::getId 变成 "id" 或 "user_id"
//        String columnName = ColumnNameExtractor.extract(fieldFn);
//        Update update = Update.update(DEL_FLAG, 1);
//        Criteria criteria = Criteria.empty().and(columnName).is(value);
//        Query query = Query.query(criteria);
//        return r2dbcEntityTemplate
//                .update(query, update, domainType)
//                .then()
//                .onErrorResume(e->{
//                    log.info("软删除失败", e);
//                    return Mono.error(e);
//                });
//    }
//}
