package com.ssafy.pocketc_backend.domain.mission.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.pocketc_backend.domain.mission.entity.Mission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionRedisService {

    private final StringRedisTemplate redis;     // 값은 JSON 문자열로
    private final ObjectMapper mapper;

    public String buildKey(Integer userId, LocalDate date) {
        return userId + ":" + date; // 예: 1:2025-09-22
    }

    /** 오늘 자정까지 TTL */
    public Duration ttlUntilNext6am() {
        ZoneId KST = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(KST);
        ZonedDateTime next6am = now.plusDays(1)
                .toLocalDate()
                .atTime(6, 0)
                .atZone(KST);
        return Duration.between(now, next6am);
    }

    /** 리스트 저장 (JSON 배열 통짜) */
    public void putList(String key, List<Mission> list, Duration ttl) {
        try {
            String json = mapper.writeValueAsString(list);
            redis.opsForValue().set(key, json, ttl);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("MissionReqDto 리스트 직렬화 실패", e);
        }
    }

    public List<Mission> getList(String key) {
        String json = redis.opsForValue().get(key);
        if (json == null || json.isBlank()) return List.of();
        try {
            return mapper.readValue(json, new TypeReference<List<Mission>>() {});
        } catch (JsonProcessingException e) {
            // 로그 남기고, 안전하게 빈 리스트
            System.err.println("Redis 역직렬화 실패: " + e.getMessage());
            return List.of();
        }
    }

    public void deleteAll() {

    }
}