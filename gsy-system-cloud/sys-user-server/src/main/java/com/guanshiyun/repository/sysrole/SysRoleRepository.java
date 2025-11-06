package com.guanshiyun.repository.sysrole;

import com.guanshiyun.rolepojo.SysRole;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.math.BigInteger;

public interface SysRoleRepository extends ReactiveCrudRepository<SysRole, BigInteger> {

}
