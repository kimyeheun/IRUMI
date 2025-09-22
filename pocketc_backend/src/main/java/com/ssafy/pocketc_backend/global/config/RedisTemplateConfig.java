package com.ssafy.pocketc_backend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisTemplateConfig {

    @Bean
    public ObjectMapper redisObjectMapper() {
        return new ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // 값은 우리가 직접 JSON으로 만들어 넣을 거라 String 전용으로 심플하게 사용
    @Bean
    @Primary
    public RedisTemplate<String, String> stringRedisTemplateOnly(org.springframework.data.redis.connection.RedisConnectionFactory cf) {
        RedisTemplate<String, String> t = new RedisTemplate<>();
        t.setConnectionFactory(cf);
        t.setKeySerializer(new org.springframework.data.redis.serializer.StringRedisSerializer());
        t.setValueSerializer(new org.springframework.data.redis.serializer.StringRedisSerializer());
        t.afterPropertiesSet();
        return t;
    }
}