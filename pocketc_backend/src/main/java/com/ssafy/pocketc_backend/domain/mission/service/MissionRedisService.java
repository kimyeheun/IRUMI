package com.ssafy.pocketc_backend.domain.mission.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
        if (val == null) return List.of();

        try {
            // 1) JSON 문자열로 저장되어 있던 경우
            if (val instanceof String s) {
                return OM.readValue(s, new TypeReference<List<MissionRedisDto>>() {});
            }
            // 2) 바이트 배열로 저장되어 있던 경우
            if (val instanceof byte[] b) {
                return OM.readValue(b, new TypeReference<List<MissionRedisDto>>() {});
            }
            // 3) 이미 메모리 객체(List<LinkedHashMap> 등)로 역직렬화된 경우
            return OM.convertValue(val, new TypeReference<List<MissionRedisDto>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Redis 리스트 역직렬화 실패: " + val.getClass(), e);
        }
    }

    public void putList(String key, List<MissionRedisDto> list, Duration ttl) {
        var ops = redisTemplate.opsForValue();
        ops.set(key, list, ttl);
    }

    private static final ObjectMapper OM = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
}