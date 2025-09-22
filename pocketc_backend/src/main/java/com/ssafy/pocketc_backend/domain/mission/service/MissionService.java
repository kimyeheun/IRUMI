package com.ssafy.pocketc_backend.domain.mission.service;

import com.ssafy.pocketc_backend.domain.mission.dto.request.MissionReqDto;
import com.ssafy.pocketc_backend.domain.mission.dto.response.MissionDto;
import com.ssafy.pocketc_backend.domain.mission.dto.response.MissionResDto;
import com.ssafy.pocketc_backend.domain.mission.entity.Mission;
import com.ssafy.pocketc_backend.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@Transactional
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository; // 주간/월간용 DB 조회에 사용(예시)
    private final MissionRedisService missionRedisService;

    public MissionResDto getMissions(Integer userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        String key = missionRedisService.buildKey(userId, today);

        // 1) 캐시 먼저
        List<Mission> cached = missionRedisService.getList(key);

        if (!cached.isEmpty()) {
            List<MissionDto> missionDtoList = new ArrayList<>();
            for (Mission mission : cached) {
                missionDtoList.add(MissionDto.from(mission));
            }
            return new MissionResDto(null, missionDtoList);
        }

        List<Mission> missions = new ArrayList<>();
        //
        //
        //
        //

        List<MissionDto> missionDtoList = new ArrayList<>();
        for (Mission mission : missions) {
            missionDtoList.add(MissionDto.from(mission));
        }

        // Weekly, Monthly 미션 추가
        List<Mission> weekMonth = missionRepository.findAllByUser_UserId(userId);
        missions.addAll(weekMonth);
        missionRedisService.putList(key, missions, missionRedisService.ttlUntilNext6am());

        return new MissionResDto(null, missionDtoList);
    }
}