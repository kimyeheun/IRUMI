package com.ssafy.pocketc_backend.domain.mission.service;

import com.ssafy.pocketc_backend.domain.mission.dto.request.MissionReqDto;
import com.ssafy.pocketc_backend.domain.mission.dto.response.MissionResDto;
import com.ssafy.pocketc_backend.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.time.LocalDateTime.now;

@Service
@Transactional
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionRedisService missionRedisService;

    public MissionResDto getMissions(Integer userId) {
        var today = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
        String key = missionRedisService.buildKey(userId, today);
        System.out.println(key);
        // 1) 캐시 먼저
        MissionReqDto one = missionRedisService.getOne(key);
        System.out.println(one.toString());
        if (one != null) {

            System.out.println("캐시 히트");
            return null;
        }

        MissionReqDto mission = MissionReqDto.of(1,"제발 되게 해주세요",1,"어려운거 아니잖아요",1,now(),now());
        missionRedisService.putOne(key, mission, missionRedisService.ttlUntilMidnight());
        System.out.println(1);
        one = missionRedisService.getOne(key);
        System.out.println(2);
        System.out.println(one.dsl());
        System.out.println(one.mission());
        if (one != null) {

            System.out.println("캐시 히트");
            return null;
        }
        return new MissionResDto(null, null);
    }

    public void saveAiResultForToday(Integer userId, java.util.List<MissionReqDto> aiDtos) {
        var today = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
        String key = missionRedisService.buildKey(userId, today);

        // 필요 시 DB 저장 로직 …

        // 캐시에 그대로 저장
        missionRedisService.putList(key, aiDtos, missionRedisService.ttlUntilMidnight());
    }
}