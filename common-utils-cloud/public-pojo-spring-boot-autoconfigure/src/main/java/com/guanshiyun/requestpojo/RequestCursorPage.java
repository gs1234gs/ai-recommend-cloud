package com.guanshiyun.requestpojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



/**
 * 游标
 * */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestCursorPage< T> {
    private Long lastId;
    @Builder.Default
    private Integer pageSize = 10;
    @Builder.Default
    private String order = "DESC";
    private T condition;
    public RequestCursorPage<T> setLastId(Long lastId){
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
