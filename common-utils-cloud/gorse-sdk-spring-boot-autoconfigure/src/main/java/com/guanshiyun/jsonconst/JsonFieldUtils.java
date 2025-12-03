package com.guanshiyun.jsonconst;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JsonFieldUtils {
    private static final Map<Class<?>, Map<String, String>> CACHE = new ConcurrentHashMap<>();

    /**
     * 获取字段在 JSON 中的名称（基于 @JsonProperty）
     */
    public static String getJsonFieldName(Class<?> clazz, String javaFieldName) {
        return CACHE.computeIfAbsent(clazz, JsonFieldUtils::buildFieldMap)
                .getOrDefault(javaFieldName, javaFieldName);
    }

    private static Map<String, String> buildFieldMap(Class<?> clazz) {
        Map<String, String> map = new HashMap<>();
        for (Field field : getAllFields(clazz)) {
            field.setAccessible(true);
            String jsonName = field.getName(); // 默认是字段名

            JsonProperty ann = field.getAnnotation(JsonProperty.class);
            if (ann != null && !ann.value().isEmpty()) {
                jsonName = ann.value();
            }

            map.put(field.getName(), jsonName);
        }
        return map;
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}
