package com.guanshiyun.rpc.behaviorapi.click.vo;

import com.guanshiyun.rpc.profile.ClickProfileApi;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.math.BigInteger;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@FieldNameConstants
public class UserClickVOApi {
    private BigInteger id;
    //点击内容
    private List<ClickProfileApi> clickContent;
}
