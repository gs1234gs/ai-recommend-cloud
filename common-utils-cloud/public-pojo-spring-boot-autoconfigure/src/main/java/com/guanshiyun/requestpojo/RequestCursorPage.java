package com.guanshiyun.requestpojo;

import lombok.*;
import lombok.experimental.Accessors;


/**
 * 游标
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RequestCursorPage<T> {
    private Long lastId;
    @Builder.Default
    private Integer pageSize = 10;
    @Builder.Default
    private String order = "DESC";
    private T condition;
}
