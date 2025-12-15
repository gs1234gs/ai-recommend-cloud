package com.guanshiyun.r2dbc;


import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
public class R2dbcConfig {
    // --- R2DBC MySQL ---
    @Value("${spring.r2dbc.mysql.url}")
    private String mysqlUrl;
    @Value("${spring.r2dbc.mysql.username}")
    private String mysqlUsername;
    @Value("${spring.r2dbc.mysql.password}")
    private String mysqlPassword;
    @Value("${spring.r2dbc.postgres.url}")
    // --- R2DBC PostgreSQL ---
    private String postgresUrl;
    @Value("${spring.r2dbc.postgres.username}")
    private String postgresUsername;
    @Value("${spring.r2dbc.postgres.password}")
    private String postgresPassword;
    // --- MongoDB ---
    @Value("${spring.data.mongodb.primary.uri}")
    private String mongodbUri;
    @Value("${spring.data.mongodb.primary.database}")
    private String mongodbDatabase;
    @Value("${spring.data.mongodb.secondary.uri}")
    private String mongodbSecondaryUri;
    @Value("${spring.data.mongodb.secondary.database}")
    private String mongodbSecondaryDatabase;


    @Bean
    @Primary
    public DatabaseClient mysqlDatabaseClient() {
        return DatabaseClient.builder()
                .connectionFactory(ConnectionFactoryBuilder.withUrl(mysqlUrl)
                        .username(mysqlUsername)
                        .password(mysqlPassword)
                        .build()
                )
                .build();
    }

    @Bean
    @Qualifier("postgresDatabaseClient")
    public DatabaseClient postgresDatabaseClient() {
        return DatabaseClient.builder()
                .connectionFactory(
                        ConnectionFactoryBuilder.withUrl(postgresUrl)
                                .username(postgresUsername)
                                .password(postgresPassword)
                                .build()
                )
                .build();
    }
    // -------------------- MongoTemplate Beans --------------------
    // --- MongoDB 主库，指定 Bean 名称 reactiveMongoTemplate ---
    @Bean(name = "reactiveMongoTemplate")
    @Primary
    public ReactiveMongoTemplate primaryMongoTemplate() {
        MongoClient client = MongoClients.create(mongodbUri);
        return new ReactiveMongoTemplate(client,mongodbDatabase);
    }
    @Bean
    @Qualifier("secondaryMongoTemplate")
    public ReactiveMongoTemplate secondaryMongoTemplate() {
        MongoClient client = MongoClients.create(mongodbSecondaryUri);
        return new ReactiveMongoTemplate(client,mongodbSecondaryDatabase);
    }


}
