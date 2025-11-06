package com.guanshiyun.relationpojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sys_user_post")
public class SysUserPost {
    //主键id
    @Id
    private BigInteger id;
    //用户id
    private BigInteger userId;
    //岗位id
    private BigInteger postId;
}
