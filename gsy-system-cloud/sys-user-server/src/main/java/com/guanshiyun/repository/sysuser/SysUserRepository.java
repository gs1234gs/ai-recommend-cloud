package com.guanshiyun.repository.sysuser;

import com.guanshiyun.userpojo.SysUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;



public interface SysUserRepository extends ReactiveCrudRepository<SysUser, Long> {
}
