package com.guanshiyun.requestpojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
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
    public RequestPage< T> setPageNum(Long pageNum){
        this.pageNum = pageNum;
        return this;
    }
    public RequestPage< T> setPageSize(Integer pageSize){
        this.pageSize = pageSize;
        return this;
    }
    public RequestPage< T> setCondition(T condition){
        this.condition = condition;
        return this;
    }
}
