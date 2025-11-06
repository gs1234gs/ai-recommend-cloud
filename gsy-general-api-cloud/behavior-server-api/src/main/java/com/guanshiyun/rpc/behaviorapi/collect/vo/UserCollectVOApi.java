package com.guanshiyun.rpc.behaviorapi.collect.vo;

import com.guanshiyun.rpc.profile.CollectProfileApi;
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
public class UserCollectVOApi {
    //会话 id
    private BigInteger id;
    //会话内容
    private List<CollectProfileApi> collectContent;
}
