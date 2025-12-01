package com.guanshiyun.jacksonBigNumberConfig;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

@Configuration
public class ConditionalJacksonConfig {
    @Bean
    @ConditionalOnBigNumberSerialization
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            SimpleModule module = new SimpleModule();

            // —— 序列化（后端 → 前端）：全部转字符串 ——
            module.addSerializer(Long.class, ToStringSerializer.instance);
            module.addSerializer(Long.TYPE, ToStringSerializer.instance);
            module.addSerializer(BigInteger.class, ToStringSerializer.instance);
            module.addSerializer(BigDecimal.class, ToStringSerializer.instance);

            // —— 反序列化（前端 → 后端）：字符串自动转回数值 ——
            module.addDeserializer(Long.class, new StdDeserializer<Long>(Long.class) {
                @Override
                public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                    String value = p.getText();
                    return (value == null || value.isEmpty()) ? null : Long.valueOf(value);
                }
            });

            module.addDeserializer(Long.TYPE, new StdDeserializer<Long>(Long.TYPE) {
                @Override
                public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                    return Long.valueOf(p.getText());
                }
            });

            module.addDeserializer(BigInteger.class, new StdDeserializer<BigInteger>(BigInteger.class) {
                @Override
                public BigInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                    String value = p.getText();
                    return (value == null || value.isEmpty()) ? null : new BigInteger(value);
                }
            });

            module.addDeserializer(BigDecimal.class, new StdDeserializer<BigDecimal>(BigDecimal.class) {
                @Override
                public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                    String value = p.getText();
                    return (value == null || value.isEmpty()) ? null : new BigDecimal(value);
                }
            });

            builder.modules(module);
        };
    }
}
