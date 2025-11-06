package com.guanshiyun.requestpojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

/**
 * 游标
 * */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestCursorPage< T> {
    private BigInteger lastId;
    private Integer pageSize;
    @Builder.Default
    private String order = "DESC";
    private T condition;
    public RequestCursorPage<T> setLastId(BigInteger lastId){
        this.lastId = lastId;
        return this;
    }
    public RequestCursorPage< T> setPageSize(Integer pageSize){
        this.pageSize = pageSize;
        return this;
    }
    public RequestCursorPage< T> setOrder(String order){
        this.order = order;
        return this;
    }
    public RequestCursorPage< T> setCondition(T condition){
        this.condition = condition;
        return this;
    }
}
