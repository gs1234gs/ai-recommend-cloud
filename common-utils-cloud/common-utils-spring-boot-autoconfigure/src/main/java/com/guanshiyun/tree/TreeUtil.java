package com.guanshiyun.tree;

import io.netty.util.internal.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用树形结构构建工具类
 */
public class TreeUtil {
    // 定义默认的字段名称常量，当外部未传入具体字段名时，将使用这些默认值
    public static String id = "id";             // 默认主键ID字段名
    public static String parentId = "parentId"; // 默认父节点ID字段名
    public static String children = "children"; // 默认子节点集合字段名
    public static String sort = "sort";         // 默认排序字段名

    /**
     * 核心方法：将扁平化的列表构建成树形结构
     * @param flatCollection 要构造的扁平列表
     * @param id             主键ID字段名
     * @param parentId       父节点ID字段名
     * @param children       子节点集合字段名
     * @param sort           排序字段名
     * @param <T>            列表元素的泛型类型
     * @return 构建好的树形结构根节点列表
     */
    public static <T>List<T> buildTree(List<T> flatCollection,//要构造的列表
                                             String id,//id字段名
                                             String parentId,//父id字段名
                                             String children,//子节点字段名
                                             String sort//排序字段名
    ){
        // 如果传入的列表为空，直接返回原列表，避免后续处理空指针异常
        if(flatCollection.isEmpty()){
            return flatCollection;
        }

        // 获取列表中第一个元素的Class对象，用于后续的反射字段查找
        Class<?> clazz = flatCollection.getFirst().getClass();
        // 参数容错处理：如果外部传入的字段名为空或null，则使用类中定义的默认字段名
        id = StringUtil.isNullOrEmpty( id) ? TreeUtil.id : id;
        parentId = StringUtil.isNullOrEmpty( parentId) ? TreeUtil.parentId : parentId;
        children = StringUtil.isNullOrEmpty( children) ? TreeUtil.children : children;
        sort = StringUtil.isNullOrEmpty( sort) ? TreeUtil.sort : sort;


        // 严格校验：如果任何一个核心字段在类中找不到，直接抛出非法参数异常，防止后续逻辑出错
        Field idField = findField(clazz, id);
        Field parentIdField = findField(clazz, parentId);
        Field childrenField = findField(clazz, children);
        Field sortField = findField(clazz, sort);
        if (idField == null) throw new IllegalArgumentException("找不到字段: " + id);
        if (parentIdField == null) throw new IllegalArgumentException("找不到字段: " + parentId);
        if (childrenField == null) throw new IllegalArgumentException("找不到字段: " + children);
        if (sortField ==  null)  throw new IllegalArgumentException("找不到字段: " + sort);
        // 将这四个字段设置为可访问（打破private/protected等访问修饰符的限制）
        makeAccessible(idField);
        makeAccessible(parentIdField);
        makeAccessible(childrenField);
        makeAccessible(sortField);

        // 获取所有 ID // 提取列表中所有元素的主键ID，放入Set集合中，用于后续O(1)时间复杂度的快速查找
        Set<Object> idSet = flatCollection.stream()
                .map(item -> getFieldValue(item, idField)) // 通过反射获取每个元素的主键值
                .filter(Objects::nonNull)                  // 过滤掉主键为null的无效数据
                .collect(Collectors.toSet());              // 收集到Set集合中
        // 筛选出所有的根节点：条件是父节点ID为null，或者父节点ID不在已知的主键集合中
        List<T> roots = flatCollection.stream()
                .filter(item -> {
                    Object pid = getFieldValue(item, parentIdField);
                    // 如果父节点ID为空，或者父节点ID不在现有的ID集合中，说明它是根节点
                    return Objects.isNull(pid) || !idSet.contains(pid);
                })
                .collect(Collectors.toList());

        // 定义自定义排序比较器，用于对同级节点按照sort字段进行升序排列
        Comparator<T> comparator = (o1, o2) -> {
            // 通过反射获取两个对象在sort字段上的值，并强转为Comparable接口以支持比较
            @SuppressWarnings("unchecked")
            Comparable<Object> c1 = (Comparable<Object>) getFieldValue(o1, sortField);
            @SuppressWarnings("unchecked")
            Comparable<Object> c2 = (Comparable<Object>) getFieldValue(o2, sortField);

            // 处理排序字段值为null的情况：两个都为null视为相等
            if (c1 == null && c2 == null) return 0;
            // null值排在前面（返回负数）
            if (c1 == null) return -1;
            if (c2 == null) return 1;
            // 都不为null时，调用Comparable的compareTo方法进行实际比较
            return c1.compareTo(c2);
        };

        // 对根节点列表应用排序比较器进行排序
        roots.sort(comparator);
        // 将扁平列表按照父节点ID进行分组，生成 Map<父节点ID, 子节点列表>，用于后续快速挂载子节点
        Map<Object, List<T>> childrenMap = flatCollection.stream()
                .collect(Collectors.groupingBy(item -> getFieldValue(item, parentIdField)));
        // 初始化一个双端队列（Deque），用于非递归的广度优先遍历（BFS）构建树，避免深层级导致的栈溢出
        Deque<T> deque = new ArrayDeque<>(roots);
        // 预先为所有根节点的children字段初始化一个空的ArrayList，防止后续操作出现空指针
        roots.forEach(root -> setFieldValue(root, childrenField, new ArrayList<T>()));
//            for (T root : roots) {
//                setFieldValue(root, childrenField, new ArrayList<T>());
//            }
        // 使用迭代方式构建树形结构（替代传统的递归方式，性能更好且安全）
        while (!deque.isEmpty()) {
            // 从队列头部取出一个父节点
            T parent = deque.poll();
            // 通过反射获取当前父节点的主键ID
            Object parentIdValue = getFieldValue(parent, idField);
            // 从分组Map中获取当前父节点对应的子节点列表，如果没有则返回空列表
            List<T> childList = childrenMap.getOrDefault(parentIdValue, Collections.emptyList());
            // 对获取到的子节点列表进行排序
            childList.sort(comparator);
            // 将排序后的子节点列表挂载到父节点的children字段上
            setFieldValue(parent, childrenField, childList);
            // 遍历当前父节点的所有子节点
            for (T child : childList) {
                // 为每个子节点的children字段也初始化一个空的ArrayList
                setFieldValue(child, childrenField, new ArrayList<T>());
                // 将当前子节点加入队列，以便在下一轮循环中作为父节点继续挂载它的子节点
                deque.add(child);
            }
        }
        return roots;
    }
    /**
     * 反射工具方法：在类及其所有父类中递归查找指定名称的字段
     * @param clazz     要查找的Class对象
     * @param fieldName 字段名称
     * @return 找到的Field对象，如果未找到则返回null
     */
    private static Field findField(Class<?> clazz, String fieldName) {
        // 初始化一个临时Class变量，从当前类开始向上查找
        Class<?> searchClass = clazz;
        // 循环向上遍历父类，直到Object类（searchClass为null时停止）
        while (searchClass != null) {
            try {
                // 尝试在当前类中获取声明的字段
                return searchClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // 如果当前类中没有该字段，则获取它的父类继续查找
                searchClass = searchClass.getSuperclass();
            }
        }
        // 如果遍历完所有父类仍未找到，返回null
        return null;
    }

