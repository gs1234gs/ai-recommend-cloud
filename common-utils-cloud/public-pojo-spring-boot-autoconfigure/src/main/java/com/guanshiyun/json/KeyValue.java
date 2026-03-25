package com.guanshiyun.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldNameConstants
public class KeyValue {
    private Long id;
    private Integer value;
    private String name;
}
