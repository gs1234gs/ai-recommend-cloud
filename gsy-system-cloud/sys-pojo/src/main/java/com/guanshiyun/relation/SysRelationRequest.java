package com.guanshiyun.relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysRelationRequest {
    //实体id
    private BigInteger entityId;
    //角色 id
    private BigInteger roleId;
}
