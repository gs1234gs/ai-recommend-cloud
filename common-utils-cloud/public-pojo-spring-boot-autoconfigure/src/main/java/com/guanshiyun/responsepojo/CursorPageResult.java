package com.guanshiyun.responsepojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageResult<T> {
    //lastId
    private BigInteger cursor;
    //结果集
    private  T rows;
    //是否存在下一页
    private Boolean hasNext;
    public CursorPageResult< T> setLastId(BigInteger cursor){
        this.cursor = cursor;
        return this;
    }
    public CursorPageResult< T> setRows( T rows){
        this.rows = rows;
        return this;
    }
    public CursorPageResult<T> setHasNext(Boolean hasNext){
        this.hasNext = hasNext;
        return this;
    }
}
