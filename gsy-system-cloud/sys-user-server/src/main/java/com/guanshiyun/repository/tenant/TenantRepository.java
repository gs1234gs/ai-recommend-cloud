package com.guanshiyun.repository.tenant;

import com.guanshiyun.tenant.Tenant;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface TenantRepository extends R2dbcRepository<Tenant, Long> {
}
