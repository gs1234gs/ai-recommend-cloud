package com.guanshiyun.responsepojo;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigInteger;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CursorPageResult<T> {
    //lastId
    private BigInteger cursor;
    //结果集
    private  T rows;
    //是否存在下一页
    private Boolean hasNext;
}
