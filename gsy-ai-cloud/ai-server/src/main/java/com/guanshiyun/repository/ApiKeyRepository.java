package com.guanshiyun.repository;

import com.guanshiyun.base.ApiKey;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ApiKeyRepository extends R2dbcRepository<ApiKey,Long> {
}
