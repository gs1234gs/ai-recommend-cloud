package com.guanshiyun.configutil;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
public class ConfigSysUtil {
    @Bean
    public R2dbcTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }
    @Bean
    public TransactionalOperator transactionalOperator(R2dbcTransactionManager txManager) {
        return TransactionalOperator.create(txManager);
    }

}
