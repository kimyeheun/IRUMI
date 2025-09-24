package com.ssafy.pocketc_backend.global.scheduler;

import com.ssafy.pocketc_backend.domain.main.service.MainService;
import com.ssafy.pocketc_backend.domain.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final MissionService missionService;
    private final MainService mainService;
    //매주 월요일 00시 실행
    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
    public void doWeeklyJobs() {
        missionService.assignWeeklyMissions();
    }

    //매달 1일 00시 실행
    @Scheduled(cron = "0 0 0 1 * ?", zone = "Asia/Seoul")
    public void doMonthlyJobs() {
        missionService.assignMonthlyMissions();
    }

    //매일 00시 빈 스트릭 생성
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void createNewStreak() {
        mainService.createEmptyStreak();
    }
}
