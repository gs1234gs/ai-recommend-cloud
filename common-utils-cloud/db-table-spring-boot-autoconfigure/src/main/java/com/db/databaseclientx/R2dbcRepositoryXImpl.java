//package com.db.databaseclientx;
//
//import com.db.constsql.SqlConst;
//import com.db.dbnumber.ConstNumber;
//import com.db.tablename.EntityTableNameUtils;
//import com.guanshiyun.mylong.MyLong;
//import com.guanshiyun.requestpojo.RequestPage;
//import com.guanshiyun.responsepojo.PageResultT;
//import com.guanshiyun.sqlenums.LikeType;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
//import org.springframework.data.relational.core.query.Criteria;
//import org.springframework.data.relational.core.query.Query;
//import org.springframework.lang.NonNull;
//import org.springframework.r2dbc.core.DatabaseClient;
//import org.springframework.stereotype.Repository;
//import org.springframework.util.StringUtils;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.Collection;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//@Slf4j
//@Repository
//public abstract class R2dbcRepositoryXImpl<T, R extends Number> implements R2dbcRepositoryX<T, R> {
//    private final DatabaseClient databaseClient;
//    private final R2dbcEntityTemplate r2dbcEntityTemplate;
//    private final MyLong myLong;
//    // 缓存提取出来的泛型 T 的实际 Class
//    protected final Class<T> entityClass;
//    // 缓存自动推导的表名
//    protected final String tableName;// 手动添加显式构造函数，用于在初始化时提取泛型和表名
//
//
//    private Criteria criteria = Criteria.empty();
//    private Sort.Order order = Sort.Order.desc(SqlConst.ID);
//    private  RequestPage<T> validatedPage;
//    // 手动添加显式构造函数，用于在初始化时提取泛型和表名
//    public R2dbcRepositoryXImpl(DatabaseClient databaseClient, MyLong myLong, R2dbcEntityTemplate r2dbcEntityTemplate) {
//        this.entityClass = resolveEntityClass();
//        this.tableName = EntityTableNameUtils.getName(entityClass);
//        this.databaseClient = databaseClient;
//        this.myLong = myLong;
//        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
//    }
//    /**
//     * 通用动态更新，忽略 null 字段
//     *
//     * @param entity      实体对象
//     * @return Mono<Long> 更新行数
//     */
//    @Override
//
//    public  Mono<Long> updateIgnoreNull(T entity) {
//        Map<String, Object> updateFields = new LinkedHashMap<>();
//        Object idValue = null;
//        String idFieldName = null;
//
//        // 遍历实体字段
//        Field[] fields = entity.getClass().getDeclaredFields();
//        for (Field field : fields) {
//            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
//                continue;
//            }
//            field.setAccessible(true);
//            try {
//                Object value = field.get(entity);
//                if (field.isAnnotationPresent(Id.class)) {
//                    idValue = value;
//                    idFieldName = field.getName();
//                    if (idValue == null) {
//                        return Mono.error(new IllegalArgumentException("更新失败：实体主键不能为空！"));
//                    }
//                    continue; // 跳过主键字段，不将其放入 SET 语句中
//                }
//                if (value != null) {
//                    updateFields.put(field.getName(), value);
//                }
//            } catch (IllegalAccessException e) {
//                log.error("获取字段值失败", e);
//            }
//        }
//
//        if (updateFields.isEmpty() || idValue == null) {
//            return Mono.just(ConstNumber.LONG_ZERO);
//        }
//
//        //  驼峰转下划线工具方法
//        Function<String, String> camelToUnderline = (name) -> {
//            if (name == null || name.isEmpty()) return name;
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < name.length(); i++) {
//                char ch = name.charAt(i);
//                if (Character.isUpperCase(ch)) {
//                    sb.append('_').append(Character.toLowerCase(ch));
//                } else {
//                    sb.append(ch);
//                }
//            }
//            return sb.toString();
//        };
//
//        // 构建动态 SQL：列名转下划线，参数仍用驼峰
//        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
//        for (Map.Entry<String, Object> entry : updateFields.entrySet()) {
//            String column = camelToUnderline.apply(entry.getKey()); // updateTime → update_time
//            sql.append(column).append("=:").append(entry.getKey()).append(", ");
//        }
//        sql.setLength(sql.length() - 2); // 去掉最后逗号
//
//        // WHERE 条件列名也转换
//        String idColumn = camelToUnderline.apply(idFieldName);
//        sql.append(" WHERE ").append(idColumn).append("=:").append(idFieldName);
//
//        // 构建执行器
//        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql.toString());
//
//        // 绑定参数：参数名是驼峰（对应 :updateTime），值是字段值
//        for (Map.Entry<String, Object> entry : updateFields.entrySet()) {
//            spec = spec.bind(entry.getKey(), entry.getValue()); // bind(:updateTime, value)
//        }
//        spec = spec.bind(idFieldName, idValue);
//
////        return spec.fetch().rowsUpdated().map(Long::valueOf);
//        // 执行并判断是否更新成功
//        final Object idValueTemp = idValue;
//        return spec.fetch()
//                .rowsUpdated()
//                .flatMap(rowsUpdated -> rowsUpdated > 0 ? Mono.just(myLong.longOrNull(idValueTemp)) : Mono.empty());
//    }
//
//    /**
//     * 核心方法：通过反射获取父类泛型签名中的实际类型 T
//     */
//    @SuppressWarnings("unchecked")
//    private Class<T> resolveEntityClass() {
//        Type genericSuperclass = this.getClass().getGenericSuperclass();
//        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
//            // 提取泛型参数数组，索引 0 对应的是 T
//            // 修改点：加上  获取数组中的第一个元素
//            return (Class<T>) parameterizedType.getActualTypeArguments()[0]; // 获取数组中的第一个元素
//        } else {
//            throw new IllegalStateException("无法获取泛型 T 的实际类型，请确保子类继承时指定了具体类型！");
//        }
//    }
//
//    @Override
//    public R2dbcRepositoryX<T, R> eq(String field, Object value){
//        if (value != null) {
//            this.criteria = this.criteria.and(field).is(value);
//        }
//        return this;
//    }
//    @Override
//    public R2dbcRepositoryX<T, R> like(String field, String value){
//        return like(field, value, LikeType.CONTAINS);
//    }
//    @Override
//    public R2dbcRepositoryX<T, R> likeLeft(String field, String value){
//        return like(field, value, LikeType.STARTS_WITH);
//    }
//    @Override
//    public R2dbcRepositoryX<T, R> likeRight(String field, String value){
//        return like(field, value, LikeType.ENDS_WITH);
//    }
//    @Override
//    public R2dbcRepositoryX<T, R> orderByDesc(String field){
//        this.order = Sort.Order.desc(field);
//        return this;
//    }
//    @Override
//    public R2dbcRepositoryX<T, R> orderByAsc(String field){
//        this.order = Sort.Order.asc(field);
//        return this;
//    }
//    @Override
//    @NonNull
//    public Mono<Long> count(){
//        return r2dbcEntityTemplate.count(Query.query( criteria), entityClass);
//    }
//    @Override
//    @NonNull
//    public Flux<T> list(){
//        Long pageNum = validatedPage.getPageNum();
//        Integer pageSize = validatedPage.getPageSize();
//        long offset = (pageNum - 1) * pageSize;
//        Query limit = Query.query(criteria)
//                .sort(Sort.by(order))
//                .limit(pageSize)
//                .offset(offset);
//        return r2dbcEntityTemplate.select(limit, entityClass);
//    }
//
//    @Override
//    @NonNull
//    public Mono<PageResultT<List<T>>> page(){
//        return count()
//                .flatMap(total -> list()
//                        .collectList()
//                        .map(rows -> PageResultT.<List<T>>builder()
//                                .pageNum(validatedPage.getPageNum())
//                                .pageSize(validatedPage.getPageSize())
//                                .total(total)
//                                .rows(rows)
//                                .build()));
//    }
//    @Override
//    public R2dbcRepositoryX<T, R> between(String field, Object min, Object max) {
//        if (min != null && max != null) {
//            this.criteria = this.criteria.and(field).between(min, max);
//        } else if (min != null) {
//            gte(field, min);
//        } else if (max != null) {
//            lte(field, max);
//        }
//        return this;
//    }
//    @Override
//    public R2dbcRepositoryX<T, R> gt(String field, Object value) {
//        if (value != null) {
//            this.criteria = this.criteria.and(field).greaterThan(value);
//        }
//        return this;
//    }
//
//
//    /**
//     * 字段大于等于 value
//     */
//    @Override
//    public R2dbcRepositoryX<T, R> gte(String field, Object value) {
//        if (value != null) {
//            this.criteria = this.criteria.and(field).greaterThanOrEquals(value);
//        }
//        return this;
//    }
//
//    /**
//     * 字段小于 value
//     */
//    @Override
//    public R2dbcRepositoryX<T, R> lt(String field, Object value) {
//        if (value != null) {
//            this.criteria = this.criteria.and(field).lessThan(value);
//        }
//        return this;
//    }
//
//    /**
//     * 字段小于等于 value
//     */
//    @Override
//    public R2dbcRepositoryX<T, R> lte(String field, Object value) {
//        if (value != null) {
//            this.criteria = this.criteria.and(field).lessThanOrEquals(value);
//        }
//        return this;
//    }
//
//// ========== 集合查询 ==========
//
//    /**
//     * 字段值在给定集合中
//     */
//    @Override
//    public <V> R2dbcRepositoryX<T, R> in(String field, Collection<V> values) {
//        if (values != null && !values.isEmpty()) {
//            this.criteria = this.criteria.and(field).in(values);
//        }
//        return this;
//    }
//
//    /**
//     * 字段值不在给定集合中
//     */
//    @Override
//    public <V> R2dbcRepositoryX<T, R> notIn(String field, Collection<V> values) {
//        if (values != null && !values.isEmpty()) {
//            this.criteria = this.criteria.and(field).notIn( values);
//        }
//        return this;
//    }
//
//// ========== 不等于 ==========
//
//    /**
//     * 字段不等于 value
//     */
//    @Override
//    public R2dbcRepositoryX<T, R> ne(String field, Object value) {
//        if (value != null) {
//            this.criteria = this.criteria.and(field).notIn( value);
//        } else {
//            // null 情况下使用 isNull()
//            return isNotNull(field);
//        }
//        return this;
//    }
//
//// ========== 空值判断 ==========
//
//    /**
//     * 字段为 null
//     */
//    @Override
//    public R2dbcRepositoryX<T, R> isNull(String field) {
//        this.criteria = this.criteria.and(field).isNull();
//        return this;
//    }
//
//    /**
//     * 字段不为 null
//     */
//    @Override
//    public R2dbcRepositoryX<T, R> isNotNull(String field) {
//        this.criteria = this.criteria.and(field).isNotNull();
//        return this;
//    }
//    private R2dbcRepositoryX<T, R> like(String field, String value, LikeType type) {
//        if (!StringUtils.hasText(value)) return this;
//
//        String pattern = switch (type) {
//            case STARTS_WITH -> value.trim() + SqlConst.PERCENT;
//            case ENDS_WITH -> SqlConst.PERCENT + value.trim();
//            case CONTAINS -> SqlConst.PERCENT + value.trim() + SqlConst.PERCENT;
//            case EXACT -> value.trim();
//        };
//
//        this.criteria = this.criteria.and(field).like(pattern);
//        return this;
//    }
//
//    private R2dbcRepositoryX<T, R> delFlagQuery(Class<?> entityClass) {
//        Class<?> clazz = entityClass;
//
//        // 递归遍历当前类及其所有父类（直到 Object）
//        while (clazz != null && clazz != Object.class) {
//            for (Field field : clazz.getDeclaredFields()) {
//                if ("delFlag".equals(field.getName())) {
//                    // 找到 delFlag 字段 → 添加 del_flag = 0 条件
//                    this.criteria = this.criteria.and("del_flag").is((short) 0);
//                    return this;
//                }
//            }
//            clazz = clazz.getSuperclass();
//        }
//
//        // 没找到 delFlag 字段 → 不加条件，安全返回
//        return this;
//    }
//    @Override
//   public R2dbcRepositoryX<T, R> page(RequestPage<T> validatedPage){
//        this.validatedPage = validatedPage;
//        return this;
//    }
//}
