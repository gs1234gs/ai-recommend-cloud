package com.guanshiyun.requestpojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestPage<T> {
    // 当前页码
    private BigInteger pageNum;
    // 每页数量
    private Integer pageSize;
    // 查询条件
    private T condition;
}
