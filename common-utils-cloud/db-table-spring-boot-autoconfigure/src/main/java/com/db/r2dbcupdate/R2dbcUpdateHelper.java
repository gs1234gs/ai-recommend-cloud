package com.db.r2dbcupdate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class R2dbcUpdateHelper {
    private final DatabaseClient databaseClient;

    /**
     * 通用动态更新，忽略 null 字段
     *
     * @param tableName   表名
     * @param entity      实体对象
     * @param idFieldName ID 字段名
     * @param <T>         实体类型
     * @return Mono<BigInteger> 更新行数
     */
    public <T> Mono<BigInteger> updateIgnoreNull(String tableName, T entity, String idFieldName) {
        Map<String, Object> updateFields = new LinkedHashMap<>();
        Object idValue = null;

        // 遍历实体字段
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(entity);
                if (field.getName().equals(idFieldName)) {
                    idValue = value;
                    continue;
                }
                if (value != null) {
                    updateFields.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                log.error("获取字段值失败", e);
            }
        }

        if (updateFields.isEmpty() || idValue == null) {
            return Mono.just(BigInteger.ZERO);
        }

        //  驼峰转下划线工具方法
        Function<String, String> camelToUnderline = (name) -> {
            if (name == null || name.isEmpty()) return name;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                char ch = name.charAt(i);
                if (Character.isUpperCase(ch)) {
                    sb.append('_').append(Character.toLowerCase(ch));
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
        spec = spec.bind(idFieldName, idValue);

        return spec.fetch().rowsUpdated().map(BigInteger::valueOf);
    }
}
