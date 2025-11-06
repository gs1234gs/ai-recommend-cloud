package com.guanshiyun.tree;

import io.netty.util.internal.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class TreeUtil {
    public static String id = "id";
    public static String parentId = "parentId";
    public static String children = "children";
    public static String sort = "sort";
    public static <T>List<T> buildTree(List<T> flatCollection,//要构造的列表
                                             String id,//id字段名
                                             String parentId,//父id字段名
                                             String children,//子节点字段名
                                             String sort//排序字段名
    ){
        if(flatCollection.isEmpty()){
            return flatCollection;
        }
        // 获取第一个元素的类
        Class<?> clazz = flatCollection.getFirst().getClass();
        //查找字段
        id = StringUtil.isNullOrEmpty( id) ? TreeUtil.id : id;
        parentId = StringUtil.isNullOrEmpty( parentId) ? TreeUtil.parentId : parentId;
        children = StringUtil.isNullOrEmpty( children) ? TreeUtil.children : children;
        sort = StringUtil.isNullOrEmpty( sort) ? TreeUtil.sort : sort;


        // 查找字段
        Field idField = findField(clazz, id);
        Field parentIdField = findField(clazz, parentId);
        Field childrenField = findField(clazz, children);
        Field sortField = findField(clazz, sort);
        if (idField == null) throw new IllegalArgumentException("找不到字段: " + id);
        if (parentIdField == null) throw new IllegalArgumentException("找不到字段: " + parentId);
        if (childrenField == null) throw new IllegalArgumentException("找不到字段: " + children);
        if (sortField ==  null)  throw new IllegalArgumentException("找不到字段: " + sort);
        makeAccessible(idField);
        makeAccessible(parentIdField);
        makeAccessible(childrenField);
        makeAccessible(sortField);

        // 获取所有 ID
        Set<Object> idSet = flatCollection.stream()
                .map(item -> getFieldValue(item, idField))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        // 找根节点
        List<T> roots = flatCollection.stream()
                .filter(item -> {
                    Object pid = getFieldValue(item, parentIdField);
                    return Objects.isNull(pid) || !idSet.contains(pid);
                })
                .collect(Collectors.toList());
        // 自定义比较器
        @SuppressWarnings("unchecked")
        Comparator<T> comparator = (o1, o2) -> {
            Comparable<Object> c1 = (Comparable<Object>) getFieldValue(o1, sortField);
            Comparable<Object> c2 = (Comparable<Object>) getFieldValue(o2, sortField);
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return -1;
            if (c2 == null) return 1;
            return c1.compareTo(c2);
        };


        roots.sort(comparator);
        // 按 parentId 分组
        Map<Object, List<T>> childrenMap = flatCollection.stream()
                .collect(Collectors.groupingBy(item -> getFieldValue(item, parentIdField)));
        // 初始化栈
        Deque<T> stack = new ArrayDeque<>(roots);
        roots.forEach(root -> setFieldValue(root, childrenField, new ArrayList<T>()));
//            for (T root : roots) {
//                setFieldValue(root, childrenField, new ArrayList<T>());
//            }
        // 构建树
        while (!stack.isEmpty()) {
            T parent = stack.pop();
            Object parentIdValue = getFieldValue(parent, idField);
            List<T> childList = childrenMap.getOrDefault(parentIdValue, Collections.emptyList());
            childList.sort(comparator);
            setFieldValue(parent, childrenField, childList);
            for (T child : childList) {
                setFieldValue(child, childrenField, new ArrayList<T>());
                stack.push(child);
            }
        }
        return roots;
    }
    // 反射工具方法（略，同上）
    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> searchClass = clazz;
        while (searchClass != null) {
            try {
                return searchClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                searchClass = searchClass.getSuperclass();
            }
        }
        return null;
    }
    private static void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers()) ||
            !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(true);
        }
    }
    private static Object getFieldValue(Object obj, Field field) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法访问字段: " + field.getName(), e);
        }
    }

    private static void setFieldValue(Object obj, Field field, Object value) {
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法设置字段: " + field.getName(), e);
        }
    }
}
