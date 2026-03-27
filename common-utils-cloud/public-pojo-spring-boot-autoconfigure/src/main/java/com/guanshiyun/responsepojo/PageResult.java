package com.guanshiyun.responsepojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;



/**
 *
 * @Description: 分页结果类
 *
 * */
@Data
@Builder// 用于创建对象时，自动填充属性的注解，使用链式调用
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)// 表示序列化时，如果属性值为 null，则不进行序列化
@Accessors(chain = true)
public class PageResult {
    //总数
    private long total;
    //结果集
    private Object rows;
}
