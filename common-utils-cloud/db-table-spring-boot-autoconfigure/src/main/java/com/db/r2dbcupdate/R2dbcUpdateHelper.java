package com.db.r2dbcupdate;


import com.db.dbnumber.ConstNumber;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.mylong.MyLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class R2dbcUpdateHelper {
    private final DatabaseClient databaseClient;
    private final MyLong myLong;

    /**
     * 通用动态更新，忽略 null 字段
     *
     * @param clazz   类
     * @param entity      实体对象
     * @param idFieldName ID 字段名
     * @param <T>         实体类型
     * @return Mono<Long> 更新行数
     */
    public <T> Mono<Long> updateIgnoreNull(Class<T> clazz, T entity, String idFieldName) {
        String tableName = EntityTableNameUtils.getName(clazz);
        if(!StringUtils.hasText(tableName)){
            return Mono.just(ConstNumber.LONG_ZERO);
        }
        Map<String, Object> updateFields = new LinkedHashMap<>();
        AtomicReference<Object> idValueRef = new AtomicReference<>();

        // 遍历实体字段
        ReflectionUtils.doWithFields(entity.getClass(), field -> {
            // 过滤掉 static 和 transient 字段
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                return;
            }

            field.setAccessible(true);
            try {
                Object value = field.get(entity);

                // 记录 ID 值
                if (field.getName().equals(idFieldName)) {

                    idValueRef.set(value);
                    return; // 注意：这里用 return 跳过后续逻辑，不要把 ID 放进 updateFields
                }

                // 【核心逻辑】忽略 null 值，实现动态更新
                if (value != null) {
                    updateFields.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                log.error("获取字段值失败, field: {}", field.getName(), e);
            }
        });

        if (updateFields.isEmpty() || idValueRef.get() == null) {
            return Mono.just(ConstNumber.LONG_ZERO);
        }

        //  驼峰转下划线工具方法
        Function<String, String> camelToUnderline = name -> {
            if (name == null || name.isEmpty()) return name;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                char ch = name.charAt(i);
                if (Character.isUpperCase(ch)) {
                    // 如果不是第一个字符，才加下划线
                    if (i > 0) {
                        sb.append('_');
                    }
                    sb.append(Character.toLowerCase(ch));
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        };

        // 构建动态 SQL：列名转下划线，参数仍用驼峰
        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        for (Map.Entry<String, Object> entry : updateFields.entrySet()) {
            String column = camelToUnderline.apply(entry.getKey()); // updateTime → update_time
            sql.append(column).append("=:").append(entry.getKey()).append(", ");
        }
        sql.setLength(sql.length() - 2); // 去掉最后逗号

        // WHERE 条件列名也转换
        String idColumn = camelToUnderline.apply(idFieldName);
        sql.append(" WHERE ").append(idColumn).append("=:").append(idFieldName);

        // 构建执行器
        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql.toString());

        // 绑定参数：参数名是驼峰（对应 :updateTime），值是字段值
        for (Map.Entry<String, Object> entry : updateFields.entrySet()) {
            spec = spec.bind(entry.getKey(), entry.getValue()); // bind(:updateTime, value)
        }
        Object realIdValue = idValueRef.get();
        spec = spec.bind(idFieldName, realIdValue  );

        // 执行并判断是否更新成功
        return spec.fetch()
                .rowsUpdated()
                .flatMap(rowsUpdated -> rowsUpdated > 0 ? Mono.just(myLong.longOrNull(realIdValue)) : Mono.empty());
    }
}
