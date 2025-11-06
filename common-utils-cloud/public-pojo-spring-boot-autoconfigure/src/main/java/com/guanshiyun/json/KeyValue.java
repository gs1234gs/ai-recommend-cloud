package com.guanshiyun.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldNameConstants
public class KeyValue {
    private BigInteger id;
    private Integer value;
    private String name;
}
