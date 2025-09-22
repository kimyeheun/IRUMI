package com.ssafy.pocketc_backend.domain.mission.service;

import com.ssafy.pocketc_backend.domain.mission.client.MissionAiClient;
import com.ssafy.pocketc_backend.domain.mission.dto.response.MissionDto;
import com.ssafy.pocketc_backend.domain.mission.dto.response.MissionResDto;
import com.ssafy.pocketc_backend.domain.mission.entity.Mission;
import com.ssafy.pocketc_backend.domain.mission.repository.MissionRepository;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.ssafy.pocketc_backend.domain.mission.exception.MissionErrorType.ERROR_GET_MISSIONS;
import static com.ssafy.pocketc_backend.domain.user.exception.UserErrorType.NOT_FOUND_MEMBER_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionRedisService missionRedisService;
    private final MissionAiClient missionAiClient;
    private final UserRepository userRepository;

    public MissionResDto getMissions(Integer userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        String key = missionRedisService.buildKey(userId, today);

        List<Mission> cached = missionRedisService.getList(key);

        if (!cached.isEmpty()) {
            List<MissionDto> missionDtoList = new ArrayList<>();
            for (Mission mission : cached) {
                missionDtoList.add(MissionDto.from(mission));
            }
            return new MissionResDto(true, missionDtoList);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        List<Mission> missions = fetchDailyMissionsFromAi(user)
                .timeout(Duration.ofSeconds(3))
                .onErrorReturn(List.of())
                .block();

        List<MissionDto> missionDtoList = new ArrayList<>();
        for (Mission mission : missions) {
            missionDtoList.add(MissionDto.from(mission));
        }

        // Weekly, Monthly 미션 추가
        List<Mission> weekMonth = missionRepository.findAllByUser_UserId(userId);
        missions.addAll(weekMonth);
        missionRedisService.putList(key, missions, missionRedisService.ttlUntilNext6am());

        return new MissionResDto(false, missionDtoList);
    }

    private Mono<List<Mission>> fetchDailyMissionsFromAi(User user) {

        return missionAiClient.getDailyMissions(user.getUserId())
                .handle((res, sink) -> {
                    if (res == null || res.data() == null || res.data().missions() == null) {
                        sink.next(List.<Mission>of());
                        return;
                    }
                    if (res.status() != 201) {
                        sink.error(new CustomException(ERROR_GET_MISSIONS));
                        return;
                    }
                    sink.next(res.data().missions().stream().map(item -> Mission.builder()
                            .user(user)
                            .subId(item.subId())
                            .dsl(item.dsl())
                            .mission(item.mission())
                            .type(item.type())
                            .validFrom(item.validFrom())
                            .validTo(item.validTo())
                            .build()
                    ).toList());
                });
    }
}