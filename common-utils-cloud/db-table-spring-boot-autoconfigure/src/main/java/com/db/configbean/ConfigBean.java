package com.db.configbean;

import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.guanshiyun.mylong.MyLong;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
public class ConfigBean {
    @Bean
    public R2dbcUpdateHelper r2dbcUpdateHelper(DatabaseClient databaseClient, MyLong myLong) {
        return new R2dbcUpdateHelper(databaseClient,myLong);
    }
}