    /**
     * 反射工具方法：强制将字段设置为可访问状态
     * @param field 要设置访问权限的Field对象
     */
    private static void makeAccessible(Field field) {
        // 判断字段本身不是public的，或者字段所在的类不是public的
        if (!Modifier.isPublic(field.getModifiers()) ||
            !Modifier.isPublic(field.getDeclaringClass().getModifiers()))
        {
            // 强制打开访问权限，允许对private/protected字段进行读写操作
            field.setAccessible(true);
        }
    }

    /**
     * 反射工具方法：获取对象中指定字段的值
     * @param obj   目标对象实例
     * @param field 要获取值的Field对象
     * @return 字段对应的值
     */
    private static Object getFieldValue(Object obj, Field field) {
        try {
            // 通过反射直接读取对象的字段值（即使字段是private的）
            return field.get(obj);
        } catch (IllegalAccessException e) {
            // 如果访问被拒绝（通常是因为没有调用makeAccessible），抛出运行时异常
            throw new RuntimeException("无法访问字段: " + field.getName(), e);
        }
    }


    /**
     * 反射工具方法：设置对象中指定字段的值
     * @param obj   目标对象实例
     * @param field 要设置值的Field对象
     * @param value 要写入的值
     */
    private static void setFieldValue(Object obj, Field field, Object value) {
        try {
            // 通过反射直接向对象的字段写入值
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            // 如果写入失败，抛出运行时异常
            throw new RuntimeException("无法设置字段: " + field.getName(), e);
        }
    }
}
