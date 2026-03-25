package com.guanshiyun.relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysRelationRequest {
    //实体id
    private Long entityId;
    //角色 id
    private Long roleId;
}
