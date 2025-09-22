package com.ssafy.pocketc_backend.domain.mission.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.pocketc_backend.domain.mission.dto.request.MissionReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MissionRedisService {

    private final RedisTemplate<String, String> redis; // 위에서 만든 String 전용
    private final ObjectMapper mapper;                 // 위에서 만든 ObjectMapper

    private static final java.time.ZoneId ZONE = java.time.ZoneId.of("Asia/Seoul");

    /** key 예시: missions:{userId}:{yyyy-MM-dd} */
    public String buildKey(Integer userId, java.time.LocalDate date) {
        return "missions:%d:%s".formatted(userId, date);
    }

    /** 자정까지 TTL */
    public java.time.Duration ttlUntilMidnight() {
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now(ZONE);
        java.time.ZonedDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay(ZONE);
        return java.time.Duration.between(now, midnight);
    }

    /** 단건 저장 */
    public void putOne(String key, MissionReqDto dto, java.time.Duration ttl) {
        try {
            String json = mapper.writeValueAsString(dto);
            redis.opsForValue().set(key, json, ttl);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("MissionReqDto 직렬화 실패", e);
        }
    }

    /** 리스트 저장 */
    public void putList(String key, java.util.List<MissionReqDto> list, java.time.Duration ttl) {
        try {
            String json = mapper.writeValueAsString(list);
            redis.opsForValue().set(key, json, ttl);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("MissionReqDto 리스트 직렬화 실패", e);
        }
    }

    /** 단건 조회 */
    public MissionReqDto getOne(String key) {
        String json = redis.opsForValue().get(key);
        if (json == null) return null;
        try {
            return mapper.readValue(json, MissionReqDto.class);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("MissionReqDto 역직렬화 실패", e);
        }
    }

    /** 리스트 조회 */
    public java.util.List<MissionReqDto> getList(String key) {
        String json = redis.opsForValue().get(key);
        if (json == null) return java.util.List.of();
        try {
            var type = mapper.getTypeFactory()
                    .constructCollectionType(java.util.List.class, MissionReqDto.class);
            return mapper.readValue(json, type);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("MissionReqDto 리스트 역직렬화 실패", e);
        }
    }

    public void evict(String key) {
        redis.delete(key);
    }
}