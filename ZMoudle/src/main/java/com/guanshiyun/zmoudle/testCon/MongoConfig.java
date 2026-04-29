//package com.xinghe.zmoudle.testCon;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
//import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
//
//@Configuration
//public class MongoConfig {
//
//    /**
//     * 利用 Spring Boot 提供的后置处理器钩子，
//     * 在默认的 MappingMongoConverter 创建完成后，直接修改它的 TypeMapper。
//     */
//    @Bean
//    public MappingMongoConverter mappingMongoConverter(MappingMongoConverter converter) {
//        // 将 typeMapper 设置为 null，即可彻底禁用 _class 字段的写入
//        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
//        return converter;
//    }
//}
