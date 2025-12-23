package com.guanshiyun.controller.address.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldNameConstants
public class OrderAddressSaveVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private BigInteger id;
    //地址
    private String address;
    //联系电话
    private String phone;
    //联系人姓名
    private String name;
    //描述
    private String description;
}
