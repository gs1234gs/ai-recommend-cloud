package com.guanshiyun;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class MainTest1 {
    public static void main(String[] args) {
// 根节点
        A root = A.builder()
                .id(1)
                .name("总公司")
                .parentId(0) // 根节点 parentId 为 null 或 0
                .build();

// 一级部门
        A deptFinance = A.builder()
                .id(2)
                .name("财务部")
                .parentId(1)
                .build();

        A deptTech = A.builder()
                .id(3)
                .name("技术部")
                .parentId(1)
                .build();

        A deptHR = A.builder()
                .id(4)
                .name("人事部")
                .parentId(1)
                .build();

// 技术部下的二级部门
        A teamBackend = A.builder()
                .id(5)
                .name("后端组")
                .parentId(3)
                .build();

        A teamFrontend = A.builder()
                .id(6)
                .name("前端组")
                .parentId(3)
                .build();

// 后端组下的小组（三级）
        A subgroupJava = A.builder()
                .id(7)
                .name("Java组")
                .parentId(5)
                .build();

// 人事部下的二级部门
        A teamRecruitment = A.builder()
                .id(8)
                .name("招聘组")
                .parentId(4)
                .build();
        List<A> list = new ArrayList<>();
        list.add(root);
        list.add(deptFinance);
        list.add(deptTech);
        list.add(deptHR);
        list.add(teamBackend);
        list.add(teamFrontend);
        list.add(teamRecruitment);
        list.add(subgroupJava);
        List<A> as = listA(list, 0);
        System.out.println(as);
    }
    public static List< A> listA(List< A> list, Integer parentId){
        //先分组
        Map<Integer, List<A>> childrenMap = list.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(A::getParentId));
//        获取当前 parentId 的所有直接子节点
        List<A> children = childrenMap.getOrDefault(parentId, Collections.emptyList());
       return children.stream()
                .peek(item -> {
                    List<A> as = listA(list, item.getId());
                    item.setChildren(as);
                }).toList();

    }

}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

class A {
    private Integer id;
    private String name;
    private Integer parentId;
    List<A> children;
}
