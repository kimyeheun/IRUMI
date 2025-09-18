package com.ssafy.pocketc_backend.global.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "RT:";

    //RefreshToken 저장 (14일 유효)
    public void save(String userId, String refreshToken) {
        redisTemplate.opsForValue().set(KEY_PREFIX + userId, refreshToken, 14, TimeUnit.DAYS);
    }

    //RefreshToken 검증
    public boolean validate(String userId, String refreshToken) {
        String saved = redisTemplate.opsForValue().get(KEY_PREFIX + userId);
        return saved != null && saved.equals(refreshToken);
    }

    //RefreshToken 삭제 (로그아웃)
    public void delete(String userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }
}
