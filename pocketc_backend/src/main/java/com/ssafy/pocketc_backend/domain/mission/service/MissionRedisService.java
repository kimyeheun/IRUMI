package com.ssafy.pocketc_backend.domain.mission.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.pocketc_backend.domain.mission.dto.request.MissionRedisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper;

    public String buildKey(Integer userId, LocalDate date) {
        return userId + ":" + date; // 예: 1:2025-09-22
    }

    public Duration ttlUntilNext6am() {
        ZoneId KST = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(KST);
        ZonedDateTime next6am = now.plusDays(1)
                .toLocalDate()
                .atTime(6, 0)
                .atZone(KST);
        return Duration.between(now, next6am);
    }

    public List<MissionRedisDto> getList(String key) {
        Object val = redisTemplate.opsForValue().get(key);
        return (List<MissionRedisDto>) val;
    }

    public void putList(String key, List<MissionRedisDto> list, Duration ttl) {
        var ops = redisTemplate.opsForValue();
        ops.set(key, list, ttl);
    }
}