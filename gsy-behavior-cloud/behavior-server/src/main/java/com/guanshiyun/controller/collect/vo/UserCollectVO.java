package com.guanshiyun.controller.collect.vo;

import com.guanshiyun.profile.CollectProfile;
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
public class UserCollectVO {
    //会话 id
    private BigInteger id;
    //会话内容
    private List<CollectProfile> collectContent;
}
