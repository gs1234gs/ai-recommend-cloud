package com.guanshiyun.responsepojo;

import lombok.*;
import lombok.experimental.Accessors;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CursorPageResult<T> {
    //lastId
    private Long cursor;
    //结果集
    private  T rows;
    //是否存在下一页
    private Boolean hasNext;

    public static <T> CursorPageResult<T> of(Long cursor, T rows, Boolean hasNext) {
        return CursorPageResult.<T>builder()
                .cursor(cursor)
                .rows(rows)
                .hasNext(hasNext)
                .build();
    }
}
