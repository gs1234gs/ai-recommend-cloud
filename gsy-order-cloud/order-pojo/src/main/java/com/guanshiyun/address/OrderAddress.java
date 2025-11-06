package com.guanshiyun.address;

import com.guanshiyun.base.BasePojo;
import lombok.*;

import java.math.BigInteger;
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderAddress extends BasePojo {
    //地址id
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
