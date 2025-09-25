package com.ssafy.pocketc_backend.domain.mission.service;

import com.ssafy.pocketc_backend.domain.mission.client.MissionAiClient;
import com.ssafy.pocketc_backend.domain.mission.dto.request.MissionItem;
import com.ssafy.pocketc_backend.domain.mission.dto.request.MissionRedisDto;
import com.ssafy.pocketc_backend.domain.mission.dto.request.MissionSelectedDto;
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
import java.util.*;

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
        // userId, 오늘 날짜로 미션 조회
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        String key = missionRedisService.buildKey(userId, today);

        List<MissionRedisDto> cached = Optional.ofNullable(missionRedisService.getList(key))
                .orElse(List.of());

        // 조회한 미션이 있으면, missionDTO로 묶어서 보낸다. (일일, 주간, 월간 미션이 포함된다.)
        if (!cached.isEmpty()) {
            List<MissionDto> missionDtoList = new ArrayList<>();
            for (MissionRedisDto m : cached) {
                missionDtoList.add(MissionDto.of(
                        m.getMissionId(),
                        m.getSubId(),
                        m.getType(),
                        m.getMission(),
                        String.valueOf(m.getStatus()),
                        m.getProgress()
                ));
            }
            return new MissionResDto(true, missionDtoList);
        }

        // 미션 없으면 AI 미션 추천
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        List<Mission> missions = fetchDailyMissionsFromAi(user)
                .timeout(Duration.ofSeconds(7))
                .doOnSubscribe(s -> System.out.println("[Mission] subscribed"))
                .doOnError(e -> System.out.println("[Mission] error: " + e.getClass() + " - " + e.getMessage()))
                .onErrorResume(e -> Mono.just(List.of()))
                .block();

        List<MissionDto> missionDtos = new ArrayList<>();
        List<MissionRedisDto> missionRedisDtos = new ArrayList<>();
        int idx = 1;
        for (Mission m : missions) {
            missionDtos.add(MissionDto.of(
                    idx++,
                    m.getSubId(),
                    m.getType(),
                    m.getMission(),
                    String.valueOf(m.getStatus()),
                    0L
            ));
            missionRedisDtos.add(MissionRedisDto.builder()
                            .missionId(idx)
                            .mission(m.getMission())
                            .subId(m.getSubId())
                            .dsl(m.getDsl())
                            .validFrom(m.getValidFrom())
                            .validTo(m.getValidTo())
                            .status(m.getStatus())
                            .type(m.getType())
                            .progress(0L)
                    .build());
        }

        List<Mission> wm = missionRepository.findAllByUser_UserId(userId);
        for (Mission m : wm) {
            missionRedisDtos.add(MissionRedisDto.builder()
                    .missionId(idx++)
                    .mission(m.getMission())
                    .subId(m.getSubId())
                    .dsl(m.getDsl())
                    .validFrom(m.getValidFrom())
                    .validTo(m.getValidTo())
                    .status(m.getStatus())
                    .type(m.getType())
                    .progress(m.getProgress())
                    .build());
        }

        missionRedisService.putList(key, missionRedisDtos, missionRedisService.ttlUntilNext6am());

        return new MissionResDto(false, missionDtos);
    }

    public MissionResDto chooseMissions(MissionSelectedDto dto, Integer userId) {
        Set<Integer> selected = new HashSet<>(dto.selected());

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        String key = missionRedisService.buildKey(userId, today);
        List<MissionRedisDto> cached = Optional.ofNullable(missionRedisService.getList(key))
                .orElse(List.of());

        List<MissionRedisDto> newCached = new ArrayList<>();
        List<MissionDto> missionDtos = new ArrayList<>();
        for (MissionRedisDto m : cached) {
            if (selected.contains(m.getMissionId()) || m.getType() != 0) {
                newCached.add(m);
                missionDtos.add(MissionDto.of(
                        m.getMissionId(),
                        m.getSubId(),
                        m.getType(),
                        m.getMission(),
                        String.valueOf(m.getStatus()),
                        m.getProgress()
                ));
            }
        }

        missionRedisService.putList(key, newCached, missionRedisService.ttlUntilNext6am());
        return new MissionResDto(true, missionDtos);
    }

    private Mono<List<Mission>> fetchDailyMissionsFromAi(User user) {
        return missionAiClient.getDailyMissions(user.getUserId())
                .handle((res, sink) -> {
                    // 방어적 null 체크
                    if (res == null || res.data() == null || res.data().missions() == null) {
                        sink.next(List.of());
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

    //스케줄러용 퍼블릭 메서드
    @Transactional
    public void assignWeeklyMissions() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            getWeeklyMissions(user.getUserId()); // private 메서드 호출
        }
    }

    //스케줄러용 퍼블릭 메서드
    @Transactional
    public void assignMonthlyMissions() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            getMonthlyMissions(user.getUserId());
        }
    }

    public void getWeeklyMissions(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        List<MissionItem> items = missionAiClient.getWeeklyMissions(user.getUserId())
                .map(res -> {
                    if (res == null) return List.<MissionItem>of();
                    if (res.status() != 201) throw new CustomException(ERROR_GET_WEEKLY_MISSIONS);

                    var data = res.data();
                    var missions = (data != null) ? data.missions() : null;
                    return missions == null ? List.<MissionItem>of() : missions;
                })
                .timeout(Duration.ofSeconds(7))
                .doOnError(e -> System.out.println("[Weekly] error: " + e.getMessage()))
                .onErrorReturn(List.of())
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

    public void getMonthlyMissions(Integer userId) {
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
                .onErrorReturn(List.of())
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