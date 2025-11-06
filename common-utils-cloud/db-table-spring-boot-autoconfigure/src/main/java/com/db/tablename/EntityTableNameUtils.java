package com.db.tablename;//package com.guanshiyun.tablename;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ClassUtils;


@Slf4j
public class EntityTableNameUtils {
    public static String getName(Class<?> entityClass) {
        // 先解析代理，获取原始类
        Class<?> userClass = ClassUtils.getUserClass(entityClass);

        Document doc = userClass.getAnnotation(Document.class);
        if (doc != null && !doc.collection().isEmpty()) {
            return doc.collection();
        }

        Table table = userClass.getAnnotation(Table.class);
        log.info("实体类 " + table);
        if (table != null && !table.value().isEmpty()) {
            return table.value();
        }

        throw new IllegalStateException("实体类 " + userClass.getSimpleName() +
                " 未指定 @Document(collection) 或 @Table(name)，无法确定表名");
    }
}
