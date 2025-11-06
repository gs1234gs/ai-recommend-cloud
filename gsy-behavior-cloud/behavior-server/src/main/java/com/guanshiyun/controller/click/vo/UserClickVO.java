package com.guanshiyun.controller.click.vo;

import com.guanshiyun.profile.ClickProfile;
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
public class UserClickVO {
    private BigInteger id;
    //点击内容
    private List<ClickProfile> clickContent;
}
