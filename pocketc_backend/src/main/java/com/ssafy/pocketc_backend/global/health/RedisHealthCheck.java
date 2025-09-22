package com.ssafy.pocketc_backend.global.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisHealthCheck implements CommandLineRunner {
    private final StringRedisTemplate r;
    @Override public void run(String... args) {
        r.opsForValue().set("health:ping","pong");
        System.out.println("Redis says: " + r.opsForValue().get("health:ping")); // pong 기대
    }
}