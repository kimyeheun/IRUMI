package com.ssafy.pocketc_backend.domain.mission.service;

import com.ssafy.pocketc_backend.domain.mission.client.MissionAiClient;
import com.ssafy.pocketc_backend.domain.mission.dto.request.MissionItem;
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

import static com.ssafy.pocketc_backend.domain.mission.exception.MissionErrorType.*;
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

        if (cached == null) cached = List.of();

        System.out.println("cache  " +  cached.size());
        if (!cached.isEmpty()) {
            System.out.println(1);
            List<MissionDto> missionDtoList = new ArrayList<>();
            for (Mission mission : cached) {
                missionDtoList.add(MissionDto.from(mission));
            }
            return new MissionResDto(true, missionDtoList);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        List<Mission> missions = fetchDailyMissionsFromAi(user)
                .timeout(Duration.ofSeconds(7))
                .doOnSubscribe(s -> System.out.println("[Mission] subscribed"))
                .doOnError(e -> System.out.println("[Mission] error: " + e.getClass() + " - " + e.getMessage()))
                .onErrorResume(e -> Mono.just(List.of()))
                .block();

        List<MissionDto> missionDtoList = new ArrayList<>();
        for (Mission mission : missions) {
            missionDtoList.add(MissionDto.from(mission));
            System.out.println(mission.getMission());
        }

        // Weekly, Monthly 미션 추가
//        List<Mission> weekMonth = missionRepository.findAllByUser_UserId(userId);
//        missions.addAll(weekMonth);

        if (!missions.isEmpty()) {
            missionRedisService.putList(key, missions, Duration.ofSeconds(60));
        }
        getWeeklyMissions(userId);
        return new MissionResDto(false, missionDtoList);
    }

    private Mono<List<Mission>> fetchDailyMissionsFromAi(User user) {
        return missionAiClient.getDailyMissions(user.getUserId())
                .handle((res, sink) -> {
                    // 방어적 null 체크
                    if (res == null || res.data() == null || res.data().missions() == null) {
                        sink.next(List.<Mission>of());
                        sink.complete();
                        return;
                    }
                    if (res.status() != 201) {
                        sink.error(new CustomException(ERROR_GET_MISSIONS));
                        return;
                    }
                    List<Mission> missions = res.data().missions().stream().map(item ->
                            Mission.builder()
                                    .user(user)
                                    .subId(item.subId())
                                    .dsl(item.dsl())
                                    .mission(item.mission())
                                    .type(item.type())
                                    .validFrom(item.validFrom())
                                    .validTo(item.validTo())
                                    .build()
                    ).toList();
                    sink.next(missions);
                    sink.complete();
                });
    }

    private void getWeeklyMissions(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        List<MissionItem> items = missionAiClient.getWeeklyMissions(user.getUserId())
                .map(res -> {
                    if (res == null) return List.<MissionItem>of();
                    if (res.status() != 201) throw new CustomException(ERROR_GET_WEEKLY_MISSIONS);

                    var data = res.data();
                    var missions = (data != null) ? data.missions() : null; // List<MissionItem> 가정
                    return missions == null ? List.<MissionItem>of() : missions;
                })
                .timeout(Duration.ofSeconds(7))
                .doOnError(e -> System.out.println("[Weekly] error: " + e.getMessage()))
                .onErrorReturn(List.<MissionItem>of())
                .blockOptional()
                .orElse(List.of());

        if (items.isEmpty()) return;

        for (MissionItem item : items) {
            missionRepository.save(Mission.builder()
                            .subId(item.subId())
                            .dsl(item.dsl())
                            .user(user)
                            .validFrom(item.validFrom())
                            .validTo(item.validTo())
                            .mission(item.mission())
                            .type(item.type())
                    .build());
        }
    }

    private void getMonthlyMissions(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        List<MissionItem> items = missionAiClient.getMonthlyMissions(user.getUserId())
                .map(res -> {
                    if (res == null) return List.<MissionItem>of();
                    if (res.status() != 201) throw new CustomException(ERROR_GET_MONTHLY_MISSIONS);

                    var data = res.data();
                    var missions = (data != null) ? data.missions() : null; // List<MissionItem> 가정
                    return missions == null ? List.<MissionItem>of() : missions;
                })
                .timeout(Duration.ofSeconds(7))
                .doOnError(e -> System.out.println("[Weekly] error: " + e.getMessage()))
                .onErrorReturn(List.<MissionItem>of())
                .blockOptional()
                .orElse(List.of());

        if (items.isEmpty()) return;

        for (MissionItem item : items) {
            missionRepository.save(Mission.builder()
                    .subId(item.subId())
                    .dsl(item.dsl())
                    .user(user)
                    .validFrom(item.validFrom())
                    .validTo(item.validTo())
                    .mission(item.mission())
                    .type(item.type())
                    .build());
        }
    }
}