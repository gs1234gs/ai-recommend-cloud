package com.guanshiyun.responsepojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.math.BigInteger;

/**
 * 提供泛型的分页结果类
 * */
@ToString
@Getter
@Builder// 用于创建对象时，自动填充属性的注解，使用链式调用
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)// 表示序列化时，如果属性值为 null，则不进行序列化
@JsonDeserialize(builder = PageResultT.PageResultTBuilder.class)  // 关键注解：告诉 Jackson 用 Builder 反序列
public class PageResultT<T> {
    //pageNum
    private BigInteger pageNum;
    //pageSize
    private Integer pageSize;
    //总数
    private long total;
    //结果集
    private T rows;
    public PageResultT<T> setPageNum(BigInteger pageNum){
        this.pageNum = pageNum;
        return this;
    }
    public PageResultT<T> setPageSize(Integer pageSize){
        this.pageSize = pageSize;
        return this;
    }
    public PageResultT<T> setTotal(long total){
        this.total = total;
        return this;
    }
    public PageResultT<T> setRows(T rows){
        this.rows = rows;
        return this;
    }
}
