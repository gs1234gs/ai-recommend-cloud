package com.guanshiyun.util;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

public class ColumnNameExtractor {

    /**
     * 核心方法：通过方法引用提取数据库列名
     * @param fn 方法引用，例如 User::getName
     * @return 数据库列名，例如 "user_name"
     */
    public static <T, R> String extract(Function<T, R> fn) {
        try {
            // 1. 解析方法引用，获取对应的 Method 对象
            Method method = resolveMethod(fn);
            String fieldName = method.getName().replaceFirst("get", "");
            // 首字母小写，还原为字段名 (getName -> name)
            fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);

            // 2. 获取字段对象，检查是否有 @Column 注解
            Field field = method.getDeclaringClass().getDeclaredField(fieldName);
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                // 优先使用 @Column 指定的值
                return !column.value().isEmpty() ? column.value() : camelToUnderscore(field.getName());
            }

            // 3. 如果没有注解，默认执行驼峰转下划线
            return camelToUnderscore(method.getName());

        } catch (Exception e) {
            throw new RuntimeException("无法解析字段名，请确保传入的是标准 Getter 方法引用", e);
        }
    }

    // 利用 SerializedLambda 解析方法引用背后的真实 Method
    private static Method resolveMethod(Function<?, ?> fn) throws Exception {
        Method writeReplace = fn.getClass().getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(fn);

        String implClassName = lambda.getImplClass().replace("/", ".");
        String methodName = lambda.getImplMethodName();

        Class<?> clazz = Class.forName(implClassName);
        // 匹配对应的 getter 方法
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new NoSuchMethodException("未找到方法: " + methodName);
    }

    // 提取表名
    public static String extractTableName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Table.class)) {
            Table table = clazz.getAnnotation(Table.class);
            return !table.value().isEmpty() ? table.value() : table.name();
        }
        return camelToUnderscore(clazz.getSimpleName());
    }
   public static String extractIdColumnName(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            // 1. 严格校验注解类型，防止误识别
            if (field.isAnnotationPresent(Id.class)) {
                // 2. 检查是否有 @Column 注解显式指定列名
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    return !column.value().isEmpty() ? column.value() : camelToUnderscore(field.getName());
                }
                // 3. 如果没有 @Column，默认将字段名转为下划线风格
                return camelToUnderscore(field.getName());
            }
        }
        throw new IllegalStateException("Entity " + clazz.getName() + " must have a field annotated with @Id");
    }

    public static String camelToUnderscore(String name) {
        return name.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}