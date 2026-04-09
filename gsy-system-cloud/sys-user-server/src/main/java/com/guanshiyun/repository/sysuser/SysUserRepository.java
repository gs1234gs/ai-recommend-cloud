package com.guanshiyun.repository.sysuser;

import com.guanshiyun.userpojo.SysUser;
import org.springframework.data.r2dbc.repository.R2dbcRepository;



public interface SysUserRepository extends R2dbcRepository<SysUser, Long> {
}
