package com.guanshiyun.tablename;//package com.guanshiyun.tablename;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.relational.core.mapping.Table;


public class EntityTableNameUtils {
    public static String getName(Class<?> entityClass) {
        // 1. 检查 MongoDB 的 @Document
        Document doc = entityClass.getAnnotation(Document.class);
        if (doc != null && !doc.collection().isEmpty()) {
            return doc.collection(); // 直接返回注解值，不做任何转换
        }

        // 2. 检查 JPA / R2DBC 的 @Table
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null && !table.name().isEmpty()) {
            return table.name(); // 直接返回注解值
        }

        // 3. 安全 fallback：使用类名（原样或小写），但不转下划线
        // 可以选择抛异常，强制要求必须写注解
        throw new IllegalStateException(
                "实体类 " + entityClass.getSimpleName() +
                        " 未指定 @Document(collection) 或 @Table(name)，无法确定表名"
        );

        // 或者返回类名小写（谨慎使用）：
        // return entityClass.getSimpleName().toLowerCase();
    }
}
