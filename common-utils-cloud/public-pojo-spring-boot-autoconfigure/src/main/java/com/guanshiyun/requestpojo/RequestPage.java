package com.guanshiyun.requestpojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestPage<T> {
    // 当前页码
    private Long pageNum;
    // 每页数量
    private Integer pageSize;
    // 查询条件
    private T condition;
}
